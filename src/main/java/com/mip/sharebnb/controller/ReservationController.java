package com.mip.sharebnb.controller;

import com.mip.sharebnb.dto.ReservationDto;
import com.mip.sharebnb.model.Reservation;
import com.mip.sharebnb.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/reservation/{id}")
    public List<ReservationDto> getReservations(@PathVariable Long id) {

        if (id == null){
            //  예외처리
            return new ArrayList<>();
        }
        return reservationService.getReservations(id);
    }

    @PostMapping("/reservation")
    public Reservation makeAReservation(@Valid @RequestBody ReservationDto reservationDto) {
        return reservationService.insertReservation(reservationDto);
    }

    @PatchMapping("/reservation/{id}")
    public Reservation updateReservation(@PathVariable Long id, @RequestBody ReservationDto reservationDto){

        if (id == null){
            //  예외처리
            return new Reservation();
        }
        return reservationService.updateReservation(id, reservationDto);
    }

    @DeleteMapping("/reservation/{id}")
    public Reservation cancelReservation(@PathVariable Long id){

        return null;
    }
}
