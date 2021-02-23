package com.sweetypie.sweetypie.service;

import com.sweetypie.sweetypie.dto.GoogleMemberDto;
import com.sweetypie.sweetypie.dto.MemberDto;
import com.sweetypie.sweetypie.exception.DataNotFoundException;
import com.sweetypie.sweetypie.exception.DuplicateValueExeption;
import com.sweetypie.sweetypie.exception.InputNotValidException;
import com.sweetypie.sweetypie.model.Member;
import com.sweetypie.sweetypie.model.MemberRole;
import com.sweetypie.sweetypie.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @DisplayName("회원정보검색-성공")
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

    @DisplayName("회원정보검색-실패-찾을회원이없음")
    @Test
    void getMemberFailTest() {

        when(memberRepository.findById(1L))
                .thenThrow(new DataNotFoundException("찾을 회원이 없습니다"));

        DataNotFoundException ex = assertThrows(DataNotFoundException.class, () -> memberService.getMember(1L));

        assertThat(ex.getMessage().contains("찾을 회원이 없습니다")).isTrue();
    }

    @DisplayName("회원가입-성공")
    @Test
    void signupTest() {

        Member member = Member.builder()
                .email("member@mail.com")
                .name("회원")
                .birthDate(LocalDate.now())
                .contact("01121111111")
                .role(MemberRole.MEMBER)
                .build();

        when(memberRepository.save(member))
                .thenReturn(member);

        MemberDto memberDto = MemberDto.builder()
                .email("member@mail.com")
                .password("member123!")
                .passwordConfirm("member123!")
                .name("회원")
                .birthDate(LocalDate.now())
                .contact("01121111111")
                .build();

        Member result = memberService.signup(memberDto);

        assertThat(result.getEmail()).isEqualTo("member@mail.com");
    }

    @DisplayName("회원가입-실패-중복된이메일")
    @Test
    void signupDuplicatedEmailFailTest() {

        MemberDto memberDto = MemberDto.builder()
                .email("test123@gmail.com")
                .password("member123!")
                .passwordConfirm("member123!")
                .name("회원")
                .birthDate(LocalDate.now())
                .contact("01111211111")
                .build();

        when(memberService.signup(memberDto))
                .thenThrow(new DuplicateValueExeption("이미 사용되고 있는 Email입니다"));

        DuplicateValueExeption ex = assertThrows(DuplicateValueExeption.class, () -> memberService.signup(memberDto));

        assertThat(ex.getMessage().contains("이미 사용되고 있는 Email입니다")).isTrue();

    }

    @DisplayName("회원가입-실패-비밀번호확인불일치")
    @Test
    void signupPasswordConfirmFailTest() {

        MemberDto memberDto = MemberDto.builder()
                .email("test@mail.com")
                .password("회원!")
                .passwordConfirm("worngpassword")
                .name("member")
                .birthDate(LocalDate.now())
                .contact("011-111-1111")
                .build();

        InputNotValidException ex = assertThrows(InputNotValidException.class, () -> memberService.signup(memberDto));

        assertThat(ex.getMessage().contains("비밀 번호 확인이 일치하지 않습니다.")).isTrue();

    }

    @DisplayName("구글회원가입-성공")
    @Test
    void signupGoogleMemberTest() {

        Member member = Member.builder()
                .email("googlemember@gmail.com")
                .name("회원")
                .birthDate(LocalDate.now())
                .contact("01111121111")
                .role(MemberRole.MEMBER)
                .isSocialMember(true)
                .build();

        when(memberRepository.save(member))
                .thenReturn(member);

        GoogleMemberDto memberDto = GoogleMemberDto.builder()
                .email("googlemember@gmail.com")
                .name("회원")
                .birthDate(LocalDate.now())
                .contact("01111121111")
                .build();

        Member result = memberService.signupGoogleMember(memberDto);

        assertThat(result.getEmail()).isEqualTo("googlemember@gmail.com");
        assertThat(result.isSocialMember()).isTrue();

    }

    @DisplayName("구글회원가입-실패-중복된아이디디")
    @Test
    void signupGoogleMemberDuplicatedEmailFailTest() {

        GoogleMemberDto memberDto = GoogleMemberDto.builder()
                .email("googlemember@gmail.com")
                .name("회원")
                .birthDate(LocalDate.now())
                .contact("01111121111")
                .build();

        when(memberService.signupGoogleMember(memberDto))
                .thenThrow(new DuplicateValueExeption("이미 사용되고 있는 Email입니다"));


        DuplicateValueExeption ex = assertThrows(DuplicateValueExeption.class, () -> memberService.signupGoogleMember(memberDto));

        assertThat(ex.getMessage().contains("이미 사용되고 있는 Email입니다")).isTrue();

    }



    @DisplayName("회원정보수정-성공")
    @Test
    void updateMemberTest() {
        LocalDate date = LocalDate.now();
        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(new Member()));

        MemberDto memberDto = MemberDto.builder()
                .birthDate(date)
                .build();

        memberService.updateMember(1L, memberDto);

        verify(memberRepository, times(1)).findById(1L);
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @DisplayName("회원정보수정-실패-존재하지않는멤버")
    @Test
    void updateMemberNotFoundFailTest() {

        MemberDto memberDto = MemberDto.builder()
                .name("변경될이름")
                .build();

        when(memberRepository.findById(1L))
                .thenThrow(new DataNotFoundException("찾을 회원이 없습니다"));

        DataNotFoundException ex = assertThrows(DataNotFoundException.class, () -> memberService.updateMember(1L, memberDto));

        assertThat(ex.getMessage().contains("찾을 회원이 없습니다")).isTrue();
    }



    @DisplayName("회원탈퇴-성공")
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

    @DisplayName("회원탈퇴-실패-존재하지않는멤버")
    @Test
    void withdrawalMemberNotFoundFailTest() {

        when(memberRepository.findById(1L))
                .thenThrow(new DataNotFoundException("찾을 회원이 없습니다"));

        DataNotFoundException ex = assertThrows(DataNotFoundException.class, () -> memberService.withdrawal(1L));

        assertThat(ex.getMessage().contains("찾을 회원이 없습니다")).isTrue();
    }
}