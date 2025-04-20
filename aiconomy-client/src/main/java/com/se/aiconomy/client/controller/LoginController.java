package com.se.aiconomy.client.controller;

import com.se.aiconomy.client.Application.StyleClassFixer;
import com.se.aiconomy.client.common.CustomDialog;
import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.handler.UserRequestHandler;
import com.se.aiconomy.server.model.dto.user.request.UserInfoRequest;
import com.se.aiconomy.server.model.dto.user.request.UserLoginRequest;
import com.se.aiconomy.server.model.dto.user.request.UserRegisterRequest;
import com.se.aiconomy.server.model.dto.user.response.UserInfo;
import com.se.aiconomy.server.model.entity.User;
import com.se.aiconomy.server.service.UserService;
import com.se.aiconomy.server.service.impl.UserServiceImpl;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class LoginController extends BaseController {
    private final UserRequestHandler userRequestHandler = new UserRequestHandler(new UserServiceImpl(JSONStorageServiceImpl.getInstance()));
    @FXML
    public Button loginButton;
    @FXML
    public Hyperlink signUpEntry;
    @FXML
    public TextField emailField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public TextField passwordTextField;
    @Setter
    @FXML
    private UserInfo userInfo;

    private boolean showPassword = false;

    private static @NotNull FadeTransition getFadeTransition(Stage stage, Scene scene) {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.2), stage.getScene().getRoot());
        fadeOut.setFromValue(1.0); // opacity = 1
        fadeOut.setToValue(0.0);   // opacity = 0
        fadeOut.setOnFinished(event1 -> {
            // after fade-out
            stage.setScene(scene);
            stage.show();

            // create fade-in animation
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), scene.getRoot());
            fadeIn.setFromValue(0.0);  // opacity = 0
            fadeIn.setToValue(1.0);    // opacity = 1
            fadeIn.play();             // play the fade-in animation
        });
        return fadeOut;
    }

    @FXML
    public void initialize() {
        passwordTextField.setVisible(false);
        UnaryOperator<TextFormatter.Change> filter = change -> {
            // not allow " "
            if (change.getText().contains(" ")) {
                return null;
            }
            return change;
        };
        emailField.setTextFormatter(new TextFormatter<>(filter));
        passwordField.setTextFormatter(new TextFormatter<>(filter));
        passwordTextField.setTextFormatter(new TextFormatter<>(filter));

//        UserRegisterRequest registerRequest = new UserRegisterRequest();
//        registerRequest.setEmail("test@example.com");
//        registerRequest.setPassword("test");
//        registerRequest.setFirstName("test");
//        registerRequest.setLastName("example");
//        try{
//            userRequestHandler.handleRegisterRequest(registerRequest);
//        } catch (Exception e){
//            e.printStackTrace();
//        }
    }

    @FXML
    public void login(ActionEvent event) throws IOException {

        String userInputEmail = emailField.getText();
        String userInputPassword;
        if (showPassword)
            userInputPassword = passwordTextField.getText();
        else
            userInputPassword = passwordField.getText();

        try {
            UserLoginRequest userLoginRequest = new UserLoginRequest();
            userLoginRequest.setEmail(userInputEmail);
            userLoginRequest.setPassword(userInputPassword);
            setUserInfo(userRequestHandler.handleLoginRequest(userLoginRequest));
            switchToMain(event, userInfo);
            CustomDialog.show("Success", "Login successfully", "success", "OK");

        } catch (RuntimeException rte) {
            System.out.println(rte.getMessage());
            CustomDialog.show("Error", rte.getMessage(), "error", "Try Again");
        }
    }

    public void switchToSignup(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/signup/signup.fxml")));
        Stage stage = (Stage) signUpEntry.getScene().getWindow();
        Scene currentScene = stage.getScene();

        // Use the scene width and height, which excludes window decorations
        double currentWidth = currentScene.getWidth();
        double currentHeight = currentScene.getHeight();

        Scene scene = new Scene(root, currentWidth, currentHeight);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void switchToMain(ActionEvent event, UserInfo userInfo) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));

        Parent root = loader.load();

        SidebarController sidebarController = loader.getController();
        sidebarController.setUserInfo(userInfo);

        System.out.println("Test: " + sidebarController.userInfo);

        StyleClassFixer.fixStyleClasses(root);

        // Get the current window, and load main page
        Stage stage = (Stage) loginButton.getScene().getWindow();
        Scene currentScene = stage.getScene();

        // Use the scene width and height, which excludes window decorations
        double currentWidth = currentScene.getWidth();
        double currentHeight = currentScene.getHeight();

        Scene scene = new Scene(root, currentWidth, currentHeight);
        stage.setScene(scene);
        stage.show();
        FadeTransition fadeOut = getFadeTransition(stage, scene);
        fadeOut.play();

    }

    public void handlePasswordVisibility(MouseEvent mouseEvent) {
        this.showPassword = !this.showPassword;
        String userInputPassword;
        if (showPassword)
            userInputPassword = passwordField.getText();
        else
            userInputPassword = passwordTextField.getText();
        passwordField.setVisible(!showPassword);
        passwordField.setText(userInputPassword);
        passwordTextField.setVisible(showPassword);
        passwordTextField.setText(userInputPassword);
    }

    @FXML
    private void loginTest(ActionEvent event) throws IOException {
        try {
            UserInfo userInfo = userRequestHandler.handleLoginRequest(UserLoginRequest.builder().email("2022213670@bupt.cn").password("123456").build());
            setUserInfo(userInfo);
            switchToMain(event, userInfo);
            CustomDialog.show("Success", "Login successfully", "success", "OK");
        } catch (Exception e) {
            UserRegisterRequest userRegisterRequest = UserRegisterRequest.builder()
                    .email("2022213670@bupt.cn")
                    .password("123456")
                    .firstName("John")
                    .lastName("Doe")
                    .phoneNumber("1234567890")
                    .birthDate(LocalDate.of(1990, 1, 1))
                    .currency("USD")
                    .financialGoal(List.of("Save for retirement", "Buy a house"))
                    .monthlyIncome(5000.0)
                    .mainExpenseType(List.of("Rent", "Groceries"))
                    .build();
            System.out.println(userRequestHandler.handleRegisterRequest(userRegisterRequest));

            UserInfoRequest userInfoRequest = new UserInfoRequest();
            userInfoRequest.setUserId("e2bd8d75-29a0-4cc0-9763-e806a52ea6e4");

            UserInfo userInfo = userRequestHandler.handleLoginRequest(UserLoginRequest.builder().email("2022213670@bupt.cn").password("123456").build());
            setUserInfo(userInfo);
            switchToMain(event, userInfo);
            CustomDialog.show("Success", "Login successfully", "success", "OK");
        }
    }
}
