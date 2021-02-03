package com.mip.sharebnb.service;

import com.mip.sharebnb.dto.ReservationDto;
import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.model.BookedDate;
import com.mip.sharebnb.model.Reservation;
import com.mip.sharebnb.repository.AccommodationRepository;
import com.mip.sharebnb.repository.ReservationRepository;
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
            reservationDto.setReservation(reservation);
            reservationDto.getAccommodationDto().setAccommodation(reservation.getAccommodation());
            reservationDto.getAccommodationDto().setAccommodationPictures(reservation.getAccommodation().getAccommodationPictures());

            reservationDtoList.add(reservationDto);
        }

        return reservationDtoList;

    }
}
