package com.example.webtoon.entity;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Table(name = "users")
@Entity
@Getter
@Setter
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public User() {

    }

    public User(String email, String username, String password, String nickname) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }
}