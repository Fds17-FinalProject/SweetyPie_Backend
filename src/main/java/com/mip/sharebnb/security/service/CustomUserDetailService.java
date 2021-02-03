package com.mip.sharebnb.security.service;

import com.mip.sharebnb.model.Member;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomUserDetailService  {

    private User createUser(String username, Member member) {
        if (member.isDeleted()) {
            throw new RuntimeException(username + " -> 탈퇴한 멤버입니다");
        }

        List<GrantedAuthority> grantedAuthorities = Stream.of(member.getRole())
                .map(r -> new SimpleGrantedAuthority(r.getRoleName()))
                .collect(Collectors.toList());

        return new User(member.getEmail(),
                member.getPassword(),
                grantedAuthorities);
    }
}
