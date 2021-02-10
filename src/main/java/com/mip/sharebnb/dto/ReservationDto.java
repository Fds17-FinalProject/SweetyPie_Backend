package com.mip.sharebnb.dto;

import com.mip.sharebnb.model.AccommodationPicture;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

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

    @Positive
    @Min(1)
    private int guestNum;

    @Positive
    private int totalPrice;

    private Boolean isWrittenReview;

    private String city;

    private String gu;

    private String title;

    private AccommodationPicture accommodationPicture;

}
