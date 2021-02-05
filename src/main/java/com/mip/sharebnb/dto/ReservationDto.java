package com.mip.sharebnb.dto;

import lombok.*;


import java.time.LocalDate;

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
