package com.sweetypie.sweetypie.service;

import com.sweetypie.sweetypie.dto.GoogleMemberDto;
import com.sweetypie.sweetypie.dto.MemberDto;
import com.sweetypie.sweetypie.exception.DataNotFoundException;
import com.sweetypie.sweetypie.exception.DuplicateValueExeption;
import com.sweetypie.sweetypie.exception.InputNotValidException;
import com.sweetypie.sweetypie.model.Member;
import com.sweetypie.sweetypie.model.MemberRole;
import com.sweetypie.sweetypie.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member signup(MemberDto memberDto) {
        checkDuplicateEmail(memberDto.getEmail());
        confirmPassword(memberDto);

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

    public Member signupGoogleMember(GoogleMemberDto memberDto) {
        checkDuplicateEmail(memberDto.getEmail());

        Member member = Member.builder()
                .email(memberDto.getEmail())
                .password(passwordEncoder.encode(memberDto.getSocialId()))
                .name(memberDto.getName())
                .birthDate(memberDto.getBirthDate())
                .contact(memberDto.getContact())
                .role(MemberRole.MEMBER)
                .isSocialMember(true)
                .build();

        return memberRepository.save(member);
    }

    public Member getMember(Long id) {
        return memberRepository
                .findById(id)
                .orElseThrow(() -> new DataNotFoundException("조회하는 멤버가 존재하지 않습니다"));
    }

    public void updateMember(Long id, MemberDto memberDto)  {

        Member member = memberRepository
                .findById(id)
                .orElseThrow(() -> new DataNotFoundException("수정할 멤버가 존재하지 않습니다"));

        if (memberDto.getName() != null) {
            member.setName(memberDto.getName());
        } else if (memberDto.getBirthDate() != null) {
            member.setBirthDate(memberDto.getBirthDate());
        } else if (memberDto.getContact() != null) {
            member.setContact(memberDto.getContact());
        } else if (memberDto.getPassword() != null) {
//            if (!passwordEncoder.matches(memberDto.getPrePassword(), member.getPassword())) {
//                throw new InputNotValidException("이전 비밀번호가 일치하지 않습니다");
//            } // 추후 구현
            member.setPassword(passwordEncoder.encode(memberDto.getPassword()));
        }

        memberRepository.save(member);
    }

    public Member withdrawal(Long id) {
        Member member = memberRepository
                .findById(id)
                .orElseThrow(() -> new DataNotFoundException("탈퇴할 멤버가 존재하지 않습니다"));

        member.setDeleted(true);
        return memberRepository.save(member);
    }

    private void checkDuplicateEmail(String email) {
        if (memberRepository.findMemberIncludeDeletedMember(email).orElse(null) != null) {
            throw new DuplicateValueExeption("이미 사용되고 있는 Email 입니다.");
        }
    }

    private void confirmPassword(MemberDto memberDto)  {
        if(!memberDto.getPassword().equals(memberDto.getPasswordConfirm())){
            throw new InputNotValidException("비밀 번호 확인이 일치하지 않습니다.");
        }
    }

}
