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

        List<ReservationDto> reservationDtoList = new ArrayList<>();

        for (Reservation reservation : reservations) {
            ReservationDto reservationDto = new ReservationDto();

            reservationDto.setAccommodationId(reservation.getAccommodation().getId());
            reservationDto.setReservationId(reservation.getId());
            reservationDto.setCheckInDate(reservation.getCheckInDate());
            reservationDto.setCheckoutDate(reservation.getCheckoutDate());
            reservationDto.setIsWrittenReview(reservation.getIsWrittenReview());
            reservationDto.setAccommodationDto(mappingAccommodationDto(reservation));

            reservationDtoList.add(reservationDto);
        }
        return reservationDtoList;
    }

    @Transactional
    public Reservation insertReservation(ReservationDto reservationDto) throws RuntimeException {
        if (reservationDto.getCheckoutDate().isBefore(reservationDto.getCheckInDate())){
            throw new InvalidInputException("예약 기간이 잘 못 되었습니다.");
        }

        Optional<Member> optionalMember = Optional.of(memberRepository.findById(reservationDto.getMemberId()).orElseThrow(() -> new DataNotFoundException("등록된 회원 정보를 찾을 수 없습니다.")));
        Member member = optionalMember.get();

        Optional<Accommodation> optionalAccommodation = Optional.of(accommodationRepository.findById(reservationDto.getAccommodationId()).orElseThrow(() -> new DataNotFoundException("등록된 숙박 정보를 찾을 수 없습니다.")));
        Accommodation accommodation = optionalAccommodation.get();

        List<BookedDate> bookedDates = new ArrayList<>();

        List<BookedDate> checkDuplicateDate = dynamicReservationRepository.findByAccommodationIdAndDate(reservationDto.getAccommodationId(), reservationDto.getCheckInDate(), reservationDto.getCheckoutDate());

        if (!checkDuplicateDate.isEmpty()){

            throw new DuplicateValueExeption("이미 예약된 날짜 입니다");
        } else {

            Reservation reservation = new Reservation();

            reservation.setCheckInDate(reservationDto.getCheckInDate());
            reservation.setCheckoutDate(reservationDto.getCheckoutDate());
            reservation.setGuestNum(reservationDto.getGuestNum());
            reservation.setTotalPrice(reservationDto.getTotalPrice());
            reservation.setCanceled(false);
            reservation.setPaymentDate(LocalDate.now());
            reservation.setMember(member);
            reservation.setAccommodation(accommodation);
            reservation.setReservationCode(setReservationCode(accommodation.getId(), member.getId()));

            for (LocalDate date = reservationDto.getCheckInDate(); date.isBefore(reservationDto.getCheckoutDate()); date = date.plusDays(1)) {
                bookedDates.add(setBookedDate(date, accommodation, reservation)); // 네이밍
            }
            return reservationRepository.save(reservation);
        }
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

    private AccommodationDto mappingAccommodationDto(Reservation reservation) {

        AccommodationDto accommodationDto = new AccommodationDto();

        accommodationDto.setCity(reservation.getAccommodation().getCity());
        accommodationDto.setGu(reservation.getAccommodation().getGu());
        accommodationDto.setTitle(reservation.getAccommodation().getTitle());
        accommodationDto.setAccommodationPictures(reservation.getAccommodation().getAccommodationPictures());

        return accommodationDto;
    }

    private BookedDate setBookedDate(LocalDate date, Accommodation accommodation, Reservation reservation) {

        BookedDate bookedDate = new BookedDate();
        bookedDate.setDate(date);
        bookedDate.setAccommodation(accommodation);
        bookedDate.setReservation(reservation);
        return bookedDate;
    }

    private String setReservationCode(Long accommodationId, Long memberId) {

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String strAccommodationId = String.format("%05d", accommodationId);
        String strMemberId = String.format("%05d", memberId);

        return today + strAccommodationId + strMemberId;
    }
}
