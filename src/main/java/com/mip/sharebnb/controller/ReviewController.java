package com.mip.sharebnb.controller;

import com.mip.sharebnb.model.Review;
import com.mip.sharebnb.service.ReviewService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/review")
    public Review getWrittenReview(@RequestParam long memberId, @RequestParam long accommodationId) throws NotFoundException {

        return reviewService.findReviewByAccommodation_IdAndMember_Id(accommodationId, memberId).orElseThrow(() -> new NotFoundException("Not Found Review"));
    }


}
