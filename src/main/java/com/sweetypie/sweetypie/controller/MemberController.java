package com.sweetypie.sweetypie.controller;

import com.sweetypie.sweetypie.dto.MemberDto;
import com.sweetypie.sweetypie.model.Member;
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
    private final AuthService authService;

    @GetMapping("/member/{member_id}")
    @PreAuthorize("authenticated")
    public ResponseEntity<MemberDto> getMember(@PathVariable Long member_id) {

        Member member = memberService.getMember(member_id);

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
