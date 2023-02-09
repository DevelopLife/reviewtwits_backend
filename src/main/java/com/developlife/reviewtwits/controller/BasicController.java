package com.developlife.reviewtwits.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class BasicController {

    @RequestMapping
    public String helloWorld(){
        return "hello world!";
    }
}
