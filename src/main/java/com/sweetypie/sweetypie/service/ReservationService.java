package com.sweetypie.sweetypie.service;

import com.sweetypie.sweetypie.dto.ReservationDto;
import com.sweetypie.sweetypie.exception.DataNotFoundException;
import com.sweetypie.sweetypie.exception.DuplicateValueExeption;
import com.sweetypie.sweetypie.exception.InputNotValidException;
import com.sweetypie.sweetypie.model.Accommodation;
import com.sweetypie.sweetypie.model.BookedDate;
import com.sweetypie.sweetypie.model.Member;
import com.sweetypie.sweetypie.model.Reservation;
import com.sweetypie.sweetypie.repository.AccommodationRepository;
import com.sweetypie.sweetypie.repository.BookedDateRepository;
import com.sweetypie.sweetypie.repository.MemberRepository;
import com.sweetypie.sweetypie.repository.ReservationRepository;
import com.sweetypie.sweetypie.repository.dynamic.DynamicReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
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

    public Reservation makeAReservation(Long memberId, ReservationDto reservationDto) throws RuntimeException {

        handleCheckoutBeforeCheckInInputException(reservationDto);

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new DataNotFoundException("등록된 회원 정보를 찾을 수 없습니다."));

        Accommodation accommodation = accommodationRepository.findById(reservationDto.getAccommodationId()).orElseThrow(() -> new DataNotFoundException("등록된 숙박 정보를 찾을 수 없습니다."));

        validateTotalPrice(reservationDto, accommodation.getPrice());

        List<BookedDate> bookedDates = dynamicReservationRepository.findByAccommodationIdAndDate(accommodation.getId(), reservationDto.getCheckInDate(), reservationDto.getCheckoutDate());

        return checkDuplicateReservationDate(bookedDates, reservationDto, member, accommodation);
    }

    public Reservation updateReservation(Long reservationId, Long memberId, ReservationDto reservationDto) {

        handleCheckoutBeforeCheckInInputException(reservationDto);

        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new DataNotFoundException("예약 내역을 찾을 수 없습니다."));

        if (!memberId.equals(reservation.getMember().getId())) {
            throw new InputNotValidException("요청한 회원정보와 예약된 회원정보가 일치하지 않습니다");
        }

        validateTotalPrice(reservationDto, reservation.getAccommodation().getPrice());

        bookedDateRepository.deleteBookedDateByReservationId(reservationId);

        List<BookedDate> duplicateBookedDate = dynamicReservationRepository.findByAccommodationIdAndDate(reservation.getAccommodation().getId(), reservationDto.getCheckInDate(), reservationDto.getCheckoutDate());

        return updateCheckDuplicateBookedDate(duplicateBookedDate, reservation, reservationDto);

    }

    public void deleteReservation(Long reservationId, Long memberId) {

        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new DataNotFoundException("예약 내역을 찾을 수 없습니다."));

        if (!memberId.equals(reservation.getMember().getId())) {
            throw new InputNotValidException("요청한 회원정보와 예약된 회원정보가 일치하지 않습니다.");
        }

        reservationRepository.deleteById(reservationId);
    }

    private void handleCheckoutBeforeCheckInInputException(ReservationDto reservationDto) {
        if (reservationDto.getCheckoutDate().isBefore(reservationDto.getCheckInDate())) {

            throw new InputNotValidException("예약기간이 잘 못 되었습니다.");
        }
    }

    private List<ReservationDto> makeReservationDtoList(List<Reservation> reservations) {
        List<ReservationDto> reservationDtoList = new ArrayList<>();

        for (Reservation reservation : reservations) {
            ReservationDto reservationDto = new ReservationDto();
            reservationDto.setAccommodationId(reservation.getAccommodation().getId());
            reservationDto.setReservationId(reservation.getId());
            reservationDto.setCheckInDate(reservation.getCheckInDate());
            reservationDto.setCheckoutDate(reservation.getCheckoutDate());
            reservationDto.setIsWrittenReview(reservation.getIsWrittenReview());
            reservationDto.setTotalGuestNum(reservation.getTotalGuestNum());
            reservationDto.setAdultNum(reservation.getAdultNum());
            reservationDto.setChildNum(reservation.getChildNum());
            reservationDto.setInfantNum(reservation.getInfantNum());
            reservationDto.setCity(reservation.getAccommodation().getCity());
            reservationDto.setGu(reservation.getAccommodation().getGu());
            reservationDto.setTitle(reservation.getAccommodation().getTitle());
            reservationDto.setHostName(reservation.getAccommodation().getHostName());
            reservationDto.setBathroomNum(reservation.getAccommodation().getBathroomNum());
            reservationDto.setBedNum(reservation.getAccommodation().getBedNum());
            reservationDto.setBedroomNum(reservation.getAccommodation().getBedroomNum());
            reservationDto.setRatingAvg(reservation.getAccommodation().getRating());
            reservationDto.setReviewNum(reservation.getAccommodation().getReviewNum());
            reservationDto.setPricePerDay(reservation.getAccommodation().getPrice());
            reservationDto.setTotalPrice(reservation.getTotalPrice());
            reservationDto.setAccommodationPicture(reservation.getAccommodation().getAccommodationPictures().get(0));
            reservationDtoList.add(reservationDto);
        }
        return reservationDtoList;
    }

    private Reservation updateCheckDuplicateBookedDate(List<BookedDate> duplicateBookedDate, Reservation reservation, ReservationDto reservationDto) {
        if (duplicateBookedDate.isEmpty()) {

            setReservation(reservation, reservationDto);

            for (LocalDate date = reservationDto.getCheckInDate(); date.isBefore(reservationDto.getCheckoutDate()); date = date.plusDays(1)) {
                setBookedDate(date, reservation.getAccommodation(), reservation);
            }
            return reservationRepository.save(reservation);

        } else {

            throw new DuplicateValueExeption("이미 예약된 날짜입니다.");
        }
    }

    private Reservation checkDuplicateReservationDate(List<BookedDate> bookedDates, ReservationDto reservationDto, Member member, Accommodation accommodation) {

        if (bookedDates.isEmpty()) {
            Reservation reservation = new Reservation();
            setReservation(reservation, reservationDto);
            reservation.setMember(member);
            reservation.setAccommodation(accommodation);
            reservation.setPaymentDate(LocalDate.now());
            reservation.setReservationCode(setReservationCode(accommodation.getId(), member.getId()));

            for (LocalDate date = reservationDto.getCheckInDate(); date.isBefore(reservationDto.getCheckoutDate()); date = date.plusDays(1)) {
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

    private void setReservation(Reservation reservation, ReservationDto reservationDto) {
        reservation.setCheckInDate(reservationDto.getCheckInDate());
        reservation.setCheckoutDate(reservationDto.getCheckoutDate());
        reservation.setTotalGuestNum(reservationDto.getTotalGuestNum());
        reservation.setAdultNum(reservationDto.getAdultNum());
        reservation.setChildNum(reservationDto.getChildNum());
        reservation.setInfantNum(reservationDto.getInfantNum());
        reservation.setTotalPrice(reservationDto.getTotalPrice());
    }

    private void validateTotalPrice(ReservationDto reservationDto, int pricePerDay) {

        long night = ChronoUnit.DAYS.between(reservationDto.getCheckInDate(), reservationDto.getCheckoutDate());

        int totalNightPrice = pricePerDay * (int) night;
        int servicePrice = (int) Math.round(totalNightPrice * 0.07);

        int validTotalPrice = totalNightPrice + 10000 + servicePrice;

        if (reservationDto.getTotalPrice() != validTotalPrice) {
            throw new InputNotValidException("총 가격이 맞지 않습니다.");
        }
    }
}
