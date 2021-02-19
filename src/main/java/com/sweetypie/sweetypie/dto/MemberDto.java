package com.sweetypie.sweetypie.dto;

import com.sweetypie.sweetypie.model.Bookmark;
import com.sweetypie.sweetypie.model.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
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
    @Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$",
            message = "비밀번호는 영어와 숫자와 특수문자를 포함해서 8자이상 으로 입력해주세요.")
    private String password;

    @Size(min = 8, max = 50, message = "이전 비밀번호는 8자 이상 50자 이하여야 합니다.")
    private String prePassword;

    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하여야 합니다.")
    @Pattern(regexp="^[가-힣]*$" , message = "이름은 한글로만 입력해주세요")
    private String name;

    @Size(min = 11, max = 11, message = "연락처는 11자리로 입력해주세요")
    @Pattern(regexp="^[0-9]*$" , message = "연락처는 숫자로만 입력해주세요")
    private String contact;

    @Past(message = "생년월일을 확인해주세요")
    private LocalDate birthDate;

}
