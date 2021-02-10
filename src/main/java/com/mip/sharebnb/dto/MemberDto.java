package com.mip.sharebnb.dto;

import com.mip.sharebnb.model.Bookmark;
import com.mip.sharebnb.model.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDto {

    @Email
    @Size(min = 5, max = 50)
    private String email;

    @Size(min = 8, max = 50)
    private String password;

    @Size(min = 8, max = 50)
    private String prePassword;

    @Size(min = 2, max = 50)
    private String name;

    @Size(min = 3, max = 20)
    private String contact;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    private List<Review> reviews;

    private List<Bookmark> bookmarks;

}
