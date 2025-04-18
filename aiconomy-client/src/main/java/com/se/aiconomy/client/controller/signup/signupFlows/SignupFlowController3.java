package com.se.aiconomy.client.controller.signup.signupFlows;

import com.se.aiconomy.client.controller.signup.SignupController;
import lombok.Setter;

public class SignupFlowController3 {
    @Setter  // Lombok generates the setter method for parentController
    private SignupController parentController;  // Automatically generates a setter
}
