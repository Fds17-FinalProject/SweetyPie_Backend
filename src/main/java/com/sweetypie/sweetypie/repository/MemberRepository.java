package com.sweetypie.sweetypie.repository;

import ch.qos.logback.classic.db.names.TableName;
import com.sweetypie.sweetypie.model.Member;
import com.sweetypie.sweetypie.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    @Query(value = "select * from Member where email=:email", nativeQuery = true)
    Optional<Member> findMemberIncludeDeletedMember(@Param("email") String email);
}
