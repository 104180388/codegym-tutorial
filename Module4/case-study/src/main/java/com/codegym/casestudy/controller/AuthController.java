package com.codegym.casestudy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/doctors")
    public String doctors() {
        return "doctors";
    }

    @GetMapping("/services")
    public String services() {
        return "services";
    }
}
