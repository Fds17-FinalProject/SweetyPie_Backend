package com.mip.sharebnb.service;

import com.mip.sharebnb.model.Reservation;
import com.mip.sharebnb.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public List<Reservation> getReservations(Long id) {
        if (id == null) {
            return new ArrayList<>();
        }
        List<Reservation> reservations = reservationRepository.findReservationByMemberId(id);

        if (reservations.isEmpty()) {
            return new ArrayList<>();
        }

        return reservations;

    }
}
