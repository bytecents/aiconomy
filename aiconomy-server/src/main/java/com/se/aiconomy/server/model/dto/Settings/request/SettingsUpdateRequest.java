package com.se.aiconomy.server.model.dto.Settings.request;

import com.se.aiconomy.server.model.dto.BaseRequest;
import lombok.*;

/**
 * Request DTO for updating user settings.
 * <p>
 * This class encapsulates the fields required to update user settings, such as
 * date format, language, theme, notification preferences, and AI functionality.
 * It extends {@link BaseRequest} and uses Lombok annotations to reduce boilerplate code.
 * </p>
 *
 * <ul>
 *   <li><b>settingsId</b>: The unique identifier for the settings.</li>
 *   <li><b>dateFormat</b>: The preferred date format (e.g., yyyy-MM-dd).</li>
 *   <li><b>language</b>: The preferred language (e.g., en, zh).</li>
 *   <li><b>theme</b>: The selected theme (e.g., light, dark).</li>
 *   <li><b>notification</b>: Notification preferences.</li>
 *   <li><b>AIFunctionality</b>: AI-related functionality settings.</li>
 * </ul>
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettingsUpdateRequest extends BaseRequest {
    /**
     * The unique identifier for the settings.
     */
    private String settingsId;

    /**
     * The preferred date format (e.g., yyyy-MM-dd).
     */
    private String dateFormat;

    /**
     * The preferred language (e.g., en, zh).
     */
    private String language;

    /**
     * The selected theme (e.g., light, dark).
     */
    private String theme;

    /**
     * Notification preferences.
     */
    private String notification;

    /**
     * AI-related functionality settings.
     */
    private String AIFunctionality;
}
