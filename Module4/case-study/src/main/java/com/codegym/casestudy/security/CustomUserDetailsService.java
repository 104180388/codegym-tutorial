package com.codegym.casestudy.security;

import com.codegym.casestudy.model.entity.Account;
import com.codegym.casestudy.repository.AccountRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    public CustomUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username)
                .orElseGet(() -> accountRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Tài khoản không tồn tại với tên đăng nhập hoặc email: " + username)));

        if (!account.isActive()) {
            throw new UsernameNotFoundException("Tài khoản đã bị tạm khóa: " + username);
        }

        return new CustomUserDetails(account);
    }
}
