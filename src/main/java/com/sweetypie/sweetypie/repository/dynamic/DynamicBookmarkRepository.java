package com.sweetypie.sweetypie.repository.dynamic;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sweetypie.sweetypie.dto.BookmarkListDto;
import com.sweetypie.sweetypie.dto.QBookmarkListDto;
import com.sweetypie.sweetypie.model.QAccommodation;
import com.sweetypie.sweetypie.model.QBookmark;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DynamicBookmarkRepository {

    private final JPAQueryFactory queryFactory;

    QBookmark bookmark = QBookmark.bookmark;
    QAccommodation acc = QAccommodation.accommodation;

    public List<BookmarkListDto> findByMemberId(long memberId) {

        return queryFactory
                .select(new QBookmarkListDto(bookmark.id, acc.id, acc.title, acc.city, acc.gu))
                .from(bookmark)
                .where(bookmark.member.id.eq(memberId))
                .join(acc)
                .on(acc.id.eq(bookmark.accommodation.id))
                .fetch();
    }
}
