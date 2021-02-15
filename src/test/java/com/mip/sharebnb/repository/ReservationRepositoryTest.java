package com.mip.sharebnb.repository;

import com.mip.sharebnb.model.Reservation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.config.location="
        + "classpath:test.yml")
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @DisplayName("예약 내역 조회 리스트")
    @Test
    @Transactional
    public void getReservationByMemberId(){

        List<Reservation> reservationByMember = reservationRepository.findReservationByMemberId(1L);

        Reservation result = reservationByMember.get(0);

        assertThat(reservationByMember.size()).isEqualTo(2);
        assertThat(reservationByMember.get(0).getTotalPrice()).isEqualTo(60000);
        assertThat(reservationByMember.get(0).getAccommodation().getAccommodationType()).isEqualTo("전체");
        assertThat(reservationByMember.get(0).getAccommodation().getBuildingType()).isEqualTo("아파트");

    }
}