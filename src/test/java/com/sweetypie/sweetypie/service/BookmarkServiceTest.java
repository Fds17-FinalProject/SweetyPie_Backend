package com.sweetypie.sweetypie.service;

import com.sweetypie.sweetypie.dto.BookmarkDto;
import com.sweetypie.sweetypie.dto.BookmarkListDto;
import com.sweetypie.sweetypie.exception.DataNotFoundException;
import com.sweetypie.sweetypie.exception.DuplicateValueExeption;
import com.sweetypie.sweetypie.model.Accommodation;
import com.sweetypie.sweetypie.model.Bookmark;
import com.sweetypie.sweetypie.model.Member;
import com.sweetypie.sweetypie.repository.AccommodationPictureRepository;
import com.sweetypie.sweetypie.repository.AccommodationRepository;
import com.sweetypie.sweetypie.repository.BookmarkRepository;
import com.sweetypie.sweetypie.repository.MemberRepository;
import com.sweetypie.sweetypie.repository.dynamic.DynamicBookmarkRepository;
import com.sweetypie.sweetypie.security.jwt.TokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {

    @InjectMocks
    private BookmarkService bookmarkService;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private DynamicBookmarkRepository dynamicBookmarkRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private AccommodationPictureRepository accommodationPictureRepository;

    @Mock
    private TokenProvider tokenProvider;

    @DisplayName("북마크 리스트 조회")
    @Test
    void findBookmarks() {
        when(dynamicBookmarkRepository.findByMemberId(0L)).thenReturn(mockBookmarks());

        List<BookmarkListDto> bookmarks = bookmarkService.findBookmarksByToken("token");

        assertThat(bookmarks.size()).isEqualTo(2);

        assertThat(bookmarks.get(0).getBookmarkId()).isEqualTo(1);
        assertThat(bookmarks.get(0).getAccommodationId()).isEqualTo(1);

        assertThat(bookmarks.get(1).getBookmarkId()).isEqualTo(2);
        assertThat(bookmarks.get(1).getAccommodationId()).isEqualTo(2);
    }

    @DisplayName("북마크 등록")
    @Test
    void postBookmark() {
        when(memberRepository.findById(0L)).thenReturn(mockMember());
        when(accommodationRepository.findById(0L)).thenReturn(mockAccommodation());

        bookmarkService.postBookmark("token", mockBookmarkDto());

        verify(bookmarkRepository, times(1)).save(any(Bookmark.class));
    }

    @DisplayName("북마크 등록 (토큰에 있는 회원 없음)")
    @Test
    void postBookmarkException1() {
        lenient().when(accommodationRepository.findById(1L)).thenReturn(mockAccommodation());

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class, ()
                -> bookmarkService.postBookmark("token", mockBookmarkDto()));

        assertThat(dataNotFoundException.getMessage()).isEqualTo("Member Not Found");
    }

    @DisplayName("북마크 등록 (없는 숙소)")
    @Test
    void postBookmarkException2() {
        when(memberRepository.findById(0L)).thenReturn(mockMember());

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> bookmarkService.postBookmark("token", mockBookmarkDto()));

        assertThat(dataNotFoundException.getMessage()).isEqualTo("Accommodation Not Found");
    }

    @DisplayName("북마크 등록 (북마크 중복)")
    @Test
    void postBookmarkException3() {
        when(memberRepository.findById(0L)).thenReturn(mockMember());
        when(accommodationRepository.findById(0L)).thenReturn(mockAccommodation());
        when(bookmarkRepository.findBookmarkByMemberIdAndAccommodationId(1L, 0L))
                .thenReturn(mockBookmark());

        DuplicateValueExeption duplicateValueExeption = assertThrows(DuplicateValueExeption.class,
                () -> bookmarkService.postBookmark("token", mockBookmarkDto()));

        assertThat(duplicateValueExeption.getMessage()).isEqualTo("Already Have a Bookmark");
    }

    @DisplayName("북마크 제거")
    @Test
    void deleteBookmarkById() {
        when(bookmarkRepository.findBookmarkByMemberIdAndAccommodationId(0, 1))
                .thenReturn(mockBookmark());
        when(tokenProvider.parseTokenToGetMemberId("token")).thenReturn(0L);

        bookmarkService.deleteBookmark("token", 1);

        verify(bookmarkRepository, times(1)).delete(mockBookmark().get());
    }

    @DisplayName("북마크 제거 (없는 북마크)")
    @Test
    void deleteBookmarkException1() {

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> bookmarkService.deleteBookmark("token", 1));

        assertThat(dataNotFoundException.getMessage()).isEqualTo("Bookmark Not Found");
    }

    private List<BookmarkListDto> mockBookmarks() {
        List<BookmarkListDto> bookmarks = new ArrayList<>();

        BookmarkListDto bookmark = new BookmarkListDto();
        bookmark.setBookmarkId(1L);
        bookmark.setAccommodationId(1L);
        bookmarks.add(bookmark);

        BookmarkListDto bookmark2 = new BookmarkListDto();
        bookmark2.setBookmarkId(2L);
        bookmark2.setAccommodationId(2L);
        bookmarks.add(bookmark2);

        return bookmarks;
    }

    private Optional<Bookmark> mockBookmark() {
        Member member = mockMember().get();

        Bookmark bookmark = new Bookmark();
        bookmark.setId(1L);
        bookmark.setMember(member);
        bookmark.setAccommodation(givenAccommodation(1L));

        return Optional.of(bookmark);
    }

    private Accommodation givenAccommodation(Long id) {

        return new Accommodation(id, 0, "서울특별시", "마포구", "서울특별시 마포구", "원룸", 1, 1, 1, 40000, 2, "010-1234-5678", 36.141f, 126.531f, "마포", "버스 7016", "깔끔", "", 4.56f, 125, "전체", "원룸", "이재복", 543, null, null, null, null);
    }

    private Optional<Member> mockMember() {
        Member member = new Member();
        member.setId(1L);
        member.setEmail("ddd@gmail.com");
        member.setName("이재복");
        member.setPassword("1234");
        member.setContact("12378");
        member.setBirthDate(LocalDate.of(1993, 5, 1));

        return Optional.of(member);
    }

    private BookmarkDto mockBookmarkDto() {
        BookmarkDto bookmarkDto = new BookmarkDto();
        bookmarkDto.setBookmarkId(1L);

        return bookmarkDto;
    }

    private Optional<Accommodation> mockAccommodation() {
        return Optional.of(new Accommodation(1L, 0, "서울특별시", "마포구", "서울특별시 마포구 독막로 266", "원룸", 1, 1, 1, 40000, 2, "010-1234-5678", 36.141f, 126.531f, "마포역 1번 출구 앞", "버스 7016", "깨끗해요", "착해요", 4.56f, 125, "전체", "원룸", "이재복", 543, null, new ArrayList<>(), null, null));
    }
}