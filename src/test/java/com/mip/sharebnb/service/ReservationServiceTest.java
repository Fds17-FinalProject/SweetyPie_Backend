package com.mip.sharebnb.service;

import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.model.Member;
import com.mip.sharebnb.model.Reservation;
import com.mip.sharebnb.repository.AccommodationRepository;
import com.mip.sharebnb.repository.MemberRepository;
import com.mip.sharebnb.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Test
    void getReservationByMemberId(){
        when(reservationRepository.findReservationByMemberId(1L)).thenReturn(mockReservation());

        List<Reservation> reservations = reservationService.getReservations(1L);

        LocalDate checkInDate = LocalDate.of(2020, 2, 22);

        assertThat(reservations.size()).isEqualTo(1);
        assertThat(reservations.get(0).getMember().getId()).isEqualTo(1L);
        assertThat(reservations.get(0).getCheckInDate()).isEqualTo(checkInDate);
    }

    private List<Reservation> mockReservation(){
        List<Reservation> reservations = new ArrayList<>();

        Member firMember = new Member();
        firMember.setId(1L);
        firMember.setEmail("test123@gmail.com");
        firMember.setContact("01077777777");

        Reservation firReservation = new Reservation();
        LocalDate checkInDate = LocalDate.of(2020,2, 22);
        LocalDate checkoutDate = LocalDate.of(2020,2, 24);
        firReservation.setCheckInDate(checkInDate);
        firReservation.setCheckoutDate(checkoutDate);
        firReservation.setTotalPrice(50000);
        firReservation.setGuestNum(5);
        firReservation.setMember(firMember);

        reservations.add(firReservation);

        return reservations;
    }

}