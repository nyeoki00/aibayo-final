package com.aico.aibayo.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/kid")
public class KidController {
    @GetMapping("/list")
    public String list() {
        return "/kid/list";
    }

    @GetMapping("/detail")
    public String detail() {
        return "/kid/detail";
    }

    @GetMapping("/user/detail")
    public String userDetail() {
        return "/kid/user_detail";
    }

    @GetMapping("/write")
    public String writeForm() {
        return "/kid/writeForm";
    }
}
