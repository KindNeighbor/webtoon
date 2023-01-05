package com.example.webtoon.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {

    private String email;
    private String username;
    private String password;
    private String nickname;
}