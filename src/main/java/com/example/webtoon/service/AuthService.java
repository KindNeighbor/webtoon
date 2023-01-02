package com.example.webtoon.service;

import com.example.webtoon.entity.Role;
import com.example.webtoon.entity.RoleName;
import com.example.webtoon.entity.User;
import com.example.webtoon.payload.ApiResponse;
import com.example.webtoon.payload.JwtAuthenticationResponse;
import com.example.webtoon.payload.LoginRequest;
import com.example.webtoon.payload.ResponseMessage;
import com.example.webtoon.payload.SignUpRequest;
import com.example.webtoon.payload.StatusCode;
import com.example.webtoon.repository.RoleRepository;
import com.example.webtoon.repository.UserRepository;
import com.example.webtoon.security.JwtTokenProvider;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // 로그인
    public ResponseEntity<?> signIn(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    // 회원가입
    public ResponseEntity<?> signUp(SignUpRequest signUpRequest) {

        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.ok(new ApiResponse(
                StatusCode.BAD_REQUEST, ResponseMessage.ALREADY_EXISTED_EMAIL));
        }

        if(userRepository.existsByNickname(signUpRequest.getNickname())) {
            return ResponseEntity.ok(new ApiResponse(
                StatusCode.BAD_REQUEST, ResponseMessage.ALREADY_EXISTED_NICKNAME));
        }

        // Creating user's account
        User user = new User(signUpRequest.getEmail(), signUpRequest.getUsername(),
            signUpRequest.getPassword(), signUpRequest.getNickname());

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
            .orElseThrow(() -> new RuntimeException());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(userRole));

        User result = userRepository.save(user);

        return ResponseEntity.ok(new ApiResponse(
            StatusCode.OK, ResponseMessage.CREATED_USER, result));
    }
}
