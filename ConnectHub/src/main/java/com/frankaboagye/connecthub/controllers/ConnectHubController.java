package com.frankaboagye.connecthub.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ConnectHubController {

    @GetMapping("/welcome")
    public String welcome(Model model){
        return "welcome";
    }
}
