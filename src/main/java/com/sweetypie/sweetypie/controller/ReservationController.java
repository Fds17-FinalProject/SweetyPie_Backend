package com.sweetypie.sweetypie.controller;

import com.sweetypie.sweetypie.dto.ReservationDto;
import com.sweetypie.sweetypie.model.Reservation;
import com.sweetypie.sweetypie.security.jwt.TokenProvider;
import com.sweetypie.sweetypie.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReservationController {

    private final ReservationService reservationService;
    private final TokenProvider tokenProvider;

    @GetMapping("/reservation")
    @PreAuthorize("authenticated")
    public List<ReservationDto> getReservations(@RequestHeader("Authorization") String token) {

        return reservationService.getReservations(tokenProvider.parseTokenToGetUserId(token));
    }

    @PostMapping("/reservation")
    @PreAuthorize("authenticated")
    public ResponseEntity<Reservation> makeAReservation(@RequestHeader("Authorization") String token, @Valid @RequestBody ReservationDto reservationDto){

        Reservation reservation = reservationService.makeAReservation(tokenProvider.parseTokenToGetUserId(token), reservationDto);

        return new ResponseEntity<>(reservation, HttpStatus.OK);
    }

    @PatchMapping("/reservation/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @Valid @RequestBody ReservationDto reservationDto) {

        Reservation reservation = reservationService.updateReservation(id, reservationDto);

        return new ResponseEntity<>(reservation, HttpStatus.OK);
    }

    @DeleteMapping("/reservation/{id}")
    public ResponseEntity<Object> cancelReservation(@PathVariable Long id) {

        reservationService.deleteReservation(id);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
