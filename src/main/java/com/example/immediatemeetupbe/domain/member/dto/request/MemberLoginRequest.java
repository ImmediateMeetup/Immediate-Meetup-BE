package com.example.immediatemeetupbe.domain.member.dto.request;

import lombok.Getter;

@Getter
public class MemberLoginRequest {

    private String email;
    private String password;
}
