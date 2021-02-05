package com.mip.sharebnb.security.service;

import com.mip.sharebnb.model.Member;

import com.mip.sharebnb.repository.MemberRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email + " -> 데이터베이스에서 찾을 수 없습니다"));

        memberId = member.getId();

        return createUser(member.getEmail(), member);
    }

    private User createUser(String email, Member member) {
        if (member.isDeleted()) {
            throw new RuntimeException(email + " -> 탈퇴한 멤버의 email 입니다");
        }

        List<GrantedAuthority> grantedAuthorities = Stream.of(member.getRole())
                .map(r -> new SimpleGrantedAuthority(r.getRoleName()))
                .collect(Collectors.toList());

        return new User(member.getEmail(),
                member.getPassword(),
                grantedAuthorities);
    }

}
