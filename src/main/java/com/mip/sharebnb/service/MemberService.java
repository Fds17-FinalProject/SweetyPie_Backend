package com.mip.sharebnb.service;

import com.mip.sharebnb.dto.LoginDto;
import com.mip.sharebnb.dto.MemberDto;
import com.mip.sharebnb.exception.PrePasswordNotMatchedException;
import com.mip.sharebnb.model.Member;
import com.mip.sharebnb.model.MemberRole;
import com.mip.sharebnb.repository.MemberRepository;
import com.mip.sharebnb.security.jwt.TokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class MemberService {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(TokenProvider tokenProvider,
                         AuthenticationManagerBuilder authenticationManagerBuilder,
                         MemberRepository memberRepository,
                         PasswordEncoder passwordEncoder) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Member signup(MemberDto memberDto) {
        checkDuplicateEmail(memberDto);

        Member member = Member.builder()
                .email(memberDto.getEmail())
                .password(passwordEncoder.encode(memberDto.getPassword()))
                .name(memberDto.getName())
                .birthDate(memberDto.getBirthDate())
                .contact(memberDto.getContact())
                .role(MemberRole.MEMBER)
                .build();

        return memberRepository.save(member);
    }

    @Transactional
    public Member getMember(Long id) {
        return memberRepository
                .findById(id)
                .orElseThrow(() ->new UsernameNotFoundException("찾는 멤버가 없습니다"));
    }

    @Transactional
    public Member updateMember(Long id, MemberDto memberDto) {

        Member member = memberRepository
                .findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("찾는 멤버가 없습니다"));

        if (memberDto.getEmail() != null) {
            checkDuplicateEmail(memberDto);
            member.setEmail(memberDto.getEmail());
        } else if (memberDto.getName() != null) {
            member.setName(memberDto.getName());
        } else if (memberDto.getBirthDate() != null) {
            member.setBirthDate(memberDto.getBirthDate());
        } else if (memberDto.getContact() != null) {
            member.setContact(memberDto.getContact());
        } else if (memberDto.getPassword() != null && memberDto.getPrePassword() != null) {
           if(!passwordEncoder.matches(memberDto.getPrePassword(), member.getPassword())) {
               throw new PrePasswordNotMatchedException("이전 비밀번호가 일치하지 않습니다");
           }
            member.setPassword(passwordEncoder.encode(memberDto.getPassword()));
        }

        return memberRepository.save(member);
    }

    @Transactional
    public Member withdrawal(Long id) {
        Member member = memberRepository
                .findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("찾는 멤버가 없습니다"));

        member.setDeleted(true);
        return memberRepository.save(member);
    }

    private void checkDuplicateEmail(MemberDto memberDto) {
        if (memberRepository.findByEmail(memberDto.getEmail()).orElse(null) != null) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }
    }

    public String authorize(LoginDto loginDto) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return tokenProvider.createToken(authentication);
    }
}
