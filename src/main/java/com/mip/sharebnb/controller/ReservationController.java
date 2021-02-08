package com.mip.sharebnb.controller;

import com.mip.sharebnb.dto.ErrorDto;
import com.mip.sharebnb.dto.ReservationDto;
import com.mip.sharebnb.exception.DuplicateDateException;
import com.mip.sharebnb.exception.NotFoundAccommodation;
import com.mip.sharebnb.exception.NotFoundMemberException;
import com.mip.sharebnb.exception.UnValidException;
import com.mip.sharebnb.exceptionhandler.ReservationHandler;
import com.mip.sharebnb.model.Reservation;
import com.mip.sharebnb.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
    public ResponseEntity<Object> makeAReservation(@Valid @RequestBody ReservationDto reservationDto, Errors errors) throws NotFoundMemberException {
        if (errors.hasErrors()) {
            throw new UnValidException("입력한 타입 및 값이 맞지 않습니다.");
        }

        reservationService.insertReservation(reservationDto);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/reservation/{id}")
    public Reservation updateReservation(@PathVariable Long id, @RequestBody ReservationDto reservationDto) {

        return reservationService.updateReservation(id, reservationDto);
    }

    @DeleteMapping("/reservation/{id}")
    public void cancelReservation(@PathVariable Long id) {

        reservationService.deleteReservation(id);
    }
}
