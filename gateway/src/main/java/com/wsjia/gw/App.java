package com.wsjia.gw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by a on 2016/8/2.
 */
@SpringBootApplication
@ComponentScan("com")
public class App {
    public static void main (String[] args) {
        SpringApplication.run(App.class, args);
    }
}
