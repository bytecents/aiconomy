package com.se.aiconomy.server.model.dto.user.request;

import com.se.aiconomy.server.model.dto.BaseRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

/**
 * Request DTO for updating user information.
 * <p>
 * All fields are optional; only non-null fields will be updated.
 * </p>
 *
 * <ul>
 *   <li><b>email</b>: The user's email address.</li>
 *   <li><b>password</b>: The user's password.</li>
 *   <li><b>avatarUrl</b>: The URL of the user's avatar.</li>
 *   <li><b>firstName</b>: The user's first name.</li>
 *   <li><b>lastName</b>: The user's last name.</li>
 *   <li><b>phone</b>: The user's phone number.</li>
 *   <li><b>birthDate</b>: The user's date of birth.</li>
 *   <li><b>currency</b>: The user's preferred currency.</li>
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
public class UserUpdateRequest extends BaseRequest {
    /**
     * The user's email address.
     */
    private String email;

    /**
     * The user's password.
     */
    private String password;

    /**
     * The URL of the user's avatar.
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

    /**
     * Checks if any field has been updated.
     *
     * @return true if any field is not null, false otherwise
     */
    public boolean hasUpdates() {
        return email != null ||
               password != null ||
               avatarUrl != null ||
               firstName != null ||
               lastName != null ||
               phone != null ||
               birthDate != null ||
               currency != null ||
               financialGoal != null ||
               monthlyIncome != null ||
               mainExpenseType != null;
    }
}