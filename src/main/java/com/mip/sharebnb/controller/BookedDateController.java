package com.mip.sharebnb.controller;

import com.mip.sharebnb.model.BookedDate;
import com.mip.sharebnb.repository.BookedDateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

        return bookedDateRepository.findBookedDatesByAccommodation_Id(accommodationId);
    }
}