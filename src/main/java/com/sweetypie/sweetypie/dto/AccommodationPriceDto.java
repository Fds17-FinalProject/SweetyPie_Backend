package com.sweetypie.sweetypie.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class AccommodationPriceDto {

    int price;

    @QueryProjection
    public AccommodationPriceDto(int price) {
        this.price = price;
    }
}
