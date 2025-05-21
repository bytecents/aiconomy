package com.se.aiconomy.server.model.dto.Settings.response;

import com.se.aiconomy.server.model.dto.user.response.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SettingsInfo {
    private String email;
    private String firstName; // 名字
    private String lastName; // 姓氏
    private String phone;
    private LocalDate birthDate;
    private String id;
    private String userId;
    private String currency;
    private String dateFormat;
    private String language;
    private String theme;
    private String notification;
    private String AIFunctionality;
}
