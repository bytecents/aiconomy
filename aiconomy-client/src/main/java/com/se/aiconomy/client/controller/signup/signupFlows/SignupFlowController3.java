package com.se.aiconomy.client.controller.signup.signupFlows;

import com.se.aiconomy.client.common.CustomDialog;
import com.se.aiconomy.client.controller.BaseController;
import com.se.aiconomy.client.controller.signup.SignupController;
import com.se.aiconomy.server.handler.UserRequestHandler;
import com.se.aiconomy.server.model.dto.user.request.UserRegisterRequest;
import com.se.aiconomy.server.model.entity.User;
import com.se.aiconomy.server.service.impl.UserServiceImpl;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Controller for the third step of the signup flow.
 * Handles user input for financial information, validation, and registration logic.
 */
public class SignupFlowController3 extends BaseController {
    /** Button to go back to the previous signup step. */
    @FXML
    public Button goBackButton;

    /** Button to submit the registration. */
    @FXML
    public Button registerButton;

    /** Text field for user's monthly income input. */
    @FXML
    public TextField monthlyIncomeField;

    /** Labels for primary financial goals. */
    @FXML
    public Label primaryFinancialGoal1, primaryFinancialGoal2, primaryFinancialGoal3, primaryFinancialGoal4;

    /** List to store selected financial goals. */
    public List<String> financialGoal;

    /** VBoxes representing selectable financial goals. */
    @FXML
    public VBox goal1, goal2, goal3, goal4;

    /** Flags indicating if each financial goal is selected. */
    public Boolean isSelectedGoal1 = false, isSelectedGoal2 = false, isSelectedGoal3 = false, isSelectedGoal4 = false;

    /** Checkboxes for main expense types. */
    public CheckBox checkBox1, checkBox2, checkBox3, checkBox4;

    /** List to store selected main expense types. */
    public List<String> mainExpenseType;

    /** Handler for user registration requests. */
    private UserRequestHandler userRequestHandler;

    /** Reference to the parent signup controller. */
    @Setter
    private SignupController parentController;

    /** User data object for storing signup information. */
    @Setter
    private User userData;

    /**
     * Initializes the controller, sets up input filters, actions, and populates fields if userData is present.
     */
    public void initialize() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getText();
            if (newText.matches("[0-9]*\\.?[0-9]*")) {
                return change;
            }
            return null;
        };
        financialGoal = new ArrayList<>();
        mainExpenseType = new ArrayList<>();
        initActions();
        initData();
    }

    /**
     * Handles the action to go back to the second signup step.
     * Saves the current input data and loads the previous step.
     */
    public void goBackToSignupFlow2() {
        if (parentController != null) {
            try {
                if (monthlyIncomeField.getText() != null)
                    userData.setMonthlyIncome(Double.parseDouble(monthlyIncomeField.getText().trim()));
            } catch (NumberFormatException e) {
                System.out.println("didn't stored the illegal monthly income, NaN");
            }
            updateMainExpenseType();
            userData.setFinancialGoal(financialGoal);
            userData.setMainExpenseType(mainExpenseType);
            parentController.loadSignupFlow("signupFlow2.fxml");
            parentController.loadSignupTip("signupTip2.fxml");
        } else {
            System.out.println("Parent controller is null");
        }
    }

    /**
     * Handles the registration process, validates input, submits the registration request, and navigates to login.
     * @throws IOException if loading the login FXML fails
     */
    @FXML
    public void register() throws IOException {
        if (financialGoal != null) {
            userData.setFinancialGoal(financialGoal);
        }
        try {
            if (monthlyIncomeField.getText() == null) {
                CustomDialog.show("Invalid Income", "Please input your income", "error", "Go");
            }
            if (monthlyIncomeField.getText() != null)
                userData.setMonthlyIncome(Double.parseDouble(monthlyIncomeField.getText().trim()));
        } catch (NumberFormatException e) {
            System.out.println("Illegal monthly income, NaN");
            CustomDialog.show("Invalid Income", "Income not a number", "error", "Reset");
        }
        if (mainExpenseType != null) {
            updateMainExpenseType();
            userData.setMainExpenseType(mainExpenseType);
        }
        System.out.println(userData);

        try {
            this.userRequestHandler = new UserRequestHandler(new UserServiceImpl(JSONStorageServiceImpl.getInstance()));
            UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
            userRegisterRequest.setEmail(userData.getEmail());
            userRegisterRequest.setPassword(userData.getPassword());
            userRegisterRequest.setFirstName(userData.getFirstName());
            userRegisterRequest.setLastName(userData.getLastName());
            userRegisterRequest.setPhoneNumber(userData.getPhone());
            userRegisterRequest.setBirthDate(userData.getBirthDate());
            userRegisterRequest.setCurrency(userData.getCurrency());
            userRegisterRequest.setFinancialGoal(userData.getFinancialGoal());
            userRegisterRequest.setMonthlyIncome(userData.getMonthlyIncome());
            userRegisterRequest.setMainExpenseType(userData.getMainExpenseType());

            userRequestHandler.handleRegisterRequest(userRegisterRequest);

            goBackToLogin();
            CustomDialog.show("Success", "Registered successfully", "success", "OK");

        } catch (Exception e) {
            CustomDialog.show("Error", e.getMessage(), "error", "OK");
        }
    }

    /**
     * Switches the scene to the login page.
     * @throws IOException if loading the login FXML fails
     */
    public void goBackToLogin() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/login.fxml")));
        Stage stage = (Stage) registerButton.getScene().getWindow();
        Scene currentScene = stage.getScene();

        double currentWidth = currentScene.getWidth();
        double currentHeight = currentScene.getHeight();

        Scene scene = new Scene(root, currentWidth, currentHeight);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Updates the mainExpenseType list based on the selected checkboxes.
     */
    private void updateMainExpenseType() {
        if (checkBox1.isSelected() && !mainExpenseType.contains(checkBox1.getText()))
            mainExpenseType.add(checkBox1.getText());
        else if (!checkBox1.isSelected()) mainExpenseType.remove(checkBox1.getText());
        if (checkBox2.isSelected() && !mainExpenseType.contains(checkBox2.getText()))
            mainExpenseType.add(checkBox2.getText());
        else if (!checkBox2.isSelected()) mainExpenseType.remove(checkBox2.getText());
        if (checkBox3.isSelected() && !mainExpenseType.contains(checkBox3.getText()))
            mainExpenseType.add(checkBox3.getText());
        else if (!checkBox3.isSelected()) mainExpenseType.remove(checkBox3.getText());
        if (checkBox4.isSelected() && !mainExpenseType.contains(checkBox4.getText()))
            mainExpenseType.add(checkBox4.getText());
        else if (!checkBox4.isSelected()) mainExpenseType.remove(checkBox4.getText());
    }

    /**
     * Initializes the actions for selecting and hovering over financial goals.
     */
    private void initActions() {
        goal1.setOnMouseClicked(event -> {
            if (!isSelectedGoal1 && !financialGoal.contains(primaryFinancialGoal1.getText())) {
                financialGoal.add(primaryFinancialGoal1.getText());
            } else financialGoal.remove(primaryFinancialGoal1.getText());
            isSelectedGoal1 = !isSelectedGoal1;
            updateGoalStyle(goal1, isSelectedGoal1, 1);
        });

        goal2.setOnMouseClicked(event -> {
            if (!isSelectedGoal2 && !financialGoal.contains(primaryFinancialGoal2.getText())) {
                financialGoal.add(primaryFinancialGoal2.getText());
            } else financialGoal.remove(primaryFinancialGoal2.getText());
            isSelectedGoal2 = !isSelectedGoal2;
            updateGoalStyle(goal2, isSelectedGoal2, 2);
        });

        goal3.setOnMouseClicked(event -> {
            if (!isSelectedGoal3 && !financialGoal.contains(primaryFinancialGoal3.getText())) {
                financialGoal.add(primaryFinancialGoal3.getText());
            } else financialGoal.remove(primaryFinancialGoal3.getText());
            isSelectedGoal3 = !isSelectedGoal3;
            updateGoalStyle(goal3, isSelectedGoal3, 3);
        });

        goal4.setOnMouseClicked(event -> {
            if (!isSelectedGoal4 && !financialGoal.contains(primaryFinancialGoal4.getText())) {
                financialGoal.add(primaryFinancialGoal4.getText());
            } else financialGoal.remove(primaryFinancialGoal4.getText());
            isSelectedGoal4 = !isSelectedGoal4;
            updateGoalStyle(goal4, isSelectedGoal4, 4);
        });

        goal1.setOnMouseEntered(event -> setHoverStyle(goal1, 1));
        goal1.setOnMouseExited(event -> resetStyle(goal1, 1));

        goal2.setOnMouseEntered(event -> setHoverStyle(goal2, 2));
        goal2.setOnMouseExited(event -> resetStyle(goal2, 2));

        goal3.setOnMouseEntered(event -> setHoverStyle(goal3, 3));
        goal3.setOnMouseExited(event -> resetStyle(goal3, 3));

        goal4.setOnMouseEntered(event -> setHoverStyle(goal4, 4));
        goal4.setOnMouseExited(event -> resetStyle(goal4, 4));
    }

    /**
     * Populates the UI fields with data from userData if available.
     */
    private void initData() {
        if (userData.getFinancialGoal() != null) {
            if (userData.getFinancialGoal().contains(primaryFinancialGoal1.getText())) {
                isSelectedGoal1 = true;
                updateGoalStyle(goal1, true, 1);
                financialGoal.add(primaryFinancialGoal1.getText());
            }
            if (userData.getFinancialGoal().contains(primaryFinancialGoal2.getText())) {
                isSelectedGoal2 = true;
                updateGoalStyle(goal2, true, 2);
                financialGoal.add(primaryFinancialGoal2.getText());
            }
            if (userData.getFinancialGoal().contains(primaryFinancialGoal3.getText())) {
                isSelectedGoal3 = true;
                updateGoalStyle(goal3, true, 3);
                financialGoal.add(primaryFinancialGoal3.getText());
            }
            if (userData.getFinancialGoal().contains(primaryFinancialGoal4.getText())) {
                isSelectedGoal4 = true;
                updateGoalStyle(goal4, true, 4);
                financialGoal.add(primaryFinancialGoal4.getText());
            }
        }
        if (userData.getMonthlyIncome() != null) monthlyIncomeField.setText(userData.getMonthlyIncome().toString());
        if (userData.getMainExpenseType() != null) {
            if (userData.getMainExpenseType().contains(checkBox1.getText())) checkBox1.setSelected(true);
            if (userData.getMainExpenseType().contains(checkBox2.getText())) checkBox2.setSelected(true);
            if (userData.getMainExpenseType().contains(checkBox3.getText())) checkBox3.setSelected(true);
            if (userData.getMainExpenseType().contains(checkBox4.getText())) checkBox4.setSelected(true);
        }
    }

    /**
     * Updates the style of a goal VBox based on its selection state.
     * @param goal the VBox representing the goal
     * @param isSelected whether the goal is selected
     * @param goalNumber the goal's number (1-4)
     */
    private void updateGoalStyle(VBox goal, boolean isSelected, int goalNumber) {
        if (isSelected) {
            goal.setStyle("-fx-background-color: #eff6ff; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-border-color: #3b82f6;");
        } else {
            goal.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: #d6dbe7; -fx-border-radius: 8px;");
        }
    }

    /**
     * Sets the hover style for a goal VBox if it is not selected.
     * @param goal the VBox representing the goal
     * @param goalNumber the goal's number (1-4)
     */
    private void setHoverStyle(VBox goal, int goalNumber) {
        if (!isSelectedGoal(goalNumber)) {
            goal.setStyle("-fx-background-color: #f3f4f6; -fx-border-radius: 8px; -fx-padding: 8px; -fx-border-color: #d6dbe7;");
        }
    }

    /**
     * Resets the style of a goal VBox if it is not selected.
     * @param goal the VBox representing the goal
     * @param goalNumber the goal's number (1-4)
     */
    private void resetStyle(VBox goal, int goalNumber) {
        if (!isSelectedGoal(goalNumber)) {
            goal.setStyle("-fx-background-color: white; -fx-border-color: #d6dbe7; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        }
    }

    /**
     * Checks if a goal is selected based on its number.
     * @param goalNumber the goal's number (1-4)
     * @return true if selected, false otherwise
     */
    private boolean isSelectedGoal(int goalNumber) {
        return switch (goalNumber) {
            case 1 -> isSelectedGoal1;
            case 2 -> isSelectedGoal2;
            case 3 -> isSelectedGoal3;
            case 4 -> isSelectedGoal4;
            default -> false;
        };
    }
}