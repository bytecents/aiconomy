package com.se.aiconomy.server.model.dto.user.response;

import com.se.aiconomy.server.langchain.common.model.DynamicBillType;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * DTO representing user information.
 * <p>
 * This class encapsulates the user's profile details, including identification,
 * contact information, financial preferences, and associated bill types.
 * </p>
 *
 * <ul>
 *   <li><b>id</b>: Unique identifier for the user.</li>
 *   <li><b>email</b>: The user's email address.</li>
 *   <li><b>avatarUrl</b>: URL of the user's avatar image.</li>
 *   <li><b>firstName</b>: The user's first name.</li>
 *   <li><b>lastName</b>: The user's last name.</li>
 *   <li><b>phone</b>: The user's phone number.</li>
 *   <li><b>birthDate</b>: The user's date of birth.</li>
 *   <li><b>currency</b>: The user's preferred currency (e.g., USD, EUR, CNY).</li>
 *   <li><b>financialGoal</b>: List of the user's financial goals (e.g., saving, investing, buying a house).</li>
 *   <li><b>monthlyIncome</b>: The user's monthly income.</li>
 *   <li><b>mainExpenseType</b>: List of the user's main expense types (e.g., rent, education, transportation).</li>
 *   <li><b>billTypes</b>: Set of dynamic bill types associated with the user.</li>
 * </ul>
 *
 * <p>
 * Lombok annotations are used to generate boilerplate code such as getters, setters,
 * constructors, toString, and builder methods.
 * </p>
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class UserInfo {
    /**
     * Unique identifier for the user.
     */
    private String id;

    /**
     * The user's email address.
     */
    private String email;

    /**
     * URL of the user's avatar image.
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
     * The user's preferred currency (e.g., USD, EUR, CNY).
     */
    private String currency;

    /**
     * List of the user's financial goals (e.g., saving, investing, buying a house).
     */
    private List<String> financialGoal;

    /**
     * The user's monthly income.
     */
    private Double monthlyIncome;

    /**
     * List of the user's main expense types (e.g., rent, education, transportation).
     */
    private List<String> mainExpenseType;

    /**
     * Set of dynamic bill types associated with the user.
     */
    private Set<DynamicBillType> billTypes;
}
