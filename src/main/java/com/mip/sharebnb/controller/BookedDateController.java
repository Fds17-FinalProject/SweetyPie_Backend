package com.mip.sharebnb.controller;

import com.mip.sharebnb.dto.BookedDateDto;
import com.mip.sharebnb.model.BookedDate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BookedDateController {

    @PostMapping("/bookedDate")
    public BookedDate getBookedDates(@RequestBody BookedDateDto bookedDateDto) {

        return new BookedDate(bookedDateDto.getDate());
    }
}