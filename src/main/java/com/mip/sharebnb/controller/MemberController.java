package com.mip.sharebnb.controller;

import com.mip.sharebnb.dto.MemberDto;
import com.mip.sharebnb.model.Member;
import com.mip.sharebnb.service.MemberService;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@RestController
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

        return ResponseEntity.ok(mapToMemberDto(member));
    }

    @PostMapping("/member")
    public ResponseEntity<MemberDto> signup(
            @Valid @RequestBody MemberDto memberDto) {

        Member member = memberService.signup(memberDto);

        return ResponseEntity.ok(mapToMemberDto(member));
    }

    @PutMapping("/member/{member_id}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<MemberDto> updateMember(
            @PathVariable Long member_id,
            @Valid @RequestBody MemberDto memberDto) throws InvalidInputException {

        Member member = memberService.updateMember(member_id, memberDto);
        return ResponseEntity.ok(mapToMemberDto(member));
    }

    @DeleteMapping("/member/{member_id}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<Boolean> updateMember(@PathVariable Long member_id) {

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
