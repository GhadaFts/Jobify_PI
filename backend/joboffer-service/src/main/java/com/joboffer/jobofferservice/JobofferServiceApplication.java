package com.joboffer.jobofferservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class JobofferServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobofferServiceApplication.class, args);
    }

}
