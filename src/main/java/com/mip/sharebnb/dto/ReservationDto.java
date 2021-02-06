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
    @DateTimeFormat // 형태 지정하는것 다시 보기
    private LocalDate checkInDate;

    @NonNull
    @DateTimeFormat
    private LocalDate checkoutDate;

    private int guestNum;

    private int totalPrice;

    private AccommodationDto accommodationDto;

}
