package com.mip.sharebnb.service;

import com.mip.sharebnb.dto.AccommodationDto;
import com.mip.sharebnb.dto.ReservationDto;
import com.mip.sharebnb.model.BookedDate;
import com.mip.sharebnb.model.Reservation;
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

    public AccommodationDto mappingAccommodationDto(Reservation reservation) {

        AccommodationDto accommodationDto = new AccommodationDto();

        accommodationDto.setCity(reservation.getAccommodation().getCity());
        accommodationDto.setGu(reservation.getAccommodation().getGu());
        accommodationDto.setAccommodationPictures(reservation.getAccommodation().getAccommodationPictures());

        return accommodationDto;
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
}
