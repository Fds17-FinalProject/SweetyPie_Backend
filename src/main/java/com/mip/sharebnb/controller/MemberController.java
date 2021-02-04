package com.mip.sharebnb.controller;

import com.mip.sharebnb.dto.MemberDto;
import com.mip.sharebnb.model.Member;
import com.mip.sharebnb.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/member/{member_id}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<MemberDto> getMember(@PathVariable Long member_id) {

        Member member = memberService.getMember(member_id);
        MemberDto result = MemberDto.builder()
                .email(member.getEmail())
                .name(member.getName())
                .contact(member.getContact())
                .build();

        return ResponseEntity.ok(result);
    }

    @PostMapping("/member")
    public ResponseEntity<MemberDto> signup(
            @Valid @RequestBody MemberDto memberDto) {

        Member member = memberService.signup(memberDto);
        MemberDto result = MemberDto.builder()
                .email(member.getEmail())
                .name(member.getName())
                .contact(member.getContact())
                .build();

        return ResponseEntity.ok(result);
    }
}
