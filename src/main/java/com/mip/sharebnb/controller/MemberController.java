package com.mip.sharebnb.controller;

import com.mip.sharebnb.dto.MemberDto;
import com.mip.sharebnb.model.Member;
import com.mip.sharebnb.security.jwt.TokenProvider;
import com.mip.sharebnb.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    @PutMapping("/member/{member_id}")
    @PreAuthorize("authenticated")
    public ResponseEntity<MemberDto> updateMember(
            @PathVariable Long member_id,
            @Valid @RequestBody MemberDto memberDto) throws InvalidInputException {

        Member member = memberService.updateMember(member_id, memberDto);
        return ResponseEntity.ok(mapToMemberDto(member));
    }

    @DeleteMapping("/member/{member_id}")
    @PreAuthorize("authenticated")
    public ResponseEntity<Boolean> withdrawalMember(@PathVariable Long member_id) {

        Member member = memberService.withdrawal(member_id);
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
