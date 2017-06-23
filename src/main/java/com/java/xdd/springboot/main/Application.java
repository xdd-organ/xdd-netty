package com.java.xdd.springboot.main;

import org.springframework.boot.SpringApplication;

public class Application {

    public static void main(String[] args) {
        //SpringApplication.run(Application.class, args);
        Object[] arr = new Object[2];
        arr[0] = TestController.class;
        arr[1] = Test2Controller.class;
        SpringApplication.run(arr, args);
    }
}
