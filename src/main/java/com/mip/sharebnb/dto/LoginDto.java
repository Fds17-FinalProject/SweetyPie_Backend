package com.mip.sharebnb.dto;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {

    @Email(message = "이메일 형식이 아닙니다")
    @NotEmpty(message = "이메일은 필수 입력값 입니다.")
    @Size(min =5, max = 50, message = "이메일에 길이를 확인해주세요")
    private String email;

    @NotEmpty(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, max = 50, message = "비밀번호는 8자 이상 50자 이하여야 합니다")
    private String password;
}
