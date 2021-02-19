package com.sweetypie.sweetypie.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookedDateDto {

    private LocalDate startDate;

    private LocalDate endDate;

    @QueryProjection
    public BookedDateDto(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
