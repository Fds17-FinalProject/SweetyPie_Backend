package com.mip.sharebnb.service;

import com.mip.sharebnb.dto.ReservationDto;
import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.model.BookedDate;
import com.mip.sharebnb.model.Reservation;
import com.mip.sharebnb.repository.AccommodationRepository;
import com.mip.sharebnb.repository.ReservationRepository;
import com.querydsl.core.types.EntityPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final AccommodationRepository accommodationRepository;

    @PersistenceContext
    private EntityManager em;
    private JPAQuery<BookedDate> jpaQuery;

    public List<Reservation> getReservations(Long memberId) {
        if (memberId == null){
            return new ArrayList<>();
        }
        List<Reservation> reservations = reservationRepository.findReservationByMemberId(memberId);

        if (reservations.isEmpty()){
            return new ArrayList<>();
        }

        return reservations;

    }

    @Transactional
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
}
