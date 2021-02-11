package com.mip.sharebnb.service;

import com.mip.sharebnb.dto.AccommodationDto;
import com.mip.sharebnb.dto.ReservationDto;
import com.mip.sharebnb.exception.*;
import com.mip.sharebnb.model.*;
import com.mip.sharebnb.repository.*;

import com.mip.sharebnb.repository.dynamic.DynamicReservationRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final DynamicReservationRepository dynamicReservationRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final AccommodationRepository accommodationRepository;
    private final BookedDateRepository bookedDateRepository;

    public List<ReservationDto> getReservations(Long memberId) {
        List<Reservation> reservations = reservationRepository.findReservationByMemberId(memberId);

        return makeReservationDtoList(reservations);
    }

    @Transactional
    public Reservation makeAReservation(ReservationDto reservationDto) throws RuntimeException {
        if (reservationDto.getCheckoutDate().isBefore(reservationDto.getCheckInDate())) {
            throw new InvalidInputException("예약기간이 올바르지 않습니다");
        }

        Member member = memberRepository.findById(reservationDto.getMemberId()).orElseThrow(() -> new DataNotFoundException("등록된 회원 정보를 찾을 수 없습니다"));

        Accommodation accommodation = accommodationRepository.findById(reservationDto.getAccommodationId()).orElseThrow(() -> new DataNotFoundException("등록된 숙박 정보를 찾을 수 없습니다"));

        List<BookedDate> bookedDates = dynamicReservationRepository.findByAccommodationIdAndDate(accommodation.getId(), reservationDto.getCheckInDate(), reservationDto.getCheckoutDate());

        return checkDuplicateReservationDate(bookedDates, reservationDto, member, accommodation);
    }

    @Transactional
    public Reservation updateReservation(Long reservationId, ReservationDto reservationDto) {

        if (reservationDto.getCheckoutDate().isBefore(reservationDto.getCheckInDate())){
            throw new InvalidInputException("예약 기간이 잘 못 되었습니다.");
        }

        Optional<Reservation> originOptionalReservation = Optional.of(reservationRepository.findById(reservationId).orElseThrow(() -> new DataNotFoundException("등록된 예약 정보를 찾을 수 없습니다.")));
        Reservation originReservation = originOptionalReservation.get();

        List<BookedDate> originReservationBookedDates = originReservation.getBookedDates();
        List<LocalDate> localDates = new ArrayList<>();

        for (BookedDate reservationBookedDate : originReservationBookedDates) {
            localDates.add(reservationBookedDate.getDate());
        }

        Long accommodationId = originReservation.getAccommodation().getId();
        LocalDate checkInDate = reservationDto.getCheckInDate();
        LocalDate checkoutDate = reservationDto.getCheckoutDate();

        bookedDateRepository.deleteBookedDateByAccommodationIdAndDateIn(accommodationId, localDates);

        List<BookedDate> duplicateDates = dynamicReservationRepository.findByAccommodationIdAndDate(accommodationId, checkInDate, checkoutDate);

        if (!duplicateDates.isEmpty()){

            throw new DuplicateValueExeption("이미 예약된 날짜입니다.");
        }else {
            originReservation.setCheckInDate(reservationDto.getCheckInDate());
            originReservation.setCheckoutDate(reservationDto.getCheckoutDate());
            originReservation.setGuestNum(reservationDto.getGuestNum());
            originReservation.setTotalPrice(reservationDto.getTotalPrice());
            List<BookedDate> bookedDates = new ArrayList<>();
            for (LocalDate date = reservationDto.getCheckInDate(); date.isBefore(reservationDto.getCheckoutDate()); date = date.plusDays(1)) {

                setBookedDate(date, originReservation.getAccommodation(), originReservation); // 네이밍

            }
            return reservationRepository.save(originReservation);
        }
    }

    @Transactional
    public void deleteReservation(Long reservationId) {

        reservationRepository.deleteById(reservationId);
    }

    private List<ReservationDto> makeReservationDtoList(List<Reservation> reservations){
        List<ReservationDto> reservationDtoList = new ArrayList<>();

        for (Reservation reservation : reservations) {
            ReservationDto reservationDto = new ReservationDto();
            reservationDto.setAccommodationId(reservation.getAccommodation().getId());
            reservationDto.setReservationId(reservation.getId());
            reservationDto.setCheckInDate(reservation.getCheckInDate());
            reservationDto.setCheckoutDate(reservation.getCheckoutDate());
            reservationDto.setIsWrittenReview(reservation.getIsWrittenReview());
            reservationDto.setCity(reservation.getAccommodation().getCity());
            reservationDto.setGu(reservation.getAccommodation().getGu());
            reservationDto.setTitle(reservation.getAccommodation().getTitle());
            reservationDto.setHostName(reservation.getAccommodation().getHostName());
            reservationDto.setAccommodationPicture(reservation.getAccommodation().getAccommodationPictures().get(0));
            reservationDtoList.add(reservationDto);
        }
        return reservationDtoList;
    }

    private Reservation checkDuplicateReservationDate(List<BookedDate> bookedDates, ReservationDto reservationDto, Member member, Accommodation accommodation) {

        if (bookedDates.isEmpty()) {
            Reservation reservation = new Reservation();
            reservation.setCheckInDate(reservationDto.getCheckInDate());
            reservation.setCheckoutDate(reservationDto.getCheckoutDate());
            reservation.setGuestNum(reservationDto.getGuestNum());
            reservation.setTotalPrice(reservationDto.getTotalPrice());
            reservation.setMember(member);
            reservation.setAccommodation(accommodation);
            reservation.setPaymentDate(LocalDate.now());
            reservation.setCanceled(false);
            reservation.setReservationCode(setReservationCode(reservationDto.getAccommodationId(), reservationDto.getMemberId()));

            for (LocalDate date = reservationDto.getCheckInDate(); date.isBefore(reservationDto.getCheckoutDate()) ; date = date.plusDays(1)) {
                setBookedDate(date, accommodation, reservation);
            }

            return reservationRepository.save(reservation);
        } else {
            throw new DuplicateValueExeption("이미 예약된 날짜입니다.");
        }
    }

    private void setBookedDate(LocalDate date, Accommodation accommodation, Reservation reservation) {

        BookedDate bookedDate = new BookedDate();
        bookedDate.setDate(date);
        bookedDate.setAccommodation(accommodation);
        bookedDate.setReservation(reservation);

    }

    private String setReservationCode(Long accommodationId, Long memberId) {

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String strAccommodationId = String.format("%05d", accommodationId);
        String strMemberId = String.format("%05d", memberId);

        return today + strAccommodationId + strMemberId;
    }
}
