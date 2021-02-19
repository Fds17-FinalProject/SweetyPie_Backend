package com.sweetypie.sweetypie.dto;

import com.sweetypie.sweetypie.model.AccommodationPicture;
import com.sweetypie.sweetypie.model.Bookmark;
import com.sweetypie.sweetypie.model.Review;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccommodationDto {

    private long id;

    private String city;

    private String gu;

    private String address;

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

    private boolean isBookmarked;

    private List<Review> reviews;

    private List<BookedDateDto> bookedDateDtos;

    private List<AccommodationPicture> accommodationPictures;

    @QueryProjection
    public AccommodationDto(long id, String city, String gu, String address, String title, int bathroomNum, int bedroomNum, int bedNum, int capacity, int price, String contact, float latitude, float longitude, String locationDesc, String transportationDesc, String accommodationDesc, float rating, int reviewNum, String accommodationType, String buildingType, String hostName, String hostDesc, int hostReviewNum) {
        this.id = id;
        this.city = city;
        this.gu = gu;
        this.address = address;
        this.title = title;
        this.bathroomNum = bathroomNum;
        this.bedroomNum = bedroomNum;
        this.bedNum = bedNum;
        this.capacity = capacity;
        this.price = price;
        this.contact = contact;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationDesc = locationDesc;
        this.transportationDesc = transportationDesc;
        this.accommodationDesc = accommodationDesc;
        this.rating = rating;
        this.reviewNum = reviewNum;
        this.accommodationType = accommodationType;
        this.buildingType = buildingType;
        this.hostName = hostName;
        this.hostDesc = hostDesc;
        this.hostReviewNum = hostReviewNum;
    }

    @QueryProjection
    public AccommodationDto(long id, String city, String gu, String address, String title, int bathroomNum, int bedroomNum, int bedNum, int capacity, int price, String contact, float latitude, float longitude, String locationDesc, String transportationDesc, String accommodationDesc, float rating, int reviewNum, String accommodationType, String buildingType, String hostName, String hostDesc, int hostReviewNum, Bookmark bookmark) {
        this.id = id;
        this.city = city;
        this.gu = gu;
        this.address = address;
        this.title = title;
        this.bathroomNum = bathroomNum;
        this.bedroomNum = bedroomNum;
        this.bedNum = bedNum;
        this.capacity = capacity;
        this.price = price;
        this.contact = contact;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationDesc = locationDesc;
        this.transportationDesc = transportationDesc;
        this.accommodationDesc = accommodationDesc;
        this.rating = rating;
        this.reviewNum = reviewNum;
        this.accommodationType = accommodationType;
        this.buildingType = buildingType;
        this.hostName = hostName;
        this.hostDesc = hostDesc;
        this.hostReviewNum = hostReviewNum;
        this.isBookmarked = bookmark != null;
    }
}
