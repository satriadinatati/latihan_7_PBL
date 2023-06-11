package com.configservice;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigServiceApplication  {
    public static void main(String[] args) {
//        SpringApplication.run(ConfigServiceApplication.class, args);
        new SpringApplicationBuilder(ConfigServiceApplication.class).run(args);
    }


}
