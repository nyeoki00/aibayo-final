package com.aibayo.aibayo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/main")
public class MainController {
    @GetMapping("/admin")
    public String adminMain() {
        return "/admin/main";
    }

    @GetMapping("/user")
    public String userMain() {
        return "/user/main";
    }
}
