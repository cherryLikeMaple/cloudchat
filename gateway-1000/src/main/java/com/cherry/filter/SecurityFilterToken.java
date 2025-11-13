package com.cherry.filter;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.cherry.constant.RedisKeys;
import com.cherry.exceptions.GraceException;
import com.cherry.grace.result.GraceJSONResult;
import com.cherry.grace.result.ResponseStatusEnum;
import com.cherry.util.RenderErrorUtils;
import com.cherry.utils.IPUtil;
import com.cherry.utils.TokenUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.cherry.grace.result.ResponseStatusEnum.*;

/**
 * @author cherry
 */
@Component
@Slf4j
@RefreshScope
public class SecurityFilterToken implements GlobalFilter, Ordered {

    @Resource
    private RedisTemplate redisTemplate;

    private static final List<String> WHITE = List.of(
            "/passport/login", "/passport/register",
            // 网关剥前缀的情况
            "/v3/api-docs", "/v3/api-docs/**",
            // 带服务前缀的情况
            "/**/v3/api-docs",                 
            "/swagger-ui.html", "/swagger-ui/**",
            "/actuator/**", "/static/**", "/doc.html", "/**/doc.html",
            // 需要前缀时
            "/auth-service/v3/api-docs"       
    );


    // 路径匹配规则器
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String url = exchange.getRequest().getURI().getPath();
        for (String white : WHITE) {
            //如果匹配到, 放行
            if (antPathMatcher.match(white, url)) {
                return chain.filter(exchange);
            }
        }
        
        //到达此处, 说明代码被拦截了
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        String auth = headers.getFirst("Authorization");
        if (auth == null) {
            return RenderErrorUtils.display(exchange, ResponseStatusEnum.UN_LOGIN);
        }
        if (auth.startsWith("Bearer ")) {
            auth = auth.substring(7);
        }
        String token = auth.trim();
        String tokenKey = RedisKeys.LOGIN_TOKEN + token;
        String userId = (String) redisTemplate.opsForValue().get(tokenKey);
        if (userId == null) {
            GraceException.display(USER_NOT_EXIST_ERROR);
        } else {
            return chain.filter(exchange);
        }
        
        return RenderErrorUtils.display(exchange, ResponseStatusEnum.UN_LOGIN);
    }
    

    @Override
    public int getOrder() {
        return 1;
    }

}
