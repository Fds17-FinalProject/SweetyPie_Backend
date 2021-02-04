package com.mip.sharebnb.service;

import com.mip.sharebnb.model.Member;
import com.mip.sharebnb.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Test
    void getMember() {
        Member member = Member.builder()
                .id(1L)
                .email("member@mail.com")
                .build();

        when(memberRepository.findById(1L))
                .thenReturn(Optional.ofNullable(member));

        Member result = memberService.getMember(1L);

        assertThat(result.getEmail()).isEqualTo("member@mail.com");
    }
}