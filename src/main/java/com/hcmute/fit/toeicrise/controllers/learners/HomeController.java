package com.hcmute.fit.toeicrise.controllers.learners;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/learner/home")
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "Welcome to the Learner Home Page!";
    }
}
