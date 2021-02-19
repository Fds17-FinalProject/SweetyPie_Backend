package com.sweetypie.sweetypie.dto;

import com.sweetypie.sweetypie.model.AccommodationPicture;
import com.sweetypie.sweetypie.model.Bookmark;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.util.List;

@Data
public class SearchAccommodationDto {
    private long id;

    private String city;

    private String gu;

    private String address;

    private String title;

    private int bathroomNum;

    private int bedroomNum;

    private int bedNum;

    private int price;

    private int capacity;

    private String contact;

    private float latitude;

    private float longitude;

    private float rating;

    private int reviewNum;

    private String accommodationType;

    private String buildingType;

    private String hostName;

    private boolean isBookmarked;

    private List<AccommodationPicture> accommodationPictures;

    @QueryProjection
    public SearchAccommodationDto(long id, String city, String gu, String address, String title, int bathroomNum, int bedroomNum, int bedNum, int price, int capacity, String contact, float latitude, float longitude, float rating, int reviewNum, String accommodationType, String buildingType, String hostName, Bookmark isBookmarked/*, List<AccommodationPicture> accommodationPictures*/) {
        this.id = id;
        this.city = city;
        this.gu = gu;
        this.address = address;
        this.title = title;
        this.bathroomNum = bathroomNum;
        this.bedroomNum = bedroomNum;
        this.bedNum = bedNum;
        this.price = price;
        this.capacity = capacity;
        this.contact = contact;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.reviewNum = reviewNum;
        this.accommodationType = accommodationType;
        this.buildingType = buildingType;
        this.hostName = hostName;
        this.isBookmarked = isBookmarked != null;
    }

    @QueryProjection
    public SearchAccommodationDto(long id, String city, String gu, String address, String title, int bathroomNum, int bedroomNum, int bedNum, int price, int capacity, String contact, float latitude, float longitude, float rating, int reviewNum, String accommodationType, String buildingType, String hostName) {
        this.id = id;
        this.city = city;
        this.gu = gu;
        this.address = address;
        this.title = title;
        this.bathroomNum = bathroomNum;
        this.bedroomNum = bedroomNum;
        this.bedNum = bedNum;
        this.price = price;
        this.capacity = capacity;
        this.contact = contact;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.reviewNum = reviewNum;
        this.accommodationType = accommodationType;
        this.buildingType = buildingType;
        this.hostName = hostName;
    }
}
