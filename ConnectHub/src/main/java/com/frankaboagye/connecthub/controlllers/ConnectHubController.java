package com.frankaboagye.connecthub.controlllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ConnectHubController {

    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }
}
