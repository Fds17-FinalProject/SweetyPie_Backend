package com.sweetypie.sweetypie.service;

import com.sweetypie.sweetypie.dto.ReservationDto;
import com.sweetypie.sweetypie.exception.DataNotFoundException;
import com.sweetypie.sweetypie.exception.DuplicateValueExeption;
import com.sweetypie.sweetypie.exception.InputNotValidException;
import com.sweetypie.sweetypie.model.*;
import com.sweetypie.sweetypie.repository.AccommodationRepository;
import com.sweetypie.sweetypie.repository.BookedDateRepository;
import com.sweetypie.sweetypie.repository.MemberRepository;
import com.sweetypie.sweetypie.repository.ReservationRepository;
import com.sweetypie.sweetypie.repository.dynamic.DynamicReservationRepository;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BookedDateRepository bookedDateRepository;

    @Mock
    private DynamicReservationRepository dynamicReservationRepository;

    @DisplayName("예약 내역 조회")
    @Test
    void getReservationByMemberId() {
        when(reservationRepository.findReservationByMemberId(1L)).thenReturn(mockReservations());

        List<ReservationDto> reservationDtoList = reservationService.getReservations(1L);

        assertThat(reservationDtoList.size()).isEqualTo(1);
        assertThat(reservationDtoList.get(0).getAccommodationId()).isEqualTo(1);
        assertThat(reservationDtoList.get(0).getCheckInDate()).isEqualTo("2020-02-22");
        assertThat(reservationDtoList.get(0).getCheckoutDate()).isEqualTo("2020-02-24");
        assertThat(reservationDtoList.get(0).getCity()).isEqualTo("서울시");
        assertThat(reservationDtoList.get(0).getGu()).isEqualTo("강남구");
        assertThat(reservationDtoList.get(0).getIsWrittenReview()).isEqualTo(false);
        assertThat(reservationDtoList.get(0).getAccommodationPicture().getUrl()).isEqualTo("picture");
    }

    @DisplayName("예약 내역이 없음")
    @Test
    void getReservationByMemberIdEmpty() {
        when(reservationRepository.findReservationByMemberId(1L)).thenReturn(new ArrayList<>());

        List<Reservation> reservations = reservationRepository.findReservationByMemberId(1L);

        assertThat(reservations.isEmpty()).isTrue();
    }

    @DisplayName("예약하기 성공")
    @Test
    void makeAReservation(){

        when(memberRepository.findById(1L)).thenReturn(mockMember());
        when(accommodationRepository.findById(1L)).thenReturn(mockAccommodation());
        when(dynamicReservationRepository.findByAccommodationIdAndDate(1L, LocalDate.of(2022, 3, 20), LocalDate.of(2022, 3, 22))).thenReturn(new ArrayList<>());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(mockReservation());

        ReservationDto dto = mockReservationDto(LocalDate.of(2022,3,20), LocalDate.of(2022,3,22), 95600);

        Reservation reservation = reservationService.makeAReservation(1L, dto);

        assertThat(reservation.getCheckInDate()).isEqualTo(LocalDate.of(2022, 3, 20));
        assertThat(reservation.getCheckoutDate()).isEqualTo(LocalDate.of(2022, 3, 22));
        assertThat(reservation.getTotalGuestNum()).isEqualTo(3);
        assertThat(reservation.getTotalPrice()).isEqualTo(95600);
        assertThat(reservation.getReservationCode()).isEqualTo("202102070000100001");

        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @DisplayName("회원 정보를 찾을 수 없습니다.")
    @Test
    void makeAReservationIfNotFoundMemberInfo(){
        lenient().when(accommodationRepository.findById(1L)).thenReturn(Optional.of(new Accommodation()));

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class, () -> reservationService.makeAReservation(1L,mockReservationDto(LocalDate.of(2022,3,20), LocalDate.of(2022,3,22), 95600)));

        assertThat(dataNotFoundException.getMessage()).isEqualTo("등록된 회원 정보를 찾을 수 없습니다.");
    }

    @DisplayName("숙박 정보를 찾을 수 없습니다.")
    @Test
    void makeAReservationIfNotFoundAccommodationInfo(){
        when(memberRepository.findById(1L)).thenReturn(mockMember());

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class, () -> reservationService.makeAReservation(1L,mockReservationDto(LocalDate.of(2022,3,20), LocalDate.of(2022,3,22), 95600)));

        assertThat(dataNotFoundException.getMessage()).isEqualTo("등록된 숙박 정보를 찾을 수 없습니다.");
    }

    @DisplayName("예약하기 총 가격 일치하지 않는 예외")
    @Test
    void makeAReservationIfTotalPriceMisMatch(){

        when(memberRepository.findById(1L)).thenReturn(mockMember());
        when(accommodationRepository.findById(1L)).thenReturn(mockAccommodation());

        InputNotValidException inputNotValidException = assertThrows(InputNotValidException.class, () -> reservationService.makeAReservation(1L, mockReservationDto(LocalDate.of(2022,2,20), LocalDate.of(2022,2,22), 10000)));

        assertThat(inputNotValidException.getMessage()).isEqualTo("총 가격이 맞지 않습니다.");

    }

    @DisplayName("예약하기 중복된 예약날짜 예외")
    @Test
    void makeAReservationIfDuplicateReservedDate(){
        when(memberRepository.findById(1L)).thenReturn(mockMember());
        when(accommodationRepository.findById(1L)).thenReturn(mockAccommodation());
        when(dynamicReservationRepository.findByAccommodationIdAndDate(1L, LocalDate.of(2022, 3, 20), LocalDate.of(2022, 3, 22))).thenReturn(mockBookedDate());

        ReservationDto reservationDto = mockReservationDto(LocalDate.of(2022,3,20), LocalDate.of(2022,3,22), 95600);

        DuplicateValueExeption duplicateValueExeption = assertThrows(DuplicateValueExeption.class, () -> reservationService.makeAReservation(1L, reservationDto));

        assertThat(duplicateValueExeption.getMessage()).isEqualTo("이미 예약된 날짜입니다.");
    }

    @DisplayName("예약하기 체크아웃날짜가 체크인날짜보다 빠른날자로 입력되었을 때 예외")
    @Test
    void makeAReservationIfEnteredCheckoutDateEarlierThanCheckInDate(){

        InputNotValidException inputNotValidException = assertThrows(InputNotValidException.class, () -> reservationService.makeAReservation(1L, mockReservationDto(LocalDate.of(2022,2,22), LocalDate.of(2022,2,20), 95600)));

        assertThat(inputNotValidException.getMessage()).isEqualTo("예약기간이 잘 못 되었습니다.");
    }

    @DisplayName("예약 할 때, 숙박 수용인원보다 예약한 최대인원이 더 많을 때 예외")
    @Test
    void makeAReservationIfTotalGuestNumGreaterThanCapacity(){
        when(memberRepository.findById(1L)).thenReturn(mockMember());
        when(accommodationRepository.findById(1L)).thenReturn(mockAccommodation());

        ReservationDto reservationDto = ReservationDto.builder()
                .accommodationId(1L)
                .checkInDate(LocalDate.of(2022, 3, 20))
                .checkoutDate(LocalDate.of(2022, 3, 22))
                .totalPrice(95600)
                .totalGuestNum(8)
                .adultNum(4)
                .childNum(4)
                .infantNum(0)
                .build();

        InputNotValidException inputNotValidException = assertThrows(InputNotValidException.class, () -> reservationService.makeAReservation(1L, reservationDto));

        assertThat(inputNotValidException.getMessage()).isEqualTo("숙소의 수용 가능한 최대 인원을 초과하였습니다.");
    }

    @DisplayName("예약수정 성공")
    @Test
    void updateReservationSuccess() {

        when(reservationRepository.findById(1L)).thenReturn(mockFindReservation());
        when(dynamicReservationRepository.findByAccommodationIdAndDate(1L, LocalDate.of(2022, 3, 20), LocalDate.of(2022, 3, 22))).thenReturn(new ArrayList<>());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(mockReservation());

        ReservationDto dto = mockReservationDto(LocalDate.of(2022,3,20), LocalDate.of(2022,3,22), 95600);

        Reservation reservation = reservationService.updateReservation(1L, 1L, dto);

        assertThat(reservation.getCheckInDate()).isEqualTo(dto.getCheckInDate());
        assertThat(reservation.getCheckoutDate()).isEqualTo(dto.getCheckoutDate());
        assertThat(reservation.getTotalGuestNum()).isEqualTo(dto.getTotalGuestNum());
        assertThat(reservation.getTotalPrice()).isEqualTo(dto.getTotalPrice());

        verify(bookedDateRepository, times(1)).deleteBookedDateByReservationId(1L);

    }

    @DisplayName("예약 날짜 중복으로 예약수정 실패")
    @Test
    void updateReservationIfDuplicateReservedDate(){
        when(reservationRepository.findById(1L)).thenReturn(mockFindReservation());
        when(dynamicReservationRepository.findByAccommodationIdAndDate(1L, LocalDate.of(2022, 3, 20), LocalDate.of(2022, 3, 22))).thenReturn(mockBookedDate());

        ReservationDto dto = mockReservationDto(LocalDate.of(2022,3,20), LocalDate.of(2022,3,22), 95600);

        DuplicateValueExeption duplicateValueExeption = assertThrows(DuplicateValueExeption.class, () -> reservationService.updateReservation(1L, 1L, dto));

        assertThat(duplicateValueExeption.getMessage()).isEqualTo("이미 예약된 날짜입니다.");
    }

    @DisplayName("예약수정시 총 가격이 일치하지 않는 예외")
    @Test
    void updateReservationIfTotalPriceMisMatch(){

        when(reservationRepository.findById(1L)).thenReturn(mockFindReservation());

        InputNotValidException inputNotValidException = assertThrows(InputNotValidException.class, () -> reservationService.updateReservation(1L, 1L, mockReservationDto(LocalDate.of(2022, 2, 20), LocalDate.of(2022, 2, 22), 10000)));

        assertThat(inputNotValidException.getMessage()).isEqualTo("총 가격이 맞지 않습니다.");
    }

    @DisplayName("예약수정시 요청한 회원정보와 예약된 회원정보 불일치 예외")
    @Test
    void updateReservationRequestMemberNotEqualReservedMember() {

        when(reservationRepository.findById(1L)).thenReturn(mockFindReservation());

        ReservationDto dto = mockReservationDto(LocalDate.of(2022,3,20), LocalDate.of(2022,3,22), 95600);

        InputNotValidException inputNotValidException = assertThrows(InputNotValidException.class, () -> reservationService.updateReservation(1L, 2L, dto));

        assertThat(inputNotValidException.getMessage()).isEqualTo("요청한 회원정보와 예약된 회원정보가 일치하지 않습니다.");

    }

    @DisplayName("예약 수정시 체크아웃이 체크인보다 빠른 날짜로 들어올 때 예외")
    @Test
    void updateReservationIfEnteredCheckoutDateEarlierThanCheckInDate(){

        InputNotValidException inputNotValidException = assertThrows(InputNotValidException.class, () -> reservationService.updateReservation(1L, 1L, mockReservationDto(LocalDate.of(2022,2,22), LocalDate.of(2022,2,20), 95600)));

        assertThat(inputNotValidException.getMessage()).isEqualTo("예약기간이 잘 못 되었습니다.");
    }

    @DisplayName("예약 취소")
    @Test
    void deleteReservation(){

        when(reservationRepository.findById(1L)).thenReturn(mockFindReservation());

        reservationService.deleteReservation(1L, 1L);

        verify(reservationRepository, times(1)).findById(1L);
        verify(reservationRepository, times(1)).deleteById(1L);

    }

    @DisplayName("예약 취소시 요청한 회원정보와 예약된 회원정보 불일치 예외")
    @Test
    void deleteReservationDiscrepancyMemberInformation (){

        when(reservationRepository.findById(1L)).thenReturn(mockFindReservation());

        InputNotValidException inputNotValidException = assertThrows(InputNotValidException.class, () -> reservationService.deleteReservation(1L, 2L));

        assertThat(inputNotValidException.getMessage()).isEqualTo("요청한 회원정보와 예약된 회원정보가 일치하지 않습니다.");

    }

    // getReservations
    private List<Reservation> mockReservations() {
        List<AccommodationPicture> accommodationPictures = new ArrayList<>();
        AccommodationPicture firAccommodationPicture = new AccommodationPicture();
        firAccommodationPicture.setUrl("picture");

        accommodationPictures.add(firAccommodationPicture);

        AccommodationPicture secAccommodationPicture = new AccommodationPicture();
        secAccommodationPicture.setUrl("photo");

        accommodationPictures.add(secAccommodationPicture);

        List<Reservation> reservations = new ArrayList<>();
        Accommodation accommodation = new Accommodation();
        accommodation.setId(1L);
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
        reservation.setTotalGuestNum(5);
        reservation.setWrittenReview(false);
        reservation.setAccommodation(accommodation);

        reservations.add(reservation);

        return reservations;
    }

    // makeAReservation
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

    private Optional<Accommodation> mockAccommodation() {

        BookedDate bookedDate = new BookedDate();
        bookedDate.setDate(LocalDate.of(2020,2,20));
        bookedDate.setDate(LocalDate.of(2020, 2, 21));

        List<BookedDate> bookedDates = new ArrayList<>();
        bookedDates.add(bookedDate);

        Accommodation accommodation = new Accommodation();
        accommodation.setId(1L);
        accommodation.setBathroomNum(2);
        accommodation.setBedroomNum(2);
        accommodation.setPrice(40000);
        accommodation.setAccommodationType("집전체");
        accommodation.setBuildingType("게스트하우스");
        accommodation.setBookedDates(bookedDates);
        accommodation.setCapacity(5);

        return Optional.of(accommodation);
    }

    private Reservation mockReservation(){

        Reservation reservation = new Reservation();

        reservation.setCheckInDate(LocalDate.of(2022, 3, 20));
        reservation.setCheckoutDate(LocalDate.of(2022, 3, 22));
        reservation.setTotalGuestNum(3);
        reservation.setAdultNum(2);
        reservation.setChildNum(1);
        reservation.setInfantNum(0);
        reservation.setTotalPrice(95600);
        reservation.setReservationCode("202102070000100001");

        return reservation;
    }

    // updateReservation
    private ReservationDto mockReservationDto(LocalDate checkIn, LocalDate checkout, int totalPrice) {
        ReservationDto dto = new ReservationDto();
        dto.setAccommodationId(1L);
        dto.setCheckInDate(checkIn);
        dto.setCheckoutDate(checkout);
        dto.setTotalGuestNum(3);
        dto.setAdultNum(2);
        dto.setChildNum(1);
        dto.setInfantNum(0);
        dto.setTotalPrice(totalPrice);

        return dto;
    }

    private Optional<Reservation> mockFindReservation() {
        Member member = new Member();
        member.setId(1L);
        member.setEmail("test123@gmail.com");
        member.setPassword("1234");
        member.setName("테스터");
        member.setContact("01022223333");

        Accommodation accommodation = new Accommodation();
        accommodation.setId(1L);
        accommodation.setBathroomNum(2);
        accommodation.setBedroomNum(2);
        accommodation.setPrice(40000);
        accommodation.setAccommodationType("집전체");
        accommodation.setBuildingType("아파트");

        Reservation reservation = new Reservation();

        reservation.setId(1L);
        reservation.setCheckInDate(LocalDate.of(2022,2,20));
        reservation.setCheckoutDate(LocalDate.of(2022,2,22));
        reservation.setTotalPrice(95600);
        reservation.setTotalGuestNum(5);
        reservation.setAccommodation(accommodation);
        reservation.setBookedDates(mockBookedDate());
        reservation.setMember(member);

        return Optional.of(reservation);
    }

    private List<BookedDate> mockBookedDate(){

        Reservation reservation = new Reservation();
        reservation.setId(1L);

        Accommodation accommodation = Accommodation.builder().id(1L).build();

        List<BookedDate> bookedDates = new ArrayList<>();

        BookedDate bookedDate1 = new BookedDate();
        bookedDate1.setId(1L);
        bookedDate1.setDate(LocalDate.of(2022, 2, 20));
        bookedDate1.setAccommodation(accommodation);
        bookedDate1.setReservation(reservation);

        bookedDates.add(bookedDate1);

        BookedDate bookedDate2 = new BookedDate();
        bookedDate2.setId(2L);
        bookedDate2.setDate(LocalDate.of(2022, 2, 21));
        bookedDate2.setAccommodation(accommodation);
        bookedDate2.setReservation(reservation);

        bookedDates.add(bookedDate2);

        return bookedDates;

    }
}