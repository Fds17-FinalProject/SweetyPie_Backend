package com.sweetypie.sweetypie.controller;

import com.sweetypie.sweetypie.dto.ReviewDto;
import com.sweetypie.sweetypie.model.Review;
import com.sweetypie.sweetypie.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@PreAuthorize("authenticated")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/review/{reservationId}")
    public Review getReview(@RequestHeader("Authorization") String token, @PathVariable long reservationId) {

        return reviewService.findReviewByReservationId(token, reservationId);
    }

    @PostMapping("/review")
    public void postReview(@RequestHeader("Authorization") String token, @Valid @RequestBody ReviewDto reviewDto) {

        reviewService.writeReview(token, reviewDto);
    }

    @PutMapping("/review")
    public void updateReview(@RequestHeader("Authorization") String token, @Valid @RequestBody ReviewDto reviewDto) {

        reviewService.updateReview(token, reviewDto);
    }

    @DeleteMapping("/review/{id}")
    public void deleteReview(@PathVariable Long id) {

        reviewService.deleteReview(id);
    }
}