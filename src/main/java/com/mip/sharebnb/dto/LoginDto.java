package com.mip.sharebnb.dto;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {

    @Email
    @NotNull
    @Size(min =3, max = 30)
    private String email;

    @NotNull
    @Size(min = 3, max = 100)
    private String password;
}
