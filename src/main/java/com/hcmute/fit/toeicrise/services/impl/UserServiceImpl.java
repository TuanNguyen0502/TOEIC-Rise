package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.UserCreateRequest;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Account;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.enums.EAuthProvider;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.repositories.AccountRepository;
import com.hcmute.fit.toeicrise.repositories.RoleRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createUser(UserCreateRequest request) {
        // Check for duplicate email
        Account existingAccount = accountRepository.findByEmail(request.getEmail()).orElse(null);
        if (existingAccount != null) {
            throw new AppException(ErrorCode.DUPLICATE_EMAIL);
        }

        // Validate password and confirm password match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }

        // Create Account entity
        Account account = new Account();
        account.setEmail(request.getEmail());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setIsActive(true);
        account.setAuthProvider(EAuthProvider.LOCAL);
        // Create associated User entity
        User user = new User();
        user.setRole(roleRepository.findByName(request.getRole()));
        user.setAccount(account);
        user.setFullName(request.getFullName());
        user.setGender(request.getGender());
        // Avatar handling can be added here if needed

        // Link the User entity to the Account
        account.setUser(user);
        accountRepository.save(account);
    }
}
