package com.example.webtoon.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Target({ElementType.PARAMETER, ElementType.TYPE}) // 어노테이션이 적용할 위치 : 타입 선언, 매개 변수 선언 시
@Retention(RetentionPolicy.RUNTIME) // 어노테이션의 범위 : 컴파일 이후에도 JVM에 의해서 참조 가능
@AuthenticationPrincipal // 현재 인증 유저에 접근
public @interface CurrentUser { // custom annotation : @interface

} 