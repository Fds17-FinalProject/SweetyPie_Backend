package com.mip.sharebnb.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDto {

    private String memberId;

    private String accommodationId;

    @NotEmpty
    private String checkInDate;

    @NotEmpty
    private String checkoutDate;

    @NotEmpty
    private String guestNum;

    @NotEmpty
    private String totalPrice;
}
