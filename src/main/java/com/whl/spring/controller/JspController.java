package com.whl.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/jsp")
public class JspController {

    @GetMapping(value = {"", "/"})
    public String index() {
        return "test";
    }

}
