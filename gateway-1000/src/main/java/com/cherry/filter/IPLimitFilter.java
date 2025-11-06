package com.cherry.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author cherry
 */
@Component
@Slf4j
public class IPLimitFilter implements GatewayFilter, Ordered {

    /**
     * 需求:
     * 判断某个请求的ip在20秒内的请求次数是否超过3次
     * 如果超过3次, 限制访问30秒
     * 等待30秒后, 才能够继续恢复访问.
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        
        // 默认放行后面的服务
        return chain.filter(exchange);
    }
    
    

    @Override
    public int getOrder() {
        return 1;
    }

}
