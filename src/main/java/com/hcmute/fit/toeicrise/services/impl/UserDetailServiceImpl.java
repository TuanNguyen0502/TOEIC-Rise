package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.models.entities.Account;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.repositories.AccountRepository;
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
    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(username).orElse(null);
        assert account != null;
        User user = account.getUser();
        return new org.springframework.security.core.userdetails.User(account.getEmail(), account.getPassword(), getAuthorities(user));
    }

    public Collection<? extends GrantedAuthority> getAuthorities(User user) {
        String role = "ROLE_" + user.getRole().getName().name();
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }
}
