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
    @Size(min =5, max = 50)
    private String email;

    @NotNull
    @Size(min = 8, max = 50)
    private String password;
}
