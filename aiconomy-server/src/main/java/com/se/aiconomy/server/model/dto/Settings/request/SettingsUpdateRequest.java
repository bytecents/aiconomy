package com.se.aiconomy.server.model.dto.Settings.request;

import com.se.aiconomy.server.model.dto.BaseRequest;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettingsUpdateRequest extends BaseRequest {
    private String settingsId;
    private String dateFormat;
    private String language;
    private String theme;
    private String notification;
    private String AIFunctionality;
}
