package com.cherry;

import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author cherry
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class CloudChatApplication {
    

    public static void main(String[] args) {
        SpringApplication.run(CloudChatApplication.class, args);
    }
}
