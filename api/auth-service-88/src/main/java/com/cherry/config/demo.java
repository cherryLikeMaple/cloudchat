package com.cherry.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class demo {
    @Bean
    ApplicationRunner checkBeans(ConfigurableApplicationContext ctx) {
        return args -> {
            String[] discoveryClients = ctx.getBeanNamesForType(org.springframework.cloud.client.discovery.DiscoveryClient.class);
            String[] serviceRegistrys = ctx.getBeanNamesForType(org.springframework.cloud.client.serviceregistry.ServiceRegistry.class);
            System.out.println("DiscoveryClient beans: " + Arrays.toString(discoveryClients));
            System.out.println("ServiceRegistry beans: " + Arrays.toString(serviceRegistrys));
        };
    }
}
