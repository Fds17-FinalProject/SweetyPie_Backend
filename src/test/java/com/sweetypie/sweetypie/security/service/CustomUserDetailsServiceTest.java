package com.sweetypie.sweetypie.security.service;

import com.sweetypie.sweetypie.exception.DataNotFoundException;
import com.sweetypie.sweetypie.model.Member;
import com.sweetypie.sweetypie.model.MemberRole;
import com.sweetypie.sweetypie.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private MemberRepository memberRepository;

    @DisplayName("loadUserByUsername-성공")
    @Test
    void loadUserByUsernameTest() {

        Member member = Member.builder()
                .email("test@mail.com")
                .password("password")
                .role(MemberRole.MEMBER)
                .build();

        when(memberRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(member));


        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@mail.com");

        verify(memberRepository, times(1)).findByEmail("test@mail.com");
        assertThat(userDetails.getUsername()).isEqualTo("test@mail.com");
    }

    @DisplayName("loadUserByUsername-실패-존재하지않는멤버")
    @Test
    void withdrawalMemberNotFoundFailTest() {

        when(memberRepository.findByEmail("test@mail.com"))
                .thenThrow(new DataNotFoundException("찾는 회원이 없습니다"));

        DataNotFoundException ex = assertThrows(DataNotFoundException.class, () ->  customUserDetailsService.loadUserByUsername("test@mail.com"));

        assertThat(ex.getMessage().contains("찾는 회원이 없습니다")).isTrue();
    }

}