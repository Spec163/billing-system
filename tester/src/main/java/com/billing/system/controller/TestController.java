package com.billing.system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tester/")
public class TestController {

    @GetMapping("aloha")
    public String getMain() {
        return String.format("Hello main");
    }
}
