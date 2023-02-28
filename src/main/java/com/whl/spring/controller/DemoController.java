package com.whl.spring.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @GetMapping(value = {"", "/"})
    public String index() {
        return "Demo";
    }

    @GetMapping("/test")
    public Map<String, Object> test() {
        Map<String, Object> data = new HashMap<>();
        data.put("a", "a");
        data.put("b", Long.MAX_VALUE);
        data.put("c", System.currentTimeMillis());
        return data;
    }

}
