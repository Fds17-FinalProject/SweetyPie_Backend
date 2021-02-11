package com.mip.sharebnb.dto;

import com.mip.sharebnb.model.AccommodationPicture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDto {

    private Long memberId;

    private Long accommodationId;

    private Long reservationId;

    @FutureOrPresent(message = "체크인은 현재날짜 이후의 날짜이어야 합니다.")
    private LocalDate checkInDate;

    @FutureOrPresent(message = "체크아웃은 현재날짜 이후의 날짜이어야 합니다.")
    private LocalDate checkoutDate;

    @Min(value = 1, message = "인원은 최소 1명입니다.") @Max(value = 8, message = "인원은 최대 8명입니다.")
    private int guestNum;

    @Positive(message = "총 비용이 맞지 않습니다.")
    private int totalPrice;

    private String hostName;

    private Boolean isWrittenReview;

    private String city;

    private String gu;

    private String title;

    private String hostName;

    private AccommodationPicture accommodationPicture;

}
