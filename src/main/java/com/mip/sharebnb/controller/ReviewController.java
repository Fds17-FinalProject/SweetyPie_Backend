package com.mip.sharebnb.controller;

import com.mip.sharebnb.dto.ReviewDto;
import com.mip.sharebnb.model.Review;
import com.mip.sharebnb.service.ReviewService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/review")
    public Review getReview(@RequestParam long memberId, @RequestParam long accommodationId) {

        return reviewService.findReviewByAccommodation_IdAndMember_Id(accommodationId, memberId);
    }

    @PostMapping("/review")
    public void postReview(@Valid @RequestBody ReviewDto reviewDto) throws NotFoundException {

        reviewService.writeReview(reviewDto);
    }

    @PutMapping("/review")
    public void updateReview(@Valid @RequestBody ReviewDto reviewDto) throws NotFoundException {

        reviewService.updateReview(reviewDto);
    }

    @DeleteMapping("/review/{id}")
    public void deleteReview(@PathVariable Long id) throws NotFoundException {

        reviewService.deleteReview(id);
    }
}