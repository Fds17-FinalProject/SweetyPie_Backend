package com.mip.sharebnb.dto;

import com.mip.sharebnb.model.Accommodation;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class ReservationDto {

    private Long id;

    private Long memberId;

    private Long accommodationId;

    @NonNull
    private LocalDate checkInDate;

    @NonNull
    private LocalDate checkoutDate;

    private int guestNum;

    private int totalPrice;

    private Accommodation accommodation;

}
