package com.mip.sharebnb.service;

import com.mip.sharebnb.dto.AccommodationDto;
import com.mip.sharebnb.dto.ReservationDto;
import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.model.BookedDate;
import com.mip.sharebnb.model.Member;
import com.mip.sharebnb.model.Reservation;
import com.mip.sharebnb.repository.AccommodationRepository;
import com.mip.sharebnb.repository.MemberRepository;
import com.mip.sharebnb.repository.ReservationRepository;

import com.mip.sharebnb.repository.dynamic.DynamicReservationRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<ReservationDto> getReservations(Long memberId) {
        if (memberId == null) {
            return new ArrayList<>();
        }
        List<Reservation> reservations = reservationRepository.findReservationByMemberId(memberId);

        List<ReservationDto> reservationDtoList = new ArrayList<>();

        for (Reservation reservation : reservations) {

            ReservationDto reservationDto = new ReservationDto();

            reservationDto.setReservationId(reservation.getId());
            reservationDto.setCheckInDate(reservation.getCheckInDate());
            reservationDto.setCheckoutDate(reservation.getCheckoutDate());
            reservationDto.setAccommodationDto(mappingAccommodationDto(reservation));

            reservationDtoList.add(reservationDto);
        }
        return reservationDtoList;
    }

    public Reservation insertReservation(ReservationDto reservationDto) {

        Optional<Member> optionalMember = Optional.of(memberRepository.findById(reservationDto.getMemberId()).orElse(new Member()));
        Member member = optionalMember.get();

        Optional<Accommodation> optionalAccommodation = Optional.of(accommodationRepository.findById(reservationDto.getAccommodationId()).orElse(new Accommodation()));

        Accommodation accommodation = optionalAccommodation.get();

        List<BookedDate> checkDuplicateDate = dynamicReservationRepository.findByReservationIdAndDate(reservationDto.getReservationId(), reservationDto.getAccommodationId(), reservationDto.getCheckInDate(), reservationDto.getCheckoutDate());

        Reservation reservation = new Reservation();
//        if (checkDuplicateDate.isEmpty()) {
//        }

        return new Reservation();
    }

    @Transactional
    public Reservation updateReservation(Long id, ReservationDto reservationDto) {

        Optional<Reservation> findReservation = Optional.of(reservationRepository.findById(id).orElse(new Reservation()));

        Reservation reservation = findReservation.get();

        System.out.println("reservation >>>" + reservation.toString());

        // accommodation의 날짜를 비교해야 함
        List<BookedDate> reservations = dynamicReservationRepository.findByReservationIdAndDate(id, reservation.getAccommodation().getId(), reservationDto.getCheckInDate(), reservationDto.getCheckoutDate());

        if (reservations.isEmpty()) {
            reservation.setCheckInDate(reservationDto.getCheckInDate());
            reservation.setCheckoutDate(reservationDto.getCheckoutDate());
            reservation.setGuestNum(reservationDto.getGuestNum());
            reservation.setTotalPrice(reservationDto.getTotalPrice());

            Reservation result = reservationRepository.save(reservation);
            return result;
        }

        return new Reservation();
    }


    public AccommodationDto mappingAccommodationDto(Reservation reservation) {

        AccommodationDto accommodationDto = new AccommodationDto();

        accommodationDto.setCity(reservation.getAccommodation().getCity());
        accommodationDto.setGu(reservation.getAccommodation().getGu());
        accommodationDto.setAccommodationPictures(reservation.getAccommodation().getAccommodationPictures());

        return accommodationDto;
    }
}
