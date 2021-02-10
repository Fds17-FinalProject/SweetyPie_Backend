package com.mip.sharebnb.dto;

import com.mip.sharebnb.model.Bookmark;
import com.mip.sharebnb.model.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDto {

    @Email(message = "이메일형식이 아닙니다")
    @Size(min = 5, max = 50, message = "이메일 길이를 확인해주세요")
    private String email;

    @Size(min = 8, max = 50, message = "비밀번호는 8자 이상 50자 이하여야 합니다")
    private String password;

    @Size(min = 8, max = 50, message = "이전 비밀번호는 8자 이상 50자 이하여야 합니다.")
    private String prePassword;

    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하여야 합니다.")
    private String name;

    @Size(min = 11, max = 11, message = "연락처는 11자리로 입력해주세요")
    private String contact;

    @Past
    private LocalDate birthDate;

    private List<Review> reviews;

    private List<Bookmark> bookmarks;

}
