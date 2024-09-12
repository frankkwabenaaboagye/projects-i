package com.frankaboagye.connecthub.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ConnectHubController {

    @GetMapping("/myTest")
    public String welcome(@RequestParam(name = "theName", required = false, defaultValue = "frankTest") String theName, Model model){
        model.addAttribute("theName", theName);
        return "welcome";
    }
}
