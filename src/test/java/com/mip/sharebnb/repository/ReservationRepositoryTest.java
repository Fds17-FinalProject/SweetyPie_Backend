package com.mip.sharebnb.repository;

import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.model.BookedDate;
import com.mip.sharebnb.model.Member;
import com.mip.sharebnb.model.Reservation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.config.location="
        + "classpath:application.yml,"
        + "classpath:datasource.yml")
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("예약 내역 조회 리스트")
    @Test
    @Transactional
    public void getReservationByMemberId(){

        givenData();

        List<Reservation> reservationByMember = reservationRepository.findReservationByMemberId(1L);

        Reservation result = reservationByMember.get(0);

        assertThat(reservationByMember.size()).isEqualTo(2);
        assertThat(reservationByMember.get(0).getTotalPrice()).isEqualTo(20000);
        assertThat(reservationByMember.get(0).getAccommodation().getAccommodationType()).isEqualTo("집전체");
        assertThat(reservationByMember.get(0).getAccommodation().getBuildingType()).isEqualTo("단독주택");

    }

    void givenData(){
        List<Reservation> reservations = new ArrayList<>();

        Member member = new Member();
        LocalDate birtDate = LocalDate.of(2020,1,14);
        member.setId(1L);
        member.setEmail("test777@gmail.com");
        member.setPassword("1234");
        member.setName("tester");
        member.setBirthDate(birtDate);
        member.setContact("01022223333");

        Member saveMember = memberRepository.save(member);

        Accommodation accommodation = new Accommodation();
        accommodation.setId(1L);
        accommodation.setBathroomNum(2);
        accommodation.setBedroomNum(2);
        accommodation.setAccommodationType("집전체");
        accommodation.setBuildingType("아파트");
        Accommodation findAccommodation1 = accommodationRepository.save(accommodation);

        Reservation reservation = new Reservation();
        List<BookedDate> bookedDates = new ArrayList<>();

        LocalDate checkIn = LocalDate.of(2020,2,20);
        LocalDate checkout = LocalDate.of(2020,2,22);
        reservation.setCheckInDate(checkIn);
        reservation.setCheckoutDate(checkout);
        reservation.setGuestNum(3);
        reservation.setTotalPrice(20000);
        reservation.setPaymentDate(LocalDate.now());
        reservation.setAccommodation(findAccommodation1);
        reservation.setMember(saveMember);

        for (LocalDate date = LocalDate.of(2020,2,20); date.isBefore(LocalDate.of(2020,2,22)); date = date.plusDays(1)) {
            BookedDate bookedDate = new BookedDate();
            bookedDate.setDate(date);
            bookedDate.setReservation(reservation);
            bookedDate.setAccommodation(findAccommodation1);
            bookedDates.add(bookedDate);
        }

        reservation.setBookedDates(bookedDates);

        reservationRepository.save(reservation);
    }
}