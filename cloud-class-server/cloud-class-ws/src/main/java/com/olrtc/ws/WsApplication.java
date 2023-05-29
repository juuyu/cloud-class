package com.olrtc.ws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

/**
 * @author njy
 * @since 2023/2/1 15:08
 */
@SpringBootApplication
public class WsApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        SpringApplication.run(WsApplication.class, args);
    }
}
