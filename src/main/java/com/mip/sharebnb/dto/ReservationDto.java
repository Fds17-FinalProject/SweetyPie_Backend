package com.mip.sharebnb.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
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
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkInDate;

    @NonNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkoutDate;

    @Positive
    @Min(1)
    private int guestNum;

    @Positive
    private int totalPrice;

    private AccommodationDto accommodationDto;

}
