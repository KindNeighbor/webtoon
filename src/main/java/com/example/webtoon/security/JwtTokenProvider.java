package com.example.webtoon.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider { // Jwt 토큰 생성 및 유효성 검증 컴포넌트


    @Value("${jwt.secret}") // yml 에 저장
    private String jwtSecret;

    @Value("${jwt.token-validity-in-seconds}") //유효기간
    private int jwtExpirationInMs; 

    //Jwt 토큰 생성
    public String generateToken(Authentication authentication) {

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs); // 만기 날짜

        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId())) // 데이터
                .setIssuedAt(new Date()) //토큰 발행 일자
                .setExpiration(expiryDate) //만기 기간
                .signWith(SignatureAlgorithm.HS512, jwtSecret) // 암호화 알고리즘, secret 값 세팅
                .compact();
    }

    // Jwt 토큰에서 회원구별 정보 추출
    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    // Jwt 토큰의 유효성 확인
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }
}