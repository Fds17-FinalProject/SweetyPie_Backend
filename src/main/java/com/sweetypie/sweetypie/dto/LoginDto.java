package com.sweetypie.sweetypie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginDto {

    @Email(message = "이메일 형식이 아닙니다")
    @NotEmpty(message = "이메일은 필수 입력값 입니다.")
    @Size(min =5, max = 50, message = "이메일에 길이를 확인해주세요")
    private String email;

    @NotEmpty(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, max = 50, message = "비밀번호는 8자 이상 50자 이하여야 합니다")
    @Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$",
            message = "비밀번호는 영어와 숫자와 특수문자를 포함해서 8자이상 으로 입력해주세요.")
    private String password;
}
