package com.se.aiconomy.server.model.dto.user.request;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate birthDate;
    private String currency;
    private List<String> financialGoal;
    private Double monthlyIncome;
    private List<String> mainExpenseType;
}
