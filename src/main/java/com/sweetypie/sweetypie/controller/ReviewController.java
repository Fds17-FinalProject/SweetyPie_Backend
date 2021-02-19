package com.sweetypie.sweetypie.controller;

import com.sweetypie.sweetypie.dto.ReviewDto;
import com.sweetypie.sweetypie.model.Review;
import com.sweetypie.sweetypie.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequiredArgsConstructor
//@PreAuthorize("hasRole('MEMBER')")
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/review/{reservationId}")
    public Review getReview(@PathVariable long reservationId) {

        return reviewService.findReviewByReservationId(reservationId);
    }

    @PostMapping("/review")
    public void postReview(@Valid @RequestBody ReviewDto reviewDto) {

        reviewService.writeReview(reviewDto);
    }

    @PutMapping("/review")
    public void updateReview(@Valid @RequestBody ReviewDto reviewDto) {

        reviewService.updateReview(reviewDto);
    }

    @DeleteMapping("/review/{id}")
    public void deleteReview(@PathVariable Long id) {

        reviewService.deleteReview(id);
    }
}