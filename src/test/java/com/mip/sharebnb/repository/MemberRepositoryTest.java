package com.mip.sharebnb.repository;

import com.mip.sharebnb.model.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(properties = "spring.config.location="
        + "classpath:application.yml,"
        + "classpath:datasource.yml")
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    void findByEmailTest() {
        Member member = givenMember("findByEmailTest@mail.com");
        memberRepository.save(member);

        Member result = memberRepository.findByEmail("findByEmailTest@mail.com").get();

        assertThat(result.getEmail()).isEqualTo("findByEmailTest@mail.com");
    }

    private Member givenMember(String email) {
        Member member = new Member();
        member.setEmail(email);
        member.setName("이재복");
        member.setPassword("1234");
        member.setContact("01055442211");
        member.setBirthDate(LocalDate.now());

        return member;
    }

}