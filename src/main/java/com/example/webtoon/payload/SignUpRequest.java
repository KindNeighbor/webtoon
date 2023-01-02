package com.example.webtoon.payload;

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