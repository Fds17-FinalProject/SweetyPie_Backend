package com.mip.sharebnb.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDto {

    private Long memberId;

    private Long accommodationId;

    @NonNull
    private LocalDate checkInDate;

    @NonNull
    private LocalDate checkoutDate;

    private int guestNum;

    private int totalPrice;
}
