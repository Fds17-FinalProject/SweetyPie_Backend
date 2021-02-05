package com.mip.sharebnb.service;

import com.mip.sharebnb.dto.ReservationDto;
import com.mip.sharebnb.model.*;
import com.mip.sharebnb.repository.BookedDateRepository;
import com.mip.sharebnb.repository.ReservationRepository;
import com.mip.sharebnb.repository.dynamic.DynamicReservationRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private BookedDateRepository bookedDateRepository;

    @Mock
    private DynamicReservationRepository dynamicReservationRepository;

    @Test
    void getReservationByMemberId() {
        when(reservationRepository.findReservationByMemberId(1L)).thenReturn(mockReservation());

        List<ReservationDto> reservationDtoList = reservationService.getReservations(1L);

        assertThat(reservationDtoList.size()).isEqualTo(1);
        assertThat(reservationDtoList.get(0).getCheckInDate()).isEqualTo("2020-02-22");
        assertThat(reservationDtoList.get(0).getCheckoutDate()).isEqualTo("2020-02-24");
        assertThat(reservationDtoList.get(0).getAccommodationDto().getCity()).isEqualTo("서울시");
        assertThat(reservationDtoList.get(0).getAccommodationDto().getGu()).isEqualTo("강남구");
        assertThat(reservationDtoList.get(0).getAccommodationDto().getAccommodationPictures().get(0).getUrl()).isEqualTo("picture");

    }

    @Test
    void getReservationByMemberIdEmpty() {
        when(reservationRepository.findReservationByMemberId(1L)).thenReturn(new ArrayList<>());

        List<Reservation> reservations = reservationRepository.findReservationByMemberId(1L);

        assertThat(reservations.isEmpty()).isTrue();
    }

    @DisplayName("update 성공")
    @Test
    void updateReservationSuccess(){
        when(reservationRepository.findById(1L)).thenReturn(mockFindReservation());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(setReservation());
        when(dynamicReservationRepository.findByReservationIdAndDate(1L,1L, LocalDate.of(2020, 3, 20), LocalDate.of(2020, 3, 22))).thenReturn(new ArrayList<>());

        ReservationDto dto = new ReservationDto();
        dto.setCheckInDate(LocalDate.of(2020,3,20));
        dto.setCheckoutDate(LocalDate.of(2020,3,22));
        dto.setGuestNum(3);
        dto.setTotalPrice(30000);

        Reservation reservation = reservationService.updateReservation(1L, dto);

        verify(bookedDateRepository, times(1)).findBookedDatesByReservationId(1L);
        verify(bookedDateRepository, times(1)).deleteBookedDateByReservationId(1L);
        verify(reservationRepository, times(1)).findById(1L);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(bookedDateRepository, times(0)).save(any(BookedDate.class));
    }

    @DisplayName("예약 날짜 중복으로 update 실패")
    @Test
    void updateReservationFail(){
        when(bookedDateRepository.findBookedDatesByReservationId(1L)).thenReturn(mockBookedDate());
        when(reservationRepository.findById(1L)).thenReturn(mockFindReservation());
        when(dynamicReservationRepository.findByReservationIdAndDate(1L,1L, LocalDate.of(2020, 2, 20), LocalDate.of(2020, 2, 22))).thenReturn(mockBookedDate());

        ReservationDto dto = new ReservationDto();
        dto.setCheckInDate(LocalDate.of(2020,2,20));
        dto.setCheckoutDate(LocalDate.of(2020,2,22));
        dto.setGuestNum(3);
        dto.setTotalPrice(30000);

        Reservation reservation = reservationService.updateReservation(1L, dto);

        verify(bookedDateRepository, times(1)).findBookedDatesByReservationId(1L);
        verify(bookedDateRepository, times(1)).deleteBookedDateByReservationId(1L);
        verify(reservationRepository, times(1)).findById(1L);
        verify(reservationRepository, times(0)).save(any(Reservation.class));
        verify(bookedDateRepository, times(3)).save(any(BookedDate.class));
    }

    private Reservation setReservation(){
        return Reservation.builder().checkInDate(LocalDate.of(2020, 2, 20))
                .checkoutDate(LocalDate.of(2020, 2, 22))
                .guestNum(3)
                .totalPrice(30000)
                .build();
    }

    private List<Reservation> mockReservation() {
        List<AccommodationPicture> accommodationPictures = new ArrayList<>();
        AccommodationPicture firAccommodationPicture = new AccommodationPicture();
        firAccommodationPicture.setUrl("picture");

        accommodationPictures.add(firAccommodationPicture);

        AccommodationPicture secAccommodationPicture = new AccommodationPicture();
        secAccommodationPicture.setUrl("photo");

        accommodationPictures.add(secAccommodationPicture);

        List<Reservation> reservations = new ArrayList<>();
        Accommodation accommodation = new Accommodation();
        accommodation.setCity("서울시");
        accommodation.setGu("강남구");
        accommodation.setBathroomNum(4);
        accommodation.setBedNum(8);
        accommodation.setAccommodationPictures(accommodationPictures);

        Reservation reservation = new Reservation();
        LocalDate checkInDate = LocalDate.of(2020, 2, 22);
        LocalDate checkoutDate = LocalDate.of(2020, 2, 24);
        reservation.setCheckInDate(checkInDate);
        reservation.setCheckoutDate(checkoutDate);
        reservation.setTotalPrice(50000);
        reservation.setGuestNum(5);
        reservation.setAccommodation(accommodation);

        reservations.add(reservation);

        return reservations;
    }

    private Optional<Reservation> mockFindReservation() {

        Accommodation accommodation = new Accommodation();
        accommodation.setId(1L);
        accommodation.setBathroomNum(2);
        accommodation.setBedroomNum(2);
        accommodation.setAccommodationType("집전체");
        accommodation.setBuildingType("아파트");

        Reservation reservation = new Reservation();
        LocalDate checkInDate = LocalDate.of(2020, 2, 22);
        LocalDate checkoutDate = LocalDate.of(2020, 2, 24);
        reservation.setId(1L);
        reservation.setCheckInDate(checkInDate);
        reservation.setCheckoutDate(checkoutDate);
        reservation.setTotalPrice(50000);
        reservation.setGuestNum(5);
        reservation.setAccommodation(accommodation);

        return Optional.of(reservation);
    }

    private Optional<Accommodation> mockFindAccommodation() {

        BookedDate bookedDate = new BookedDate();
        bookedDate.setDate(LocalDate.of(2020,2,22));
        bookedDate.setDate(LocalDate.of(2020, 2, 22));

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

    private List<BookedDate> mockBookedDate(){

        Reservation reservation = Reservation.builder().id(1L).build();

        Accommodation accommodation = Accommodation.builder().id(1L).build();

        List<BookedDate> bookedDates = new ArrayList<>();

        BookedDate bookedDate1 = new BookedDate();
        bookedDate1.setId(1L);
        bookedDate1.setDate(LocalDate.of(2020, 2, 20));
        bookedDate1.setReservation(reservation);
        bookedDate1.setAccommodation(accommodation);

        bookedDates.add(bookedDate1);

        BookedDate bookedDate2 = new BookedDate();
        bookedDate2.setId(2L);
        bookedDate2.setDate(LocalDate.of(2020, 2, 21));
        bookedDate2.setReservation(reservation);
        bookedDate2.setAccommodation(accommodation);

        bookedDates.add(bookedDate2);

        BookedDate bookedDate3 = new BookedDate();
        bookedDate3.setDate(LocalDate.of(2020, 2, 22));
        bookedDate3.setId(3L);
        bookedDate3.setReservation(reservation);
        bookedDate3.setAccommodation(accommodation);

        bookedDates.add(bookedDate3);

        return bookedDates;

    }
}