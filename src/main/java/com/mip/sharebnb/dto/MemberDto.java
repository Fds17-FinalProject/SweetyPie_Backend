package com.mip.sharebnb.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mip.sharebnb.model.Bookmark;
import com.mip.sharebnb.model.Reservation;
import com.mip.sharebnb.model.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDto {

    @JsonIgnore
    private Long id;
    @Email
    private String email;

    @Size(min = 3, max = 100)
    private String password;

    @Size(min = 3, max = 30)
    private String prePassword;

    @Size(min = 3, max = 30)
    private String name;

    @Size(min = 3, max = 20)
    private String contact;

    private LocalDate birthDate;

    private Long socialId;

    private List<Review> reviews;

    private List<Bookmark> bookmarks;

}
