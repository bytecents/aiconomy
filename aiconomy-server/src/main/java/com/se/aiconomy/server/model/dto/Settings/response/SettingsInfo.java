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
    private String id;
    private String userId;
    private String dateFormat;
    private String language;
    private String theme;
    private String notification;
    private String AIFunctionality;
}
