package com.se.aiconomy.server.model.dto.user.request;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginRequest {
    private String email;
    private String password;
}
