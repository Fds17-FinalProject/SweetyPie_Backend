package com.mip.sharebnb.service;

import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.model.Bookmark;
import com.mip.sharebnb.model.Member;
import com.mip.sharebnb.repository.BookmarkRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {

    @InjectMocks
    private BookmarkService bookmarkService;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @DisplayName("북마크 리스트 조회")
    @Test
    void findBookmarks() {
        when(bookmarkService.findBookmarks(1L)).thenReturn(mockBookmark());

        List<Bookmark> bookmarks = bookmarkService.findBookmarks(1L);

        assertThat(bookmarks.size()).isEqualTo(2);

        for (Bookmark bookmark : bookmarks) {
            assertThat(bookmark.getMember().getName()).isEqualTo("이재복");
            assertThat(bookmark.getAccommodation().getCity()).isEqualTo("서울특별시");
            assertThat(bookmark.getAccommodation().getGu()).isEqualTo("마포구");
        }
    }

    @DisplayName("북마크 제거")
    @Test
    void deleteBookmarkById() {
    }

    private List<Bookmark> mockBookmark() {
        List<Bookmark> bookmarks = new ArrayList<>();

        Bookmark bookmark = new Bookmark();
        bookmark.setId(1L);
        bookmark.setMember(givenMember());
        bookmark.setAccommodation(givenAccommodation(1L));
        bookmarks.add(bookmark);

        Bookmark bookmark2 = new Bookmark();
        bookmark2.setId(2L);
        bookmark2.setMember(givenMember());
        bookmark2.setAccommodation(givenAccommodation(2L));
        bookmarks.add(bookmark2);

        return bookmarks;
    }

    private Accommodation givenAccommodation(Long id) {

        return new Accommodation(id, "서울특별시", "마포구", "서울특별시 마포구", "원룸", 1, 1, 1, 40000, 2, "010-1234-5678", 36.141f, 126.531f, "마포", "버스 7016", "깔끔", "", 4.56f, 125, "전체", "원룸", "이재복", 543, null, null, null, null);
    }

    private Member givenMember() {
        Member member = new Member();
        member.setId(1L);
        member.setEmail("ddd@gmail.com");
        member.setName("이재복");
        member.setPassword("1234");
        member.setContact("12378");
        member.setBirthDate(LocalDate.of(1993, 5, 1));

        return member;
    }
}