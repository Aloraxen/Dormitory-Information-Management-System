package com.cxt.dormitory.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 无权限访问提示页
 */
@Controller
public class ErrorPageController {

    @GetMapping("/403")
    public String forbidden() {
        return "error/403";
    }
}
