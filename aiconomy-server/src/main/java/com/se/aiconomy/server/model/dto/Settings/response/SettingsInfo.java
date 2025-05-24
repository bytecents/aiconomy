package com.se.aiconomy.server.model.dto.Settings.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the user settings information.
 * <p>
 * This DTO encapsulates the details of user settings, including the settings ID, user ID,
 * date format, language, theme, notification preferences, and AI functionality settings.
 * It uses Lombok annotations to reduce boilerplate code.
 * </p>
 *
 * <ul>
 *   <li><b>id</b>: The unique identifier for the settings.</li>
 *   <li><b>userId</b>: The ID of the user associated with these settings.</li>
 *   <li><b>dateFormat</b>: The preferred date format (e.g., yyyy-MM-dd).</li>
 *   <li><b>language</b>: The preferred language (e.g., en, zh).</li>
 *   <li><b>theme</b>: The selected theme (e.g., light, dark).</li>
 *   <li><b>notification</b>: Notification preferences.</li>
 *   <li><b>AIFunctionality</b>: AI-related functionality settings.</li>
 * </ul>
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
public class SettingsInfo {
    /**
     * The unique identifier for the settings.
     */
    private String id;

    /**
     * The ID of the user associated with these settings.
     */
    private String userId;

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
