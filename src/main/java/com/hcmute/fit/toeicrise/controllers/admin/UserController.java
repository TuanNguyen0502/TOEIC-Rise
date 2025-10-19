package com.hcmute.fit.toeicrise.controllers.admin;

import com.hcmute.fit.toeicrise.dtos.requests.UserCreateRequest;
import com.hcmute.fit.toeicrise.services.interfaces.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("adminUserController")
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    @PostMapping("")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateRequest userCreateRequest) {
        userService.createUser(userCreateRequest);
        return ResponseEntity.ok().build();
    }
}
