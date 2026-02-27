package com.dine.backend.security;

import com.dine.backend.entity.Account;
import com.dine.backend.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountUserDetailsService implements UserDetailsService {

    private final AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountService.findByEmail(email);
        if (account == null) {
            throw new UsernameNotFoundException("Account not found with email: " + email);
        }
        return new AccountUserDetails(account);
    }

    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        Account account = accountService.getById(id);
        if (account == null) {
            throw new UsernameNotFoundException("Account not found with id: " + id);
        }
        return new AccountUserDetails(account);
    }
}
