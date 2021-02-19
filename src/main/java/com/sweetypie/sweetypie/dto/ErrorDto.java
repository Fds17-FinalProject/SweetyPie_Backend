package com.sweetypie.sweetypie.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class ErrorDto {
    private String message;
}
