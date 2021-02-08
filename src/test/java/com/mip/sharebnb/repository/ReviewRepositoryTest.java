package com.mip.sharebnb.repository;

import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.model.Member;
import com.mip.sharebnb.model.Review;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.config.location="
        + "classpath:application.yml,"
        + "classpath:datasource.yml")
class ReviewRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @DisplayName("작성한 리뷰 가져오기")
    @Test
    void findReviewByAccommodation_IdAndMember_Id() {
        givenReview();

        Review review = reviewRepository.findReviewByAccommodation_IdAndMember_Id(1, 1).get();

        assertThat(review.getRating()).isEqualTo(3);
    }

    private void givenReview() {
        Review review = new Review(3L, 3, "좋아요", LocalDate.now(), givenMember(), givenAccommodation());

        reviewRepository.save(review);
    }

    private Member givenMember() {
        Member member = new Member();
        member.setId(1L);
        member.setEmail("ddd@gmail.com");
        member.setName("이재복");
        member.setPassword("1234");
        member.setContact("12378");
        member.setBirthDate(LocalDate.of(1993, 5, 1));

        memberRepository.save(member);

        return member;
    }

    private Accommodation givenAccommodation() {
        Accommodation accommodation = new Accommodation(1L, "서울특별시", "마포구", "서울특별시 마포구", "원룸", 1, 1, 1, 40000, 2, "010-1234-5678", 36.141f, 126.531f, "마포", "버스 7016", "깔끔", "", 4.56f, 125, "전체", "원룸", "이재복", 543, null, null, null, null, null);

        accommodationRepository.save(accommodation);

        return accommodation;
    }
}