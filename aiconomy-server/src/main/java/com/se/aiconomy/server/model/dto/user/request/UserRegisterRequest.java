package com.se.aiconomy.server.model.dto.user.request;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Request DTO for user registration.
 * <p>
 * This class encapsulates the data required for registering a new user,
 * including personal information, contact details, and financial preferences.
 * </p>
 *
 * <ul>
 *   <li><b>email</b>: The email address of the user.</li>
 *   <li><b>password</b>: The password for the user's account.</li>
 *   <li><b>firstName</b>: The user's first name.</li>
 *   <li><b>lastName</b>: The user's last name.</li>
 *   <li><b>phoneNumber</b>: The user's phone number.</li>
 *   <li><b>birthDate</b>: The user's date of birth.</li>
 *   <li><b>currency</b>: The preferred currency of the user.</li>
 *   <li><b>financialGoal</b>: A list of the user's financial goals.</li>
 *   <li><b>monthlyIncome</b>: The user's monthly income.</li>
 *   <li><b>mainExpenseType</b>: A list of the user's main expense types.</li>
 * </ul>
 *
 * <p>
 * Lombok annotations are used to generate boilerplate code such as getters, setters,
 * constructors, toString, and builder methods.
 * </p>
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterRequest {
    /**
     * The email address of the user.
     */
    private String email;

    /**
     * The password for the user's account.
     */
    private String password;

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
    private String phoneNumber;

    /**
     * The user's date of birth.
     */
    private LocalDate birthDate;

    /**
     * The preferred currency of the user.
     */
    private String currency;

    /**
     * A list of the user's financial goals.
     */
    private List<String> financialGoal;

    /**
     * The user's monthly income.
     */
    private Double monthlyIncome;

    /**
     * A list of the user's main expense types.
     */
    private List<String> mainExpenseType;
}
