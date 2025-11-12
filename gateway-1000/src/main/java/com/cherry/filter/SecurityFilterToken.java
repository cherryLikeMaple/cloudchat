package com.cherry.filter;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.cherry.grace.result.GraceJSONResult;
import com.cherry.grace.result.ResponseStatusEnum;
import com.cherry.utils.IPUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @author cherry
 */
@Component
@Slf4j
@RefreshScope
public class IPLimitFilter implements GlobalFilter, Ordered {

    @Resource
    private RedisTemplate redisTemplate;

    @Value("${blackIp.continueCounts}")
    private Integer continueCounts;
    @Value("${blackIp.timeInterval}")
    private Integer timeInterval;
    @Value("${blackIp.limitTimes}")
    private Integer limitTimes;

    /**
     * 需求:
     * 判断某个请求的ip在20秒内的请求次数是否超过3次
     * 如果超过3次, 限制访问30秒
     * 等待30秒后, 才能够继续恢复访问.
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return doLimit(exchange, chain);
    }

    public Mono<Void> doLimit(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.从请求头中得到IP
        ServerHttpRequest request = exchange.getRequest();
        String ip = IPUtil.getIP(request);

        // 2.设置redis的key
        final String ipRedisKey = "gateway-ip" + ip;
        final String ipRedisLimited = "gateway-ip:limit" + ip;

        // 3.判断ip是不是在请求中
        Long expire = redisTemplate.getExpire(ipRedisLimited);
        if (expire > 0) {
            return renderErrorMsg(exchange, ResponseStatusEnum.SYSTEM_ERROR_BLACK_IP);
        }

        // 4.统计redis中的ip次数
        Long requestCounts = redisTemplate.opsForValue().increment(ipRedisKey);
        if (requestCounts == 1) {
            redisTemplate.expire(ipRedisKey, timeInterval, TimeUnit.SECONDS);
        }

        // 5.如果请求达到设置的次数, 进入黑名单
        if (requestCounts > continueCounts) {
            redisTemplate.opsForValue().set(ipRedisLimited, limitTimes);
            return renderErrorMsg(exchange, ResponseStatusEnum.SYSTEM_ERROR_BLACK_IP);
        }

        return chain.filter(exchange);
    }

    /**
     * 重新包装并返回错误信息
     *
     * @param exchange
     * @param responseStatusEnum
     * @return
     */
    public Mono<Void> renderErrorMsg(ServerWebExchange exchange, ResponseStatusEnum responseStatusEnum) {
        // 1. 获得相应的response
        ServerHttpResponse response = exchange.getResponse();
        // 2. 构建jsonResult
        GraceJSONResult jsonResult = GraceJSONResult.exception(responseStatusEnum);
        // 3. 设置header类型
        if (!response.getHeaders().containsKey("Content-Type")) {
            response.getHeaders().add("Content-Type", MimeTypeUtils.APPLICATION_JSON_VALUE);
        }
        // 4.修改response的状态码code为500
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        String resultJson = new Gson().toJson(jsonResult);
        DataBuffer buffer = response.bufferFactory().wrap(resultJson.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return 1;
    }

}
