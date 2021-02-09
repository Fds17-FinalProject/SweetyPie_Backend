package com.mip.sharebnb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {

    @Positive
    private long memberId;

    @Positive
    private long accommodationId;

    @PositiveOrZero
    private float rating;

    @NotEmpty
    private String content;
}
