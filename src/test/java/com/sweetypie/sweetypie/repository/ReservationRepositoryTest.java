package com.sweetypie.sweetypie.repository;

import com.sweetypie.sweetypie.model.Reservation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.config.location=classpath:test.yml")
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @DisplayName("예약 내역 조회 리스트")
    @Test
    @Transactional
    public void getReservationByMemberId(){

        List<Reservation> reservationByMember = reservationRepository.findReservationByMemberId(1L);

        Reservation result = reservationByMember.get(0);

        assertThat(reservationByMember.size()).isEqualTo(3);
        assertThat(reservationByMember.get(0).getTotalPrice()).isEqualTo(95600);
        assertThat(reservationByMember.get(0).getAccommodation().getAccommodationType()).isEqualTo("전체");
        assertThat(reservationByMember.get(0).getAccommodation().getBuildingType()).isEqualTo("아파트");

    }
}