package com.mip.sharebnb.dto;

import com.mip.sharebnb.model.AccommodationPicture;
import com.mip.sharebnb.model.BookedDate;
import com.mip.sharebnb.model.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccommodationDto {

    private String city;

    private String gu;

    private String title;

    private Integer bathroomNum;

    private Integer bedroomNum;

    private Integer bedNum;

    private Integer capacity;

    private Integer price;

    private String contact;

    private String latitude;

    private String longitude;

    private String locationDesc;

    private String transportationDesc;

    private String accommodationDesc;

    private String rating;

    private Integer reviewNum;

    private String accommodationType;

    private String buildingType;

    private String hostName;

    private String hostDesc;

    private Integer hostReviewNum;

    private List<Review> reviews;

    private List<BookedDate> bookedDates;

    private List<AccommodationPicture> accommodationPictures;
}
