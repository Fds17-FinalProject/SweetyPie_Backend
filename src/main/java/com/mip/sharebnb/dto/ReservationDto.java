package com.mip.sharebnb.dto;

import com.mip.sharebnb.model.AccommodationPicture;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

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

    @Positive(message = "인원수가 맞지 않습니다.")
    @Min(value = 1, message = "인원은 최소 1명입니다.") @Max(value = 8, message = "인원은 최대 8명입니다.")
    private int guestNum;

    @Positive(message = "총 비용이 맞지 않습니다.")
    private int totalPrice;

    private Boolean isWrittenReview;

    private String city;

    private String gu;

    private String title;

    private AccommodationPicture accommodationPicture;

}
