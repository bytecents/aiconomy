package com.se.aiconomy.client.controller;

import com.se.aiconomy.client.Application.StyleClassFixer;
import com.se.aiconomy.client.common.MyFXMLLoader;
import com.se.aiconomy.client.controller.budgets.BudgetController;
import com.se.aiconomy.client.controller.transactions.AddTransactionController;
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

/**
 * Controller for the sidebar navigation in the application.
 * Handles navigation button states, view loading, and dialog panels.
 */
public class SidebarController implements Initializable {

    /** Style for inactive navigation buttons. */
    private static final String INACTIVE_STYLE = "-fx-background-radius: 8;";
    /** Style for active navigation button text color. */
    private static final String ACTIVE_TEXT_COLOR = "-fx-text-fill: #2563EB;";

    /** Map of navigation button keys to their HBox nodes. */
    private final Map<String, HBox> navButtons = new HashMap<>();
    /** Map of navigation button keys to their icon ImageView nodes. */
    private final Map<String, ImageView> navIcons = new HashMap<>();
    /** Map of navigation button keys to their Label nodes. */
    private final Map<String, Label> navLabels = new HashMap<>();

    /** User information for the current session. */
    @Setter
    @FXML
    UserInfo userInfo;

    /** Root StackPane of the sidebar. */
    @FXML
    private StackPane root;

    /** ScrollPane for the main content area. */
    @FXML
    private ScrollPane contentArea;

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

    /** The key of the currently active panel. */
    private String activePanel;
    /** The controller of the currently loaded main content. */
    private BaseController controller;

    /**
     * Converts a JavaFX Color to a hex string.
     * @param color the color to convert
     * @return the hex string representation
     */
    private static String toHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    /**
     * Initializes the sidebar controller and sets up navigation.
     * @param location the location used to resolve relative paths for the root object, or null if unknown
     * @param resources the resources used to localize the root object, or null if not localized
     */
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

    /**
     * Sets the active navigation button and updates styles and icons.
     * @param buttonKey the key of the button to activate
     */
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

    /**
     * Opens a modal panel with the specified FXML file.
     * @param fxmlPath the path to the FXML file
     */
    private void openPanel(String fxmlPath) {
        MyFXMLLoader loader = new MyFXMLLoader(fxmlPath);
        Parent dialogContent = loader.load();
        BaseController panelController = loader.getController();
        panelController.setUserInfo(userInfo);

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

        if (fxmlPath.contains("add-transaction")) {
            AddTransactionController addTransactionController = loader.getController();
            addTransactionController.setOnCloseListener(() -> {
                FadeTransition fadeOut1 = new FadeTransition(Duration.millis(100), dialogWrapper);
                fadeOut1.setFromValue(1.0);
                fadeOut1.setToValue(0.0);

                FadeTransition fadeOut2 = new FadeTransition(Duration.millis(100), overlay);
                fadeOut1.setFromValue(1.0);
                fadeOut1.setToValue(0.0);

                ParallelTransition parallelOut = new ParallelTransition(fadeOut1, fadeOut2);
                parallelOut.setOnFinished(event -> root.getChildren().removeAll(overlay, dialogWrapper));
                parallelOut.play();
            });
            addTransactionController.setParentController(controller);
        }
    }

    /**
     * Opens the add transaction modal panel.
     */
    private void openAddTransactionPanel() {
        openPanel("/fxml/transactions/add-transaction.fxml");
    }

    /**
     * Opens the add budget modal panel.
     */
    public void openAddBudgetPanel() {
        openPanel("/fxml/budgets/add_budget.fxml");
    }

    /**
     * Loads a new view into the main content area with fade transitions.
     * @param fxmlPath the path to the FXML file to load
     */
    private void loadView(String fxmlPath) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(100), contentArea);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            try {
                MyFXMLLoader loader = new MyFXMLLoader(fxmlPath);
                Parent dialogContent = loader.load();
                StyleClassFixer.fixStyleClasses(dialogContent);

                controller = loader.getController();

                if (fxmlPath.contains("budgets") && controller instanceof BudgetController budgetController) {
                    budgetController.setOnOpenListener(this::openAddBudgetPanel);
                } else if (fxmlPath.contains("transactions") && controller instanceof TransactionsController transactionsController) {
                    transactionsController.setOnOpenListener(this::openAddTransactionPanel);
                }

                controller.setUserInfo(userInfo);
                controller.setMainController(this);
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

    /**
     * Switches to the dashboard view.
     */
    @FXML
    protected void switchToDashboard() {
        setActiveButton("dashboard");
        loadView("/fxml/dashboard.fxml");
    }

    /**
     * Switches to the transactions view.
     */
    @FXML
    protected void switchToTransactions() {
        setActiveButton("transactions");
        loadView("/fxml/transactions/transactions.fxml");
    }

    /**
     * Switches to the analytics view.
     */
    @FXML
    protected void switchToAnalytics() {
        setActiveButton("analytics");
        loadView("/fxml/analytics.fxml");
    }

    /**
     * Switches to the budgets view.
     */
    @FXML
    protected void switchToBudgets() {
        setActiveButton("budgets");
        loadView("/fxml/budgets/budgets.fxml");
    }

    /**
     * Switches to the accounts view.
     */
    @FXML
    protected void switchToAccounts() {
        setActiveButton("accounts");
        loadView("/fxml/accounts/accounts.fxml");
    }

    /**
     * Switches to the settings view.
     */
    @FXML
    protected void switchToSettings() {
        setActiveButton("settings");
        loadView("/fxml/settings.fxml");
    }
}