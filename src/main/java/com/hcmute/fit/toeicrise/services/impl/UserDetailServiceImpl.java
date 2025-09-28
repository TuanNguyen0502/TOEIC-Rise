package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByAccount_Email(username)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        return new org.springframework.security.core.userdetails.User(
                user.getAccount().getEmail(),
                user.getAccount().getPassword(),
                getAuthorities(user)
        );
    }

    public Collection<? extends GrantedAuthority> getAuthorities(User user) {
        String role = "ROLE_" + user.getRole().getName().name();
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }
}
