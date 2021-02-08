package com.mip.sharebnb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of") // 스태틱 네임을 쓰게 되면 좋은점 -
public class ErrorDto {
    private String message;
}
