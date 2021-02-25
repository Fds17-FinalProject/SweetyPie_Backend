package com.sweetypie.sweetypie.controller;

import com.sweetypie.sweetypie.dto.MemberDto;
import com.sweetypie.sweetypie.model.Member;
import com.sweetypie.sweetypie.security.jwt.TokenProvider;
import com.sweetypie.sweetypie.service.AuthService;
import com.sweetypie.sweetypie.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;
    private final AuthService authService;

    @GetMapping("/member")
    @PreAuthorize("authenticated")
    public ResponseEntity<MemberDto> getMember(@RequestHeader("Authorization") String token) {

        Member member = memberService.getMember(tokenProvider.parseTokenToGetMemberId(token));

        return ResponseEntity.ok(mapToMemberDto(member));
    }


    @PostMapping("/member")
    public void signup(
            @Valid @RequestBody MemberDto memberDto) {

        memberService.signup(memberDto);
    }

    @PutMapping("/member")
    @PreAuthorize("authenticated")
    public void updateMember(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody MemberDto memberDto) {

        memberService.updateMember(tokenProvider.parseTokenToGetMemberId(token), memberDto);
    }

    @DeleteMapping("/member")
    @PreAuthorize("authenticated")
    public void withdrawalMember(@RequestHeader("Authorization") String token) {

        memberService.withdrawal(tokenProvider.parseTokenToGetMemberId(token));
        //회원 탈퇴후 접근한 토큰 만료시키기
        authService.logout(token);
    }

    private MemberDto mapToMemberDto(Member member) {

        return   MemberDto.builder()
                .email(member.getEmail())
                .name(member.getName())
                .contact(member.getContact())
                .birthDate(member.getBirthDate())
                .isSocialMember(member.isSocialMember())
                .build();
    }
}
