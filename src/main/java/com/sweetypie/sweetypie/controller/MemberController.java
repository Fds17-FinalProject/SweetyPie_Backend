package com.sweetypie.sweetypie.controller;

import com.sweetypie.sweetypie.dto.MemberDto;
import com.sweetypie.sweetypie.model.Member;
import com.sweetypie.sweetypie.security.jwt.TokenProvider;
import com.sweetypie.sweetypie.service.AuthService;
import com.sweetypie.sweetypie.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.eclipse.jdt.core.compiler.InvalidInputException;
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

        Member member = memberService.getMember(tokenProvider.parseTokenToGetUserId(token));

        return ResponseEntity.ok(mapToMemberDto(member));
    }


    @PostMapping("/member")
    public ResponseEntity<String> signup(
            @Valid @RequestBody MemberDto memberDto) {

        memberService.signup(memberDto);

        return ResponseEntity.ok("회원가입이 정상적으로 완료되었습니다");
    }

    @PutMapping("/member")
    @PreAuthorize("authenticated")
    public ResponseEntity<String> updateMember(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody MemberDto memberDto) {

        memberService.updateMember(tokenProvider.parseTokenToGetUserId(token),
                                                     memberDto);
        return ResponseEntity.ok("회원정보가 정상적으로 수정되었습니다");
    }

    @DeleteMapping("/member")
    @PreAuthorize("authenticated")
    public ResponseEntity<String> withdrawalMember(@RequestHeader("Authorization") String token) {

        memberService.withdrawal(tokenProvider.parseTokenToGetUserId(token));
        //회원 탈퇴후 접근한 토큰 만료시키기
        authService.logout(token);
        return ResponseEntity.ok("회원 탈퇴가 정상적으로 완료되었습니다");
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
