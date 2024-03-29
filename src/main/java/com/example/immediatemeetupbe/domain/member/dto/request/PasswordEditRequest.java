package com.example.immediatemeetupbe.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordEditRequest {

    @NotBlank(message = "필수 항목입니다.")
    private String password;

    @NotBlank(message = "필수 항목입니다.")
    private String checkPassword;
}
