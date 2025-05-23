package com.se.aiconomy.client.controller;

import com.se.aiconomy.client.Application.StyleClassFixer;
import com.se.aiconomy.client.common.MyFXMLLoader;
import com.se.aiconomy.client.controller.budgets.BudgetController;
import com.se.aiconomy.client.controller.transactions.TransactionsController;
import com.se.aiconomy.server.model.dto.user.response.UserInfo;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import lombok.Setter;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class SidebarController implements Initializable {

    //    private static final String ACTIVE_STYLE = "-fx-background-color: #EFF6FF; -fx-background-radius: 8;";
    private static final String INACTIVE_STYLE = "-fx-background-radius: 8;";
    private static final String ACTIVE_TEXT_COLOR = "-fx-text-fill: #2563EB;";
    private final Map<String, HBox> navButtons = new HashMap<>();
    private final Map<String, ImageView> navIcons = new HashMap<>();
    private final Map<String, Label> navLabels = new HashMap<>();
    @Setter
    @FXML
    UserInfo userInfo;
    @FXML
    private StackPane root;
    @FXML
    private ScrollPane contentArea;
    // Navigation buttons
    @FXML
    private HBox dashboardBtn;
    @FXML
    private HBox transactionsBtn;
    @FXML
    private HBox analyticsBtn;
    @FXML
    private HBox budgetsBtn;
    @FXML
    private HBox accountsBtn;
    @FXML
    private HBox settingsBtn;
    // Icons
    @FXML
    private ImageView dashboardIcon;
    @FXML
    private ImageView transactionsIcon;
    @FXML
    private ImageView analyticsIcon;
    @FXML
    private ImageView budgetsIcon;
    @FXML
    private ImageView accountsIcon;
    @FXML
    private ImageView settingsIcon;
    // Labels
    @FXML
    private Label dashboardLabel;
    @FXML
    private Label transactionsLabel;
    @FXML
    private Label analyticsLabel;
    @FXML
    private Label budgetsLabel;
    @FXML
    private Label accountsLabel;
    @FXML
    private Label settingsLabel;
//    private static final String INACTIVE_TEXT_COLOR = "";
    private String activePanel;

    private static String toHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

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
        if (buttonKey.equals(activePanel)) {
            return;
        }

        Color inactiveBg = Color.web("#F9FAFB");
        Color activeBg = Color.web("#EFF6FF");
        Duration duration = Duration.millis(200);

        if (activePanel != null) {
            HBox inactiveButton = navButtons.get(activePanel);
            Timeline inactiveTimeline = new Timeline(new KeyFrame(duration));
            inactiveTimeline.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                double frac = newTime.toMillis() / duration.toMillis();
                Color currentBg = activeBg.interpolate(inactiveBg, frac);
                inactiveButton.setStyle("-fx-background-color: " + toHex(currentBg) + "; -fx-background-radius: 8;");
            });
            inactiveTimeline.play();

            String inactiveIconPath = String.format("/assets/%s.png", activePanel);
            navIcons.get(activePanel).setImage(new Image(Objects.requireNonNull(getClass().getResource(inactiveIconPath)).toExternalForm()));
            navButtons.get(activePanel).setStyle(INACTIVE_STYLE);
            navLabels.get(activePanel).setStyle(INACTIVE_STYLE);
        }

        HBox activeButton = navButtons.get(buttonKey);
        Timeline activetimeline = new Timeline(new KeyFrame(duration));
        activetimeline.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            double frac = newTime.toMillis() / duration.toMillis();
            Color currentBg = inactiveBg.interpolate(activeBg, frac);
            activeButton.setStyle("-fx-background-color: " + toHex(currentBg) + "; -fx-background-radius: 8;");
        });
        activetimeline.play();

        String activeIconPath = String.format("/assets/%s-active.png", buttonKey);
        navIcons.get(buttonKey).setImage(new Image(Objects.requireNonNull(getClass().getResource(activeIconPath)).toExternalForm()));
        navLabels.get(buttonKey).setStyle(ACTIVE_TEXT_COLOR);
        activePanel = buttonKey;
    }

    private void openPanel(String fxmlPath) {
        MyFXMLLoader loader = new MyFXMLLoader(fxmlPath);
        Parent dialogContent = loader.load();
        BaseController controller = loader.getController();
        controller.setUserInfo(userInfo);

        Region overlay = new Region();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.5);");
        overlay.setPrefSize(root.getWidth(), root.getHeight());

        StackPane dialogWrapper = new StackPane(dialogContent);
        dialogWrapper.setMaxWidth(600);
        dialogWrapper.setMaxHeight(600);

        overlay.setOnMouseClicked((MouseEvent e) -> {
            FadeTransition fadeOut1 = new FadeTransition(Duration.millis(100), dialogWrapper);
            fadeOut1.setFromValue(1.0);
            fadeOut1.setToValue(0.0);

            FadeTransition fadeOut2 = new FadeTransition(Duration.millis(100), overlay);
            fadeOut1.setFromValue(1.0);
            fadeOut1.setToValue(0.0);

            ParallelTransition parallel = new ParallelTransition(fadeOut1, fadeOut2);
            parallel.setOnFinished(event -> root.getChildren().removeAll(overlay, dialogWrapper));
            parallel.play();
        });

        root.getChildren().addAll(overlay, dialogWrapper);

        FadeTransition fadeIn1 = new FadeTransition(Duration.millis(100), overlay);
        fadeIn1.setFromValue(0.0);
        fadeIn1.setToValue(1.0);

        FadeTransition fadeIn2 = new FadeTransition(Duration.millis(100), dialogWrapper);
        fadeIn2.setFromValue(0.0);
        fadeIn2.setToValue(1.0);

        ParallelTransition parallel = new ParallelTransition(fadeIn1, fadeIn2);
        parallel.play();
    }

    private void openAddTransactionPanel() {
        openPanel("/fxml/transactions/add-transaction.fxml");
    }

    public void openAddBudgetPanel() {
        openPanel("/fxml/add_budget.fxml");
    }

    private void loadView(String fxmlPath) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(100), contentArea);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            try {
                MyFXMLLoader loader = new MyFXMLLoader(fxmlPath);
                Parent dialogContent = loader.load();
                StyleClassFixer.fixStyleClasses(dialogContent);

                BaseController controller = loader.getController();
                controller.setUserInfo(userInfo);
                controller.setMainController(this);

                if (fxmlPath.contains("budgets")) {
                    BudgetController budgetController = loader.getController();
                    budgetController.setOnOpenListener(this::openAddBudgetPanel);
                } else if (fxmlPath.contains("transactions")) {
                    TransactionsController transactionsController = loader.getController();
                    transactionsController.setOnOpenListener(this::openAddTransactionPanel);
                }

                contentArea.setContent(dialogContent);


                FadeTransition fadeIn = new FadeTransition(Duration.millis(100), contentArea);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        fadeOut.play();
    }

    @FXML
    protected void switchToDashboard() {
        setActiveButton("dashboard");
        loadView("/fxml/dashboard.fxml");
    }

    @FXML
    protected void switchToTransactions() {
        setActiveButton("transactions");
        loadView("/fxml/transactions/transactions.fxml");
    }

    @FXML
    protected void switchToAnalytics() {
        setActiveButton("analytics");
        loadView("/fxml/analytics.fxml");
    }

    @FXML
    protected void switchToBudgets() {
        setActiveButton("budgets");
        loadView("/fxml/budgets.fxml");
    }

    @FXML
    protected void switchToAccounts() {
        setActiveButton("accounts");
        loadView("/fxml/accounts.fxml");
    }

    @FXML
    protected void switchToSettings() {
        setActiveButton("settings");
        loadView("/fxml/settings.fxml");
    }
}