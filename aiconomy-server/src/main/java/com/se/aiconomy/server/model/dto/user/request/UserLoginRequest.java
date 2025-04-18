package com.se.aiconomy.server.model.dto.user.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserLoginRequest {
    private String email;
    private String password;
}
