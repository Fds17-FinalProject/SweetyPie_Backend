package com.sweetypie.sweetypie.security.service;

import com.sweetypie.sweetypie.exception.DataNotFoundException;
import com.sweetypie.sweetypie.model.Member;

import com.sweetypie.sweetypie.repository.MemberRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public static Long memberId;

    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("멤버가 존재하지 않습니다"));

        memberId = member.getId();

        return createUser(member);
    }

    private User createUser(Member member) {

        List<GrantedAuthority> grantedAuthorities = Stream.of(member.getRole())
                .map(r -> new SimpleGrantedAuthority(r.getRoleName()))
                .collect(Collectors.toList());

        return new User(member.getEmail(),
                member.getPassword(),
                grantedAuthorities);
    }

}
