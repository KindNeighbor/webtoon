package com.example.webtoon.entity;

import com.example.webtoon.type.RoleName;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Size(max = 50)
    @Column(length = 50, unique = true)
    private String email;

    @Size(max = 20)
    private String username;

    @Size(max = 100)
    private String password;

    @Size(max = 20)
    @Column(length = 50, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private RoleName role;

    public User(String email, String username, String password, String nickname) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }
}