package com.java.xdd.springboot.main;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableAutoConfiguration
@RestController
public class TestController {

    @RequestMapping("/hello2")
    public String hello() {
        return "Hello World!2";
    }
}
