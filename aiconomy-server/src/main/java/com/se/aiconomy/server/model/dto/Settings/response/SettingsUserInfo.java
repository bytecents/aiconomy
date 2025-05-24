package com.se.aiconomy.server.model.dto.Settings.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SettingsUserInfo {
    private String avatarUrl;
    private String firstName;
    private String lastName;
    private String phone;
    private LocalDate birthDate;
    private String currency;
}
