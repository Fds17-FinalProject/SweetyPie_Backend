package com.mip.sharebnb.dto;

import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.model.AccommodationPicture;
import com.mip.sharebnb.model.Reservation;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class ReservationDto {

    @NonNull
    private Reservation reservation;

    private AccommodationDto accommodationDto;

}
