package com.hcmute.fit.toeicrise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ToeicRiseApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToeicRiseApplication.class, args);
    }

}
