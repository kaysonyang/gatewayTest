package com.wsjia.ga;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Created by a on 2016/8/4.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class App {
    public static void main(String[] args){
        SpringApplication.run(App.class, args);
    }
}
