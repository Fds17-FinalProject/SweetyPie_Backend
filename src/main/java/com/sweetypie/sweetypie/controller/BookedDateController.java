package com.sweetypie.sweetypie.controller;

import com.sweetypie.sweetypie.model.BookedDate;
import com.sweetypie.sweetypie.repository.BookedDateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BookedDateController {

    private final BookedDateRepository bookedDateRepository;

    @GetMapping("/bookDate/{id}")
    public BookedDate findById(@PathVariable Long id) {

        return bookedDateRepository.findById(id).orElse(BookedDate.emptyObject());
    }

    @GetMapping("/bookDates/{accommodationId}")
    public List<BookedDate> findBookedDatesByAccommodation_Id(@PathVariable Long accommodationId) {

        return bookedDateRepository.findBookedDatesByAccommodationId(accommodationId);
    }
}