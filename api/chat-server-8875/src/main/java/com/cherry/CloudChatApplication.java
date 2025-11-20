package com.cherry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author cherry
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class CloudChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudChatApplication.class, args);
    }
}
