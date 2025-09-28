package com.hcmute.fit.toeicrise.controllers.learner;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/learner")
@RequiredArgsConstructor
public class HomeController {
    @GetMapping("")
    public String home() {
        return "Welcome to the Learner Home Page!";
    }
}
