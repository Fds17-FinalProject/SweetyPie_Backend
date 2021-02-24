package com.sweetypie.sweetypie.repository;

import com.sweetypie.sweetypie.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    @Query(value = "select * from member where email=:email", nativeQuery = true)
    Optional<Member> findMemberIncludeDeletedMember(@Param("email") String email);
}
