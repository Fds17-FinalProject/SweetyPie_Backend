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

    private int bathroomNum;

    private int bedroomNum;

    private int bedNum;

    private int capacity;

    private int price;

    private String contact;

    private float latitude;

    private float longitude;

    private String locationDesc;

    private String transportationDesc;

    private String accommodationDesc;

    private float rating;

    private int reviewNum;

    private String accommodationType;

    private String buildingType;

    private String hostName;

    private String hostDesc;

    private int hostReviewNum;

    private List<Review> reviews;

    private List<BookedDate> bookedDates;

    private List<AccommodationPicture> accommodationPictures;
}
