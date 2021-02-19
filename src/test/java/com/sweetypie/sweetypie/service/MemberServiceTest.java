package com.sweetypie.sweetypie.service;

import com.sweetypie.sweetypie.dto.MemberDto;
import com.sweetypie.sweetypie.model.Member;
import com.sweetypie.sweetypie.model.MemberRole;
import com.sweetypie.sweetypie.repository.MemberRepository;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @DisplayName("회원정보검색")
    @Test
    void getMemberTest() {
        Member member = Member.builder()
                .id(1L)
                .email("member@mail.com")
                .build();

        when(memberRepository.findById(1L))
                .thenReturn(Optional.ofNullable(member));

        Member result = memberService.getMember(1L);

        assertThat(result.getEmail()).isEqualTo("member@mail.com");
    }

    @DisplayName("회원가입")
    @Test
    void signupTest() {

        Member member = Member.builder()
                .email("member@mail.com")
                .name("member")
                .birthDate(LocalDate.now())
                .contact("011-111-1111")
                .role(MemberRole.MEMBER)
                .build();

        when(memberRepository.save(member))
                .thenReturn(member);

        MemberDto memberDto = MemberDto.builder()
                .email("member@mail.com")
                .password("member")
                .name("member")
                .birthDate(LocalDate.now())
                .contact("011-111-1111")
                .build();

        Member result = memberService.signup(memberDto);

        assertThat(result.getEmail()).isEqualTo("member@mail.com");
    }

    @DisplayName("회원정보수정")
    @Test
    void updateMemberTest() throws InvalidInputException {
        LocalDate date = LocalDate.now();
        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(new Member()));

        MemberDto memberDto = MemberDto.builder()
                .birthDate(date)
                .build();

        memberService.updateMember(1L, memberDto);

        verify(memberRepository, times(1)).findById(1L);
    }

    @DisplayName("회원탈퇴")
    @Test
    void withdrawalTest() {
        Member member = Member.builder()
                .email("test@mail.com")
                .build();

        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(member));

        when(memberRepository.save(member))
                .thenReturn(member);

        Member result = memberService.withdrawal(1L);

        assertThat(result.isDeleted()).isEqualTo(true);
    }
}