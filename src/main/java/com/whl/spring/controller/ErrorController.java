package com.whl.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/404")
    public String error404() {
        return "404";
    }

    @GetMapping("/500")
    public String error500() {
        return "500";
    }

}
