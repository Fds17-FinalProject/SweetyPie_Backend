package com.mip.sharebnb.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class ReservationDto {

    private Long memberId;

    private Long accommodationId;

    private Long reservationId;

    @NonNull
    private LocalDate checkInDate;

    @NonNull
    private LocalDate checkoutDate;

    private int guestNum;

    private int totalPrice;

    private AccommodationDto accommodationDto;

}
