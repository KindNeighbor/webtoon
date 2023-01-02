package com.example.webtoon.security;

import com.example.webtoon.entity.User;
import com.example.webtoon.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        // email 로 로그인
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> 
                        new UsernameNotFoundException("User not found with username or email : " + email)
        );

        return UserPrincipal.create(user);
    }

    // JWTAuthenticationFilter 에서 사용
    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
            () -> new UsernameNotFoundException("User not found with id : " + id)
        );

        return UserPrincipal.create(user);
    }
}