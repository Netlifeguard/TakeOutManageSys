package com.example.reggietout;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.example.reggietout.mapper")
@ServletComponentScan
@EnableTransactionManagement
public class ReggieToutApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReggieToutApplication.class, args);
    }

}
