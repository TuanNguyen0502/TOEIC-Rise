package com.hcmute.fit.toeicrise.controllers.admin;

import com.hcmute.fit.toeicrise.dtos.requests.UserCreateRequest;
import com.hcmute.fit.toeicrise.services.interfaces.IAccountService;
import com.hcmute.fit.toeicrise.services.interfaces.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("adminUserController")
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserController {
    private final IAccountService accountService;
    private final IUserService userService;

    @PostMapping("")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateRequest userCreateRequest) {
        userService.createUser(userCreateRequest);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> changeUserStatus(@PathVariable("id") Long accountId) {
        accountService.changeAccountStatus(accountId);
        return ResponseEntity.ok().build();
    }
}
