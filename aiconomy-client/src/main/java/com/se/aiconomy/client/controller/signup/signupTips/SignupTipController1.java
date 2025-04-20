package com.se.aiconomy.client.controller.signup.signupTips;

import com.se.aiconomy.client.controller.signup.SignupController;
import lombok.Setter;

public class SignupTipController1 {
    @Setter  // Lombok generates the setter method for parentController
    private SignupController parentController;  // Automatically generates a setter
}
