package com.sweetypie.sweetypie.controller;

import com.sweetypie.sweetypie.dto.ReservationDto;
import com.sweetypie.sweetypie.security.jwt.TokenProvider;
import com.sweetypie.sweetypie.service.ReservationService;
import lombok.RequiredArgsConstructor;
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
    public List<ReservationDto> getReservations(@RequestHeader(value = "Authorization") String token) {

        return reservationService.getReservations(tokenProvider.parseTokenToGetUserId(token));
    }

    @PostMapping("/reservation")
    @PreAuthorize("authenticated")
    public void makeAReservation(@RequestHeader(value = "Authorization") String token, @Valid @RequestBody ReservationDto reservationDto){

        reservationService.makeAReservation(tokenProvider.parseTokenToGetUserId(token), reservationDto);

    }

    @PatchMapping("/reservation/{id}")
    @PreAuthorize("authenticated")
    public void updateReservation(@PathVariable Long id, @RequestHeader(value = "Authorization") String token, @Valid @RequestBody ReservationDto reservationDto) {

        reservationService.updateReservation(id, tokenProvider.parseTokenToGetUserId(token), reservationDto);

    }

    @DeleteMapping("/reservation/{id}")
    @PreAuthorize("authenticated")
    public void cancelReservation(@PathVariable Long id,  @RequestHeader(value = "Authorization") String token) {

        reservationService.deleteReservation(id, tokenProvider.parseTokenToGetUserId(token));

    }
}
