package com.mip.sharebnb.service;

import com.mip.sharebnb.dto.ReservationDto;
import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.model.BookedDate;
import com.mip.sharebnb.model.Member;
import com.mip.sharebnb.model.Reservation;
import com.mip.sharebnb.repository.AccommodationRepository;
import com.mip.sharebnb.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private AccommodationRepository accommodationRepository;

    @Test
    void getReservationByMemberId(){
        when(reservationRepository.findReservationByMemberId(1L)).thenReturn(mockReservation());

        List<Reservation> reservations = reservationService.getReservations(1L);

        LocalDate checkInDate = LocalDate.of(2020, 2, 22);

        assertThat(reservations.size()).isEqualTo(1);
        assertThat(reservations.get(0).getMember().getId()).isEqualTo(1L);
        assertThat(reservations.get(0).getCheckInDate()).isEqualTo(checkInDate);
    }

    @Test
    void getReservationByMemberIdEmpty(){
        when(reservationRepository.findReservationByMemberId(1L)).thenReturn(new ArrayList<>());

        List<Reservation> reservations = reservationRepository.findReservationByMemberId(1L);

        assertThat(reservations.isEmpty()).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    void updateReservation(){
        when(reservationRepository.findById(1L)).thenReturn(mockFindReservation());
        when(accommodationRepository.findById(1L)).thenReturn(mockFindAccommodation());

        ReservationDto dto = new ReservationDto();
        setReservationDto(dto);

        Reservation reservation = reservationService.updateReservation(1L, dto);

        verify(reservationRepository, times(1)).findById(1L);
        verify(accommodationRepository, times(1)).findById(1L);
        verify(reservationRepository, times(1)).save(any(Reservation.class));

    }

    private void setReservationDto(ReservationDto dto){
        dto.setCheckInDate(LocalDate.of(2020,2,22));
        dto.setCheckoutDate(LocalDate.of(2020, 2, 24));
        dto.setGuestNum(3);
        dto.setTotalPrice(30000);
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

    private Optional<Reservation> mockFindReservation(){

        Accommodation accommodation = new Accommodation();
        accommodation.setId(1L);
        accommodation.setBathroomNum(2);
        accommodation.setBedroomNum(2);
        accommodation.setAccommodationType("집전체");
        accommodation.setBuildingType("아파트");

        Reservation reservation = new Reservation();
        LocalDate checkInDate = LocalDate.of(2020,2, 22);
        LocalDate checkoutDate = LocalDate.of(2020,2, 24);
        reservation.setCheckInDate(checkInDate);
        reservation.setCheckoutDate(checkoutDate);
        reservation.setTotalPrice(50000);
        reservation.setGuestNum(5);
        reservation.setAccommodation(accommodation);

        return Optional.of(reservation);
    }

    private Optional<Accommodation> mockFindAccommodation(){
        BookedDate bookedDate = new BookedDate();
        bookedDate.setDate(LocalDate.of(2020,2,22));

        List<BookedDate> bookedDates = new ArrayList<>();
        bookedDates.add(bookedDate);

        Accommodation accommodation = new Accommodation();
        accommodation.setId(1L);
        accommodation.setBathroomNum(2);
        accommodation.setBedroomNum(2);
        accommodation.setAccommodationType("집전체");
        accommodation.setBuildingType("게스트하우스");
        accommodation.setBookedDates(bookedDates);
        accommodation.setCapacity(4);

        return Optional.of(accommodation);
    }
}