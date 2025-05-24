package com.se.aiconomy.server.model.dto.Settings.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Represents user profile information for settings.
 * <p>
 * This DTO encapsulates user-related details such as avatar URL, first name, last name,
 * phone number, birth date, and preferred currency. It is used to transfer user profile
 * information in the context of settings management.
 * </p>
 *
 * <ul>
 *   <li><b>avatarUrl</b>: The URL of the user's avatar image.</li>
 *   <li><b>firstName</b>: The user's first name.</li>
 *   <li><b>lastName</b>: The user's last name.</li>
 *   <li><b>phone</b>: The user's phone number.</li>
 *   <li><b>birthDate</b>: The user's date of birth.</li>
 *   <li><b>currency</b>: The user's preferred currency.</li>
 * </ul>
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
public class SettingsUserInfo {
    /**
     * The URL of the user's avatar image.
     */
    private String avatarUrl;

    /**
     * The user's first name.
     */
    private String firstName;

    /**
     * The user's last name.
     */
    private String lastName;

    /**
     * The user's phone number.
     */
    private String phone;

    /**
     * The user's date of birth.
     */
    private LocalDate birthDate;

    /**
     * The user's preferred currency.
     */
    private String currency;
}
