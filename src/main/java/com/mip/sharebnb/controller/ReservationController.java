package com.mip.sharebnb.controller;

import com.mip.sharebnb.dto.ReservationDto;
import com.mip.sharebnb.model.Reservation;
import com.mip.sharebnb.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/reservation/{id}")
    public List<Reservation> getReservations(@PathVariable Long id) {
        if (id == null){
            return new ArrayList<>();
        }
        return reservationService.getReservations(id);
    }

    @PatchMapping("/reservation/{id}")
    public Reservation updateReservation(@PathVariable Long id, @RequestBody ReservationDto reservationDto) {
        return reservationService.updateReservation(id, reservationDto);
    }
}
