package com.sweetypie.sweetypie.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.sweetypie.sweetypie.model.Bookmark;

public class IsBookmarkDto {

    private final boolean isBookmark;

    public boolean isBookmark() {
        return isBookmark;
    }

    @QueryProjection
    public IsBookmarkDto(Bookmark bookmark) {
        this.isBookmark = bookmark != null;
    }
}
