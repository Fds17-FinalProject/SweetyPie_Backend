package com.mip.sharebnb.service;

import com.mip.sharebnb.dto.AccommodationDto;
import com.mip.sharebnb.dto.ReservationDto;
import com.mip.sharebnb.model.Reservation;
import com.mip.sharebnb.repository.AccommodationRepository;
import com.mip.sharebnb.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final AccommodationRepository accommodationRepository;

    public List<ReservationDto> getReservations(Long memberId) {
        if (memberId == null){
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

    public Reservation updateReservation(Long id, ReservationDto reservationDto) {

        List<LocalDate> dates = setDate(reservationDto.getCheckInDate(), reservationDto.getCheckoutDate());

        return null;
    }

    private List<LocalDate> setDate(LocalDate checkInDate, LocalDate checkoutDate){
        List<LocalDate> dates = new ArrayList<>();
        for (LocalDate date = checkInDate; date.isBefore(checkoutDate); date.plusDays(1)) {
            dates.add(date);
        }

        return dates;
    }

    public AccommodationDto mappingAccommodationDto(Reservation reservation) {
        AccommodationDto accommodationDto = new AccommodationDto();
        accommodationDto.setCity(reservation.getAccommodation().getCity());
        accommodationDto.setGu(reservation.getAccommodation().getGu());
        accommodationDto.setAccommodationPictures(reservation.getAccommodation().getAccommodationPictures());

        return accommodationDto;

    }
}
