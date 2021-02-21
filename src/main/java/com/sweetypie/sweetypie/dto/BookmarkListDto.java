package com.sweetypie.sweetypie.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.sweetypie.sweetypie.model.AccommodationPicture;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
public class BookmarkListDto {

    private long bookmarkId;

    @Positive
    private long accommodationId;

    private String title;

    private String city;

    private String gu;

    private AccommodationPicture accommodationPicture;

    @QueryProjection
    public BookmarkListDto(long bookmarkId, @Positive long accommodationId, String title, String city, String gu) {
        this.bookmarkId = bookmarkId;
        this.accommodationId = accommodationId;
        this.title = title;
        this.city = city;
        this.gu = gu;
    }

    @QueryProjection
    public BookmarkListDto(long bookmarkId, @Positive long accommodationId, String title, String city, String gu, AccommodationPicture accommodationPicture) {
        this.bookmarkId = bookmarkId;
        this.accommodationId = accommodationId;
        this.title = title;
        this.city = city;
        this.gu = gu;
        this.accommodationPicture = accommodationPicture;
    }
}
