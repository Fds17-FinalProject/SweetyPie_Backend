package com.mip.sharebnb.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReservationController {

    @GetMapping("/reservation/{id}")
    public void getReservations(@PathVariable Long id) {
        
    }
}
