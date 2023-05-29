package com.olrtc.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

/**
 * @author njy
 * @since 2023/2/16 14:00
 */
@SpringBootApplication
public class BootApplication {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        SpringApplication.run(BootApplication.class, args);
    }
}
