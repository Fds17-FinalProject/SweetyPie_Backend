package com.sweetypie.sweetypie.repository;

import com.sweetypie.sweetypie.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.config.location=classpath:test.yml")
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @DisplayName("email로Member찾기")
    @Test
    void findByEmailTest() {

        Member result = memberRepository.findByEmail("test123@gmail.com").get();

        assertThat(result.getEmail()).isEqualTo("test123@gmail.com");
    }

    @DisplayName("email로탈퇴한회원포함해서찾기")
    @Test
    void findByEmailIncludeDeletedTest() {
        Optional<Member> result = memberRepository.findMemberIncludeDeletedMember("withdrawaltest@gmail.com");

        assertThat(result.isPresent()).isTrue();
    }

}