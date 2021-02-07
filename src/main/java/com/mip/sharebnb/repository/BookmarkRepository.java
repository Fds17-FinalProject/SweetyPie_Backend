package com.mip.sharebnb.repository;

import com.mip.sharebnb.model.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    List<Bookmark> findBookmarksByMember_Id(long memberId);
}
