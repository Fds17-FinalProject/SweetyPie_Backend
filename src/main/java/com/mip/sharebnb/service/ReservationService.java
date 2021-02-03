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

    public List<Reservation> getReservations(Long memberId) {
        if (memberId == null){
            return new ArrayList<>();
        }
        List<Reservation> reservations = reservationRepository.findReservationByMemberId(memberId);

        if (reservations.isEmpty()) {
            return new ArrayList<>();
        }

        return reservations;

    }

    @Transactional
    public Reservation updateReservation(Long id, ReservationDto reservationDto) {
        Optional<Reservation> optionalReservation = Optional.of(reservationRepository.findById(id).orElse(new Reservation()));

        Reservation findReservation = optionalReservation.get();

        Long accommodationId = findReservation.getAccommodation().getId();

        Optional<Accommodation> optionalAccommodation = Optional.of(accommodationRepository.findById(accommodationId).orElse(new Accommodation()));
        System.out.println(">>>>>>>>>>>>>> " + optionalAccommodation.get().getBuildingType());
        List<BookedDate> bookedDates = optionalAccommodation.get().getBookedDates();

        int guestNum = Integer.parseInt(reservationDto.getGuestNum());
        if (optionalAccommodation.get().getCapacity() < guestNum) {
            // 예외처리
            return new Reservation();
        }

        boolean flag = false;
        for (BookedDate date : bookedDates) {
            String bookedDate = String.valueOf(date.getDate());
            if (reservationDto.getCheckInDate().equals(bookedDate)) {
                flag = false;
                break;
            } else {
                flag = true;
            }
        }
        System.out.println("flag >>>>>>>> " + flag);

        if (flag){
            LocalDate updateCheckInDate =  LocalDate.parse(reservationDto.getCheckInDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            System.out.println("CheckInDate >>>>> " + updateCheckInDate);
            LocalDate updateCheckoutDate =  LocalDate.parse(reservationDto.getCheckoutDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            findReservation.setCheckInDate(updateCheckInDate);
            findReservation.setCheckoutDate(updateCheckoutDate);
            findReservation.setGuestNum(Integer.parseInt(reservationDto.getGuestNum()));
            findReservation.setTotalPrice(Integer.parseInt(reservationDto.getTotalPrice()));

            return reservationRepository.save(findReservation);
        } else {
            // 예외 처리
            return new Reservation();
        }

    }
}
