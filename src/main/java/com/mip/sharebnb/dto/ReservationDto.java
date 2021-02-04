package com.mip.sharebnb.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class ReservationDto {

    private Long reservationId;

    @NonNull
    private LocalDate checkInDate;
    @NonNull
    private LocalDate checkoutDate;

    private AccommodationDto accommodationDto;
}
