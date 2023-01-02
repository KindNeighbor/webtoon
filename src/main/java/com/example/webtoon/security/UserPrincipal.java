package com.example.webtoon.security;

import com.example.webtoon.entity.User;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails {

    private Long userId;

    private String email;

    private String username;

    private String password;

    private String nickname;

    private Collection<? extends GrantedAuthority> authorities; //계정이 갖고 있는 권한 목록

    public UserPrincipal(Long userId, String username,String nickname, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.authorities = authorities;
    }

    public UserPrincipal(Long userId, String email, String username, String password, String nickname, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.authorities = authorities;
    }

    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream().map(role ->
                new SimpleGrantedAuthority(role.getName().name())
        ).collect(Collectors.toList());

        return new UserPrincipal(
                user.getUserId(),
                user.getEmail(),
                user.getUsername(),
                user.getPassword(),
                user.getNickname(),
                authorities
        );
    }

    public Long getId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}