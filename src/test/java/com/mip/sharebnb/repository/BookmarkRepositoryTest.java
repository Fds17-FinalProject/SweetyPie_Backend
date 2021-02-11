package com.mip.sharebnb.repository;

import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.model.Bookmark;
import com.mip.sharebnb.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(properties = "spring.config.location="
        + "classpath:application.yml,"
        + "classpath:datasource.yml")
class BookmarkRepositoryTest {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @DisplayName("북마크 리스트 조회")
    @Test

    void findBookmarksByMember_Id() {
        List<Member> members = memberRepository.findAll();

        List<Bookmark> bookmarks = bookmarkRepository.findBookmarksByMember_Id(members.get(members.size() - 1).getId());

        assertThat(bookmarks.size()).isEqualTo(2);

        assertThat(bookmarks.get(0).getAccommodation().getGu()).isEqualTo("마포구");
        assertThat(bookmarks.get(1).getAccommodation().getGu()).isEqualTo("서대문구");
    }

    @DisplayName("북마크 제거")
    @Test
//    @Transactional
    void deleteBookmark() {
        Member member = givenMember();
        givenBookmarks(member);

        List<Member> members = memberRepository.findAll();

        // 삭제 전
        List<Bookmark> bookmarks = bookmarkRepository.findBookmarksByMember_Id(members.get(members.size() - 1).getId());
        System.out.println("------------" + bookmarks);

        assertThat(bookmarks.size()).isEqualTo(2);

        for (Bookmark bookmark : bookmarks) {
            System.out.println("삭제 시점-----------------------");
            bookmarkRepository.deleteById(bookmark.getId());
//            bookmarkRepository.delete(bookmark);
        }

        // 삭제 후
        bookmarks = bookmarkRepository.findBookmarksByMember_Id(members.get(members.size() - 1).getId());
        System.out.println("------------" + bookmarks);
        assertThat(bookmarks.size()).isEqualTo(0);
    }

    private void givenBookmarks(Member member) {

        Accommodation accommodation = new Accommodation(null, "서울특별시", "마포구", "서울특별시 마포구", "원룸", 1, 1, 1, 40000, 2, "010-1234-5678", 36.141f, 126.531f, "마포", "버스 7016", "깔끔", "", 4.56f, 125, "전체", "원룸", "이재복", 543, null, null, null, null, new ArrayList<>());
        Accommodation accommodation2 = new Accommodation(null, "서울특별시", "서대문구", "서울특별시 서대문구", "아파트", 2, 2, 2, 100000, 4, "010-1234-5678", 36.141f, 126.531f, "서대문구", "버스 7016", "깔끔", "", 4.56f, 125, "전체", "원룸", "이재복", 543, null, null, null, null, new ArrayList<>());

        accommodationRepository.save(accommodation);
        accommodationRepository.save(accommodation2);

        Bookmark bookmark = new Bookmark();
        bookmark.setMember(member);
        bookmark.setAccommodation(accommodation);
        bookmarkRepository.save(bookmark);

        Bookmark bookmark2 = new Bookmark();
        bookmark2.setMember(member);
        bookmark2.setAccommodation(accommodation2);
        bookmarkRepository.save(bookmark2);
    }

    private Member givenMember() {
        Member member = new Member();
        member.setEmail("ddd2@gmail.com");
        member.setName("이재복");
        member.setPassword("1234");
        member.setContact("12378");
        member.setBirthDate(LocalDate.of(1993, 5, 1));

        memberRepository.save(member);

        return member;
    }
}