package com.mip.sharebnb.controller;

import com.mip.sharebnb.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class helloController {

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/member/{id}")
    public String getMember(@PathVariable Long id) {

        return memberRepository.findById(id).get().getEmail();
    }
}