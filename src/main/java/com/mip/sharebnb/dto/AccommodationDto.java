package com.mip.sharebnb.dto;

import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.model.AccommodationPicture;
import com.mip.sharebnb.model.BookedDate;
import com.mip.sharebnb.model.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class AccommodationDto {

    @NonNull
    private Accommodation accommodation;

    private List<Review> reviews;

    private List<BookedDate> bookedDates;

    private List<AccommodationPicture> accommodationPictures;
}
