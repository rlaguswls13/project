package com.example.retrospective;

import com.example.retrospective.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan(basePackageClasses = AppProperties.class)
public class RetrospectiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(RetrospectiveApplication.class, args);
    }
}