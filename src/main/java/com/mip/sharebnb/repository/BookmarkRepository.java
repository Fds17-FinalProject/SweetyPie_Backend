package com.mip.sharebnb.repository;

import com.mip.sharebnb.model.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    List<Bookmark> findBookmarksByMemberId(long memberId);

    List<Bookmark> findBookmarksByMemberEmail(String email);

    Optional<Bookmark> findBookmarkByMemberIdAndAccommodationId(long memberId, long accommodationId);

    void deleteBookmarkByMemberIdAndAccommodationId(long memberId, long accommodationId);
}
