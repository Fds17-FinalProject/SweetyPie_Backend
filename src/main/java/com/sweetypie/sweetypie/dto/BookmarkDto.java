package com.sweetypie.sweetypie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookmarkDto {

    private long bookmarkId;

    @Positive
    private long memberId;

    @Positive
    private long accommodationId;
}
