package com.mip.sharebnb.service;

import com.mip.sharebnb.dto.ReservationDto;
import com.mip.sharebnb.model.*;
import com.mip.sharebnb.repository.AccommodationRepository;
import com.mip.sharebnb.repository.BookedDateRepository;
import com.mip.sharebnb.repository.MemberRepository;
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

    @Mock(name = "reservationRepository")
    private ReservationRepository reservationRepository;

    @Mock(name = "bookedDateRepository")
    private BookedDateRepository bookedDateRepository;

    @Mock(name = "accommodationRepository")
    private AccommodationRepository accommodationRepository;

    @Mock(name = "memberRepository")
    private MemberRepository memberRepository;

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

    @Test
    void insertReservation(){
        ReservationDto reservationDto = ReservationDto.builder()
                .memberId(1L)
                .accommodationId(1L)
                .checkInDate(LocalDate.of(2020, 3, 20))
                .checkoutDate(LocalDate.of(2020, 3, 22))
                .build();

        when(memberRepository.findById(reservationDto.getMemberId())).thenReturn(mockMember());
        when(accommodationRepository.findById(reservationDto.getAccommodationId())).thenReturn(mockFindAccommodation());
        when(dynamicReservationRepository.findByAccommodationIdAndDate(reservationDto.getAccommodationId(), LocalDate.of(2020, 3, 20), LocalDate.of(2020, 3, 22))).thenReturn(new ArrayList<>());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(setReservation());

        ReservationDto dto = setReservationDto();

        Reservation reservation = reservationService.insertReservation(dto);

        assertThat(reservation.getCheckInDate()).isEqualTo(LocalDate.of(2020, 2, 20));
        assertThat(reservation.getCheckoutDate()).isEqualTo(LocalDate.of(2020, 2, 22));
        assertThat(reservation.getGuestNum()).isEqualTo(3);
        assertThat(reservation.getTotalPrice()).isEqualTo(30000);
        assertThat(reservation.getReservationCode()).isEqualTo("Num20210206100000001");

        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(bookedDateRepository, times(2)).save(any(BookedDate.class));

    }

    @DisplayName("update 성공")
    @Test
    void updateReservationSuccess(){
        when(reservationRepository.findById(1L)).thenReturn(mockFindReservation(LocalDate.of(2020, 3, 20), LocalDate.of(2020, 3, 22)));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(setReservation());
        when(dynamicReservationRepository.findByAccommodationIdAndDate(1L, LocalDate.of(2020, 3, 20), LocalDate.of(2020, 3, 22))).thenReturn(new ArrayList<>());

        ReservationDto dto = setReservationDto();

        Reservation reservation = reservationService.updateReservation(1L, dto);

        verify(reservationRepository, times(1)).findById(1L);
        verify(bookedDateRepository, times(1)).deleteBookedDateByAccommodationIdAndDateIn(1L, mockDates());
        verify(dynamicReservationRepository, times(1)).findByAccommodationIdAndDate(1L, dto.getCheckInDate(), dto.getCheckoutDate());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(bookedDateRepository, times(0)).save(any(BookedDate.class));
    }

    @DisplayName("예약 날짜 중복으로 update 실패")
    @Test
    void updateReservationFail(){
        when(reservationRepository.findById(1L)).thenReturn(mockFindReservation(LocalDate.of(2020, 2, 20), LocalDate.of(2020, 2, 22)));
        when(dynamicReservationRepository.findByAccommodationIdAndDate(1L, LocalDate.of(2020, 2, 20), LocalDate.of(2020, 2, 22))).thenReturn(mockBookedDate());

        ReservationDto dto = setReservationDto();

        Reservation reservation = reservationService.updateReservation(1L, dto);

        verify(reservationRepository, times(1)).findById(1L);
        verify(bookedDateRepository, times(1)).deleteBookedDateByAccommodationIdAndDateIn(1L, mockDates());
        verify(dynamicReservationRepository, times(1)).findByAccommodationIdAndDate(1L, LocalDate.of(2020,2,20), LocalDate.of(2020,2,22));
        verify(bookedDateRepository, times(3)).save(any(BookedDate.class));
    }

    private ReservationDto setReservationDto() {
        ReservationDto dto = new ReservationDto();
        dto.setMemberId(1L);
        dto.setAccommodationId(1L);
        dto.setCheckInDate(LocalDate.of(2020, 3, 20));
        dto.setCheckoutDate(LocalDate.of(2020, 3, 22));
        dto.setGuestNum(3);
        dto.setTotalPrice(30000);

        return dto;
    }

    private Reservation setReservation(){
        return Reservation.builder().checkInDate(LocalDate.of(2020, 2, 20))
                .checkoutDate(LocalDate.of(2020, 2, 22))
                .guestNum(3)
                .totalPrice(30000)
                .reservationCode("Num20210206100000001")
                .build();
    }

    private Optional<Member> mockMember(){
        Member member = Member.builder()
                .id(1L)
                .email("test1@gmail.com")
                .password("1234")
                .birthDate(LocalDate.of(2020, 2, 11))
                .contact("01022223333")
                .name("tester")
                .role(MemberRole.MEMBER)
                .build();

        return Optional.of(member);
    }

    private List<LocalDate> mockDates() {

        List<LocalDate> localDates = new ArrayList<>();
        localDates.add(LocalDate.of(2020, 2, 20));
        localDates.add(LocalDate.of(2020, 2, 21));
        localDates.add(LocalDate.of(2020, 2, 22));

        return localDates;

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

    private Optional<Reservation> mockFindReservation(LocalDate checkInDate, LocalDate checkoutDate) {

        Accommodation accommodation = new Accommodation();
        accommodation.setId(1L);
        accommodation.setBathroomNum(2);
        accommodation.setBedroomNum(2);
        accommodation.setAccommodationType("집전체");
        accommodation.setBuildingType("아파트");

        Reservation reservation = new Reservation();

        reservation.setId(1L);
        reservation.setCheckInDate(checkInDate);
        reservation.setCheckoutDate(checkoutDate);
        reservation.setTotalPrice(50000);
        reservation.setGuestNum(5);
        reservation.setAccommodation(accommodation);
        reservation.setBookedDates(mockBookedDate());

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
        bookedDate1.setAccommodation(accommodation);

        bookedDates.add(bookedDate1);

        BookedDate bookedDate2 = new BookedDate();
        bookedDate2.setId(2L);
        bookedDate2.setDate(LocalDate.of(2020, 2, 21));
        bookedDate2.setAccommodation(accommodation);

        bookedDates.add(bookedDate2);

        BookedDate bookedDate3 = new BookedDate();
        bookedDate3.setDate(LocalDate.of(2020, 2, 22));
        bookedDate3.setId(3L);
        bookedDate3.setAccommodation(accommodation);

        bookedDates.add(bookedDate3);

        return bookedDates;

    }
}