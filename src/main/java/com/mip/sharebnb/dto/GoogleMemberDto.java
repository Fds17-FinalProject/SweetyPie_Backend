package com.mip.sharebnb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleMemberDto {

    @Email
    private String email;

    @Size(min = 3, max = 30)
    private String name;

    @Size(min = 3, max = 20)
    private String contact;

    private LocalDate birthDate;

    private String socialId;

}
