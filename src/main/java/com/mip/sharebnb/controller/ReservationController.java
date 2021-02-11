package com.mip.sharebnb.controller;

import com.mip.sharebnb.dto.ReservationDto;
import com.mip.sharebnb.exception.InvalidInputException;
import com.mip.sharebnb.model.Reservation;
import com.mip.sharebnb.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/reservation/{id}")
    public List<ReservationDto> getReservations(@PathVariable Long id) {

        return reservationService.getReservations(id);
    }

    @PostMapping("/reservation")
    public ResponseEntity<Reservation> makeAReservation(@Valid @RequestBody ReservationDto reservationDto){

        Reservation reservation = reservationService.makeAReservation(reservationDto);

        return new ResponseEntity<>(reservation, HttpStatus.OK);
    }

    @PatchMapping("/reservation/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @RequestBody ReservationDto reservationDto) {

        Reservation reservation = reservationService.updateReservation(id, reservationDto);

        return new ResponseEntity<>(reservation, HttpStatus.OK);
    }

    @DeleteMapping("/reservation/{id}")
    public void cancelReservation(@PathVariable Long id) {

        reservationService.deleteReservation(id);
    }
}
