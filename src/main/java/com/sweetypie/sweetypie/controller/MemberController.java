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

    @GetMapping("/member")
    @PreAuthorize("authenticated")
    public ResponseEntity<MemberDto> getMember(@RequestHeader("Authorization") String token) {

        Member member = memberService.getMember(tokenProvider.parseTokenToGetUserId(token));

        return ResponseEntity.ok(mapToMemberDto(member));
    }


    @PostMapping("/member")
    public ResponseEntity<MemberDto> signup(
            @Valid @RequestBody MemberDto memberDto) {

        Member member = memberService.signup(memberDto);

        return ResponseEntity.ok(mapToMemberDto(member));
    }

    @PutMapping("/member")
    @PreAuthorize("authenticated")
    public ResponseEntity<MemberDto> updateMember(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody MemberDto memberDto) throws InvalidInputException {

        Member member = memberService.updateMember(tokenProvider.parseTokenToGetUserId(token),
                                                     memberDto);
        return ResponseEntity.ok(mapToMemberDto(member));
    }

    @DeleteMapping("/member")
    @PreAuthorize("authenticated")
    public ResponseEntity<Boolean> withdrawalMember(@RequestHeader("Authorization") String token) {

        Member member = memberService.withdrawal(tokenProvider.parseTokenToGetUserId(token));
        return ResponseEntity.ok(member.isDeleted());
    }

    private MemberDto mapToMemberDto(Member member) {

        return   MemberDto.builder()
                .email(member.getEmail())
                .name(member.getName())
                .contact(member.getContact())
                .birthDate(member.getBirthDate())
                .build();
    }
}
