package com.zhenlong.darwinmall.testssoclient.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HelloController {

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/employees")
    public String employees(Model model) {
        List<String> employees = new ArrayList<>();
        employees.add("张三");
        employees.add("李四");
        model.addAttribute("emps", employees);
        return "list";
    }
}
