package com.example.jungleroyal.common.util;

import com.example.jungleroyal.common.types.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final Long userId;      // 서버 회원 번호
    private final String username;  // 유저 닉네임
    private final UserRole userRole; // 유저 역할

    private final Collection<? extends GrantedAuthority> authorities;


    public CustomUserDetails(Long userId, String username, UserRole userRole, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.userRole = userRole;
        this.authorities = authorities;
    }

    public UserRole getUserRole() {
        return userRole;
    }
    @Override
    public String getPassword() {
        return null;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public String getUsername() {
        return username;
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
