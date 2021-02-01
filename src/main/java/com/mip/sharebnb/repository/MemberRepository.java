package com.mip.sharebnb.repository;

import com.mip.sharebnb.model.Member;
import org.springframework.data.repository.CrudRepository;

public interface MemberRepository extends CrudRepository<Member, Long> {
}
