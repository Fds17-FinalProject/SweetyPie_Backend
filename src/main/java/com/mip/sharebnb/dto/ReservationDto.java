package com.mip.sharebnb.dto;

import com.mip.sharebnb.model.AccommodationPicture;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
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

    private Boolean isWrittenReview;

    private String city;

    private String gu;

    private String title;

    private String hostName;

    private AccommodationPicture accommodationPicture;

}
