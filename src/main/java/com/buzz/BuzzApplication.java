package com.buzz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BuzzApplication {

    public static void main(String[] args) {
        SpringApplication.run(BuzzApplication.class, args);
    }
}