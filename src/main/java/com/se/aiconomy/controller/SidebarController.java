package com.se.aiconomy.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class SidebarController implements Initializable {

    @FXML private ScrollPane contentArea;

    // Navigation buttons
    @FXML private HBox dashboardBtn;
    @FXML private HBox transactionsBtn;
    @FXML private HBox analyticsBtn;
    @FXML private HBox budgetsBtn;
    @FXML private HBox accountsBtn;
    @FXML private HBox settingsBtn;

    // Icons
    @FXML private ImageView dashboardIcon;
    @FXML private ImageView transactionsIcon;
    @FXML private ImageView analyticsIcon;
    @FXML private ImageView budgetsIcon;
    @FXML private ImageView accountsIcon;
    @FXML private ImageView settingsIcon;

    // Labels
    @FXML private Label dashboardLabel;
    @FXML private Label transactionsLabel;
    @FXML private Label analyticsLabel;
    @FXML private Label budgetsLabel;
    @FXML private Label accountsLabel;
    @FXML private Label settingsLabel;

    private final Map<String, HBox> navButtons = new HashMap<>();
    private final Map<String, ImageView> navIcons = new HashMap<>();
    private final Map<String, Label> navLabels = new HashMap<>();

    private static final String ACTIVE_STYLE = "-fx-background-color: #EFF6FF; -fx-background-radius: 8;";
    private static final String INACTIVE_STYLE = "-fx-background-radius: 8;";
    private static final String ACTIVE_TEXT_COLOR = "-fx-text-fill: #2563EB;";
    private static final String INACTIVE_TEXT_COLOR = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize maps for easy access to UI components
        navButtons.put("dashboard", dashboardBtn);
        navButtons.put("transactions", transactionsBtn);
        navButtons.put("analytics", analyticsBtn);
        navButtons.put("budgets", budgetsBtn);
        navButtons.put("accounts", accountsBtn);
        navButtons.put("settings", settingsBtn);

        navIcons.put("dashboard", dashboardIcon);
        navIcons.put("transactions", transactionsIcon);
        navIcons.put("analytics", analyticsIcon);
        navIcons.put("budgets", budgetsIcon);
        navIcons.put("accounts", accountsIcon);
        navIcons.put("settings", settingsIcon);

        navLabels.put("dashboard", dashboardLabel);
        navLabels.put("transactions", transactionsLabel);
        navLabels.put("analytics", analyticsLabel);
        navLabels.put("budgets", budgetsLabel);
        navLabels.put("accounts", accountsLabel);
        navLabels.put("settings", settingsLabel);

        // Load default view
        switchToDashboard();
    }

    private void setActiveButton(String buttonKey) {
        // Reset all buttons to inactive state
        navButtons.forEach((key, button) -> {
            button.setStyle(INACTIVE_STYLE);
            navLabels.get(key).setStyle(INACTIVE_TEXT_COLOR);

            // Reset icons to inactive
            String iconPath = String.format("/assets/%s.png", key);
            navIcons.get(key).setImage(new Image(Objects.requireNonNull(getClass().getResource(iconPath)).toExternalForm()));
        });

        // Set active state for selected button
        navButtons.get(buttonKey).setStyle(ACTIVE_STYLE);
        navLabels.get(buttonKey).setStyle(ACTIVE_TEXT_COLOR);

        // Set active icon
        String activeIconPath = String.format("/assets/%s-active.png", buttonKey);
        navIcons.get(buttonKey).setImage(new Image(Objects.requireNonNull(getClass().getResource(activeIconPath)).toExternalForm()));
    }

    private void loadView(String fxmlPath) {
        try {
            Node view = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            contentArea.setContent(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToDashboard() {
        setActiveButton("dashboard");
        loadView("/fxml/dashboard.fxml");
    }

    @FXML
    public void switchToTransactions() {
        setActiveButton("transactions");
        loadView("/fxml/transactions.fxml");
    }

    @FXML
    public void switchToAnalytics() {
        setActiveButton("analytics");
        loadView("/fxml/analytics.fxml");
    }

    @FXML
    public void switchToBudgets() {
        setActiveButton("budgets");
        loadView("/fxml/budgets.fxml");
    }

    @FXML
    public void switchToAccounts() {
        setActiveButton("accounts");
        loadView("/fxml/accounts.fxml");
    }

    @FXML
    public void switchToSettings() {
        setActiveButton("settings");
        loadView("/fxml/settings.fxml");
    }
}