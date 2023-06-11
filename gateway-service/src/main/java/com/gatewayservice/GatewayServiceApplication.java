package com.gatewayservice;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayServiceApplication {
    public static void main(String[] args) {
//        SpringApplication.run(ConfigServiceApplication.class, args);
        new SpringApplicationBuilder(GatewayServiceApplication.class).run(args);
    }
}
