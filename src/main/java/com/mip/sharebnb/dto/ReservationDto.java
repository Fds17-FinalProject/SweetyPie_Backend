package com.mip.sharebnb.dto;

import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.model.Reservation;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class ReservationDto {

    private Reservation reservation;

    private AccommodationDto accommodationDto;

}
