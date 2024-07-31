package com.aico.aibayo.control;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping
public class TeacherController {

    @GetMapping("/admin/teacherMain")
    public String adminTeacherMain() {
        return "/admin/teacher/teacherMain";
    }

    @GetMapping("/admin/teacherProfileAccept")
    public String adminTeacherProfileAccept() {
        return "/admin/teacher/teacherProfileAccept";
    }

    @GetMapping("/admin/teacherProfileWait")
    public String adminTeacherProfileWait() {
        return "/admin/teacher/teacherProfileWait";
    }



}
