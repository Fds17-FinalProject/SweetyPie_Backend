package com.sweetypie.sweetypie.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.sweetypie.sweetypie.model.Bookmark;

public class IsBookmarkDto {

    private final boolean isBookmarked;

    public boolean isBookmarked() {
        return isBookmarked;
    }

    @QueryProjection
    public IsBookmarkDto(Bookmark bookmark) {
        this.isBookmarked = bookmark != null;
    }
}
