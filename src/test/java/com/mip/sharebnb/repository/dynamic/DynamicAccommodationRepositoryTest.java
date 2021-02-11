package com.mip.sharebnb.repository.dynamic;

import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.model.BookedDate;
import com.mip.sharebnb.model.Member;
import com.mip.sharebnb.model.Reservation;
import com.mip.sharebnb.repository.AccommodationRepository;
import com.mip.sharebnb.repository.MemberRepository;
import com.mip.sharebnb.repository.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(properties = "spring.config.location="
        + "classpath:application.yml,"
        + "classpath:datasource.yml")
class DynamicAccommodationRepositoryTest {

    @Autowired
    private DynamicAccommodationRepository dynamicAccommodationRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @DisplayName("메인 검색 테스트")
    @Test
    void search() {
        for (int i = 0; i < 10; i++) {
            Accommodation accommodation = givenAccommodation();
            accommodationRepository.save(accommodation);
        }

        Page<Accommodation> accommodations = dynamicAccommodationRepository.
                findAccommodationsBySearch("대구",
                        LocalDate.of(2022, 3, 3),
                        LocalDate.of(2022, 3, 4), 1,
                        PageRequest.of(0, 10));

        assertThat(accommodations.toList().size()).isEqualTo(10);

        if (!accommodations.isEmpty()) {
            for (Accommodation ac : accommodations) {
                assertThat(ac.getCity()).isEqualTo("대구광역시");
            }
        }
    }

    @DisplayName("메인 검색 (날짜 겹침) 테스트")
    @Test
    void search2() {
        Accommodation accommodation = givenAccommodation();
        setBookDates(accommodation);
        accommodationRepository.save(accommodation);

        Page<Accommodation> accommodations = dynamicAccommodationRepository.
                findAccommodationsBySearch("대구",
                        LocalDate.of(2022, 3, 3),
                        LocalDate.of(2022, 3, 7), 1,
                        PageRequest.of(0, 10));

        assertThat(accommodations.toList().size()).isEqualTo(0);
    }

    @DisplayName("검색어 없이 메인 검색 테스트")
    @Test
    void searchIfSearchKeywordIsEmpty() {
        Page<Accommodation> accommodations = dynamicAccommodationRepository.findAccommodationsBySearch(null, LocalDate.of(2021, 5, 1), LocalDate.of(2021, 5, 5), 3, PageRequest.of(1, 10));

        assertThat(accommodations.toList().size()).isEqualTo(10);

        for (Accommodation accommodation : accommodations) {
            assertThat(accommodation.getCapacity()).isGreaterThanOrEqualTo(3);
        }
    }

    @DisplayName("지도 범위(좌표 기준) 내 검색")
    @Test
    void searchByCoordinate() {
        Page<Accommodation> accommodations = dynamicAccommodationRepository.findAccommodationsByMapSearch(37f, 37.5f, 126f, 127f, PageRequest.of(1, 10));

        for (Accommodation accommodation : accommodations) {
            assertThat(accommodation.getLatitude()).isGreaterThan(37f);
            assertThat(accommodation.getLatitude()).isLessThan(37.5f);
            assertThat(accommodation.getLongitude()).isGreaterThan(126f);
            assertThat(accommodation.getLongitude()).isLessThan(127f);
        }
    }

    private Accommodation givenAccommodation() {

        return new Accommodation(null, "대구광역시", "수성구", "대구광역시 수성구 xx로", "원룸", 1, 1, 1, 40000, 2, "010-1234-5678", 36.141f, 126.531f, "마포역 1번 출구 앞", "버스 7016", "깨끗해요", "착해요", 4.56f, 125, "전체", "원룸", "이재복", 543, null, null, new ArrayList<>(), null);
    }

    private Member givenMember() {
        Member member = new Member();
        member.setEmail("d64u90@gmail.com");
        member.setName("이재복");
        member.setPassword("1234");
        member.setContact("12378");
        member.setBirthDate(LocalDate.of(1993, 5, 1));

        return memberRepository.save(member);
    }

    private void setBookDates(Accommodation accommodation) {
        Member member = givenMember();
        Reservation reservation = givenReservation(accommodation, member);

        for (int i = 0; i < 5; i++) {
            BookedDate bookedDate = new BookedDate();
            bookedDate.setDate(LocalDate.of(2022, 3, 4).plusDays(i));
            bookedDate.setReservation(reservation);
        }

        reservationRepository.save(reservation);
    }

    private Reservation givenReservation(Accommodation accommodation, Member member) {
        Reservation reservation = new Reservation();

        LocalDate checkIn = LocalDate.of(2022, 3, 4);
        LocalDate checkout = LocalDate.of(2022, 3, 9);
        reservation.setCheckInDate(checkIn);
        reservation.setCheckoutDate(checkout);
        reservation.setGuestNum(3);
        reservation.setTotalPrice(20000);
        reservation.setPaymentDate(LocalDate.now());
        reservation.setAccommodation(accommodation);
        reservation.setMember(member);

        return reservation;
    }
}