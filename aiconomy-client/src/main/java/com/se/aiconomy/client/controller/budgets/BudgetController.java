package com.se.aiconomy.client.controller.budgets;

import com.se.aiconomy.client.Application.StyleClassFixer;
import com.se.aiconomy.client.common.MyFXMLLoader;
import com.se.aiconomy.client.controller.BaseController;
import com.se.aiconomy.client.controller.ai.AiController;
import com.se.aiconomy.server.handler.BudgetRequestHandler;
import com.se.aiconomy.server.model.dto.budget.request.BudgetCategoryInfoRequest;
import com.se.aiconomy.server.model.dto.budget.request.BudgetTotalInfoRequest;
import com.se.aiconomy.server.model.dto.budget.response.BudgetCategoryInfo;
import com.se.aiconomy.server.model.dto.budget.response.TotalBudgetInfo;
import com.se.aiconomy.server.service.impl.BudgetServiceImpl;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;

/**
 * Controller for managing the budget view and operations.
 * Handles loading, displaying, and updating budget and category information.
 */
public class BudgetController extends BaseController {

    /** Handler for budget requests. */
    private final BudgetRequestHandler handler = new BudgetRequestHandler(new BudgetServiceImpl(JSONStorageServiceImpl.getInstance()));

    @FXML
    private ScrollPane aiOptimizePanel;
    @FXML
    private ColumnConstraints mainCol;
    @FXML
    private ColumnConstraints aiOptimizeCol;
    @FXML
    private Label totalBudgetLabel;
    @FXML
    private Label totalSpentLabel;
    @FXML
    private Label dailyAvailableBudgetLabel;
    @FXML
    private Label totalUsedRatioLabel;
    @FXML
    private Label remainingLabel;
    @FXML
    private Label alertsLabel;
    @FXML
    private Label daysLeftLabel;
    /** Container for budget category cards. */
    @FXML
    private GridPane budgetCardsContainer;
    @Setter
    private String userId;
    @FXML
    private Label categoryLabel;
    @FXML
    private Label budgetAmountLabel;
    @FXML
    private Label spendingLabel;
    @FXML
    private ProgressBar budgetProgressBar;
    @FXML
    private Label remainingOrOverLabel;
    private BudgetCategoryInfo info;
    /** The root pane of main.fxml. */
    @FXML
    @Setter
    @Getter
    private StackPane rootPane;

    /**
     * Initializes the controller.
     * Loads budget data if user info is available.
     */
    @FXML
    public void initialize() {
        if (userInfo == null) {
            Platform.runLater(() -> {
                if (userInfo != null) {
                    init();
                }
            });
        } else {
            init();
        }
    }

    /**
     * Initializes the budget view and loads data.
     */
    private void init() {
        userId = userInfo.getId();
        loadTotalBudgetInfo();
        loadCategoryBudgets();
        mainCol.setPercentWidth(100);
        aiOptimizeCol.setPercentWidth(0);
    }

    /**
     * Refreshes the budget view.
     */
    public void refresh() {
        init();
    }

    /**
     * Loads and displays the total budget information.
     */
    private void loadTotalBudgetInfo() {
        BudgetTotalInfoRequest request = new BudgetTotalInfoRequest();
        request.setUserId(userId);

        try {
            TotalBudgetInfo info = handler.handleGetTotalBudgetRequest(request);
            System.out.println(info);
            totalBudgetLabel.setText("$ " + info.getTotalBudget());
            totalUsedRatioLabel.setText("Used " + String.format("%.2f%%", info.getTotalUsedRatio() * 100));
            totalSpentLabel.setText("$ " + info.getTotalSpent());
            remainingLabel.setText("Left $ " + info.getTotalRemaining());
            alertsLabel.setText(info.getTotalAlerts() + "");
            dailyAvailableBudgetLabel.setText("$ " + String.format("%.2f", info.getDailyAvailableBudget()));
            daysLeftLabel.setText(getDaysLeftThisMonth() + " days left");
        } catch (Exception e) {
            showError("Failed to load total budget: " + e.getMessage());
        }
    }

    /**
     * Loads and displays the budget information for each category.
     */
    private void loadCategoryBudgets() {
        BudgetCategoryInfoRequest request = new BudgetCategoryInfoRequest();
        request.setUserId(String.valueOf(userId));

        try {
            List<BudgetCategoryInfo> categoryInfoList = handler.handleGetBudgetByCategory(request);
            System.out.println(categoryInfoList);
            budgetCardsContainer.getChildren().clear();
            int row = 0;
            int col = 0;

            for (BudgetCategoryInfo info : categoryInfoList) {
                Node card = createBudgetCard(info);
                budgetCardsContainer.add(card, col, row);
                col++;
                if (col > 1) {
                    col = 0;
                    row++;
                }
            }
        } catch (Exception e) {
            showError("Failed to load category budgets: " + e.getMessage());
        }
    }

    /**
     * Creates a budget card node for a given category.
     *
     * @param info the budget category info
     * @return the node representing the budget card
     */
    private Node createBudgetCard(BudgetCategoryInfo info) {
        MyFXMLLoader loader = new MyFXMLLoader("/fxml/budgets/budget_category_card.fxml");
        Node node = loader.load();
        BudgetCategoryCardController controller = loader.getController();
        controller.setBudgetController(this);
        controller.setBudgetCategoryInfo(info);
        controller.setRootPane(rootPane);
        controller.setUserInfo(userInfo);
        String imagePath;
        String backgroundColor;
        String percentageColorClass;
        String categoryName = info.getCategoryName().toLowerCase();

        if (categoryName.contains("food") || categoryName.contains("dining")) {
            imagePath = Objects.requireNonNull(getClass().getResource("/assets/FoodDining.png")).toExternalForm();
            backgroundColor = "#e0f2fe";
            percentageColorClass = "text-red-500";
        } else if (categoryName.contains("education")) {
            imagePath = Objects.requireNonNull(getClass().getResource("/assets/Education1_blue.png")).toExternalForm();
            backgroundColor = "#e0f2fe";
            percentageColorClass = "text-green-500";
        } else if (categoryName.contains("transport")) {
            imagePath = Objects.requireNonNull(getClass().getResource("/assets/Transportation.png")).toExternalForm();
            backgroundColor = "#dcfce7";
            percentageColorClass = "text-green-500";
        } else if (categoryName.contains("shop")) {
            imagePath = Objects.requireNonNull(getClass().getResource("/assets/Shopping.png")).toExternalForm();
            backgroundColor = "#e0bbff";
            percentageColorClass = "text-red-500";
        } else if (categoryName.contains("entertain")) {
            imagePath = Objects.requireNonNull(getClass().getResource("/assets/Entertainment.png")).toExternalForm();
            backgroundColor = "#fef9c3";
            percentageColorClass = "text-yellow-500";
        } else if (categoryName.contains("utilities")) {
            imagePath = Objects.requireNonNull(getClass().getResource("/assets/Utilities.png")).toExternalForm();
            backgroundColor = "#ffedd5";
            percentageColorClass = "text-orange-500";
        } else if (categoryName.contains("gift")) {
            imagePath = Objects.requireNonNull(getClass().getResource("/assets/Gift1_blue.png")).toExternalForm();
            backgroundColor = "#e0f2fe";
            percentageColorClass = "text-orange-500";
        } else if (categoryName.contains("housing")) {
            imagePath = Objects.requireNonNull(getClass().getResource("/assets/House1_blue.png")).toExternalForm();
            backgroundColor = "#e0f2fe";
            percentageColorClass = "text-blue-500";
        } else if (categoryName.contains("groceries")) {
            imagePath = Objects.requireNonNull(getClass().getResource("/assets/shopping-basket_blue.png")).toExternalForm();
            backgroundColor = "#e0f2fe";
            percentageColorClass = "text-blue-500";
        } else {
            imagePath = Objects.requireNonNull(getClass().getResource("/assets/Other.png")).toExternalForm();
            backgroundColor = "#f3f4f6";
            percentageColorClass = "text-gray-500";
        }
        String budgetText = String.format("$%.2f / month", info.getBudgetAmount());
        String percentageText = String.format("%.0f%% used", info.getUsedRatio() * 100);
        String statusText;
        if (info.getSpentAmount() > info.getBudgetAmount()) {
            double over = info.getSpentAmount() - info.getBudgetAmount();
            statusText = String.format("$%.2f over budget", over);
        } else {
            statusText = String.format("$%.2f left", info.getRemainingAmount());
        }
        controller.setCardData(
                info.getCategoryName(),
                budgetText,
                imagePath,
                backgroundColor,
                info.getUsedRatio(),
                percentageText,
                statusText
        );
        return node;
    }

    /**
     * Gets the number of days left in the current month.
     *
     * @return days left in this month
     */
    private int getDaysLeftThisMonth() {
        LocalDate today = LocalDate.now();
        YearMonth yearMonth = YearMonth.of(today.getYear(), today.getMonth());
        return yearMonth.lengthOfMonth() - today.getDayOfMonth();
    }

    /**
     * Displays an error message.
     *
     * @param msg the error message
     */
    private void showError(String msg) {
        System.err.println("❌ " + msg);
        // Or show a popup dialog
    }

    /**
     * Handles the add budget button click event.
     *
     * @param event the action event
     */
    @FXML
    public void onAddBudgetClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/budgets/add_budget.fxml"));
            Parent dialogContent = loader.load();
            AddBudgetController controller = loader.getController();
            controller.setRootPane(rootPane);
            controller.setBudgetController(this);
            controller.setUserInfo(userInfo);
            dialogContent.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 20;");

            Region overlay = new Region();
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.5);");
            overlay.setPrefSize(rootPane.getWidth(), rootPane.getHeight());

            StackPane dialogWrapper = new StackPane(dialogContent);
            dialogWrapper.setMaxWidth(500);
            dialogWrapper.setMaxHeight(600);

            overlay.setOnMouseClicked((MouseEvent e) -> {
                rootPane.getChildren().removeAll(overlay, dialogWrapper);
            });

            rootPane.getChildren().addAll(overlay, dialogWrapper);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the budget category info and updates the view.
     *
     * @param info the budget category info
     */
    public void setBudgetCategoryInfo(BudgetCategoryInfo info) {
        this.info = info;
        updateView();
    }

    /**
     * Updates the budget category view with current info.
     */
    private void updateView() {
        categoryLabel.setText(info.getCategoryName());
        budgetAmountLabel.setText("预算：¥" + info.getBudgetAmount());
        spendingLabel.setText("已花费：¥" + info.getSpentAmount());

        double budget = info.getBudgetAmount();
        double spent = info.getSpentAmount();
        double progress = Math.min(spent / budget, 1.0);
        budgetProgressBar.setProgress(progress);

        if (spent > budget) {
            remainingOrOverLabel.setText("超支 ¥" + String.format("%.2f", spent - budget));
            remainingOrOverLabel.setStyle("-fx-text-fill: red;");
        } else {
            remainingOrOverLabel.setText("剩余 ¥" + String.format("%.2f", budget - spent));
            remainingOrOverLabel.setStyle("-fx-text-fill: green;");
        }
    }

    /**
     * Sets a listener for opening the budget panel.
     *
     * @param listener the listener
     */
    @FXML
    public void setOnOpenListener(BudgetController.OnOpenListener listener) {
        // Implementation can be added as needed
    }

    /**
     * Listener interface for opening the budget panel.
     */
    public interface OnOpenListener {
        void onOpenBudgetPanel();
    }

    /**
     * Opens the AI optimize panel with animation.
     */
    @FXML
    public void openAiOptimizePanel() {
        if (aiOptimizePanel.getContent() != null) {
            return;
        }
        try {
            MyFXMLLoader loader = new MyFXMLLoader("/fxml/budgets/ai-optimize-panel.fxml");
            Node view = loader.load();
            AiOptimizePanelController controller = loader.getController();
            aiOptimizePanel.setContent(view);

            controller.setUserInfo(userInfo);
            controller.setOnCloseListener(this::closeAiOptimizePanel);

            Duration duration = Duration.millis(300);
            Timeline activetimeline = new Timeline(new KeyFrame(duration));
            activetimeline.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                double t = newTime.toMillis() / duration.toMillis();
                double easedProgress;
                if (t < 0.5) {
                    easedProgress = 4 * t * t * t;
                } else {
                    easedProgress = 1 - Math.pow(-2 * t + 2, 3) / 2;
                }
                double mainColWidth = 100 - 40 * easedProgress;
                double aiColWidth = 40 * easedProgress;
                mainCol.setPercentWidth(mainColWidth);
                aiOptimizeCol.setPercentWidth(aiColWidth);
            });
            activetimeline.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the AI optimize panel with animation.
     */
    private void closeAiOptimizePanel() {
        if (aiOptimizePanel.getContent() == null) {
            return;
        }
        Duration duration = Duration.millis(300);
        Timeline activetimeline = new Timeline(new KeyFrame(duration));
        activetimeline.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            double t = newTime.toMillis() / duration.toMillis();
            double easedProgress;
            if (t < 0.5) {
                easedProgress = 4 * t * t * t;
            } else {
                easedProgress = 1 - Math.pow(-2 * t + 2, 3) / 2;
            }
            double mainColWidth = 60 + 40 * easedProgress;
            double aiColWidth = 40 - 40 * easedProgress;
            mainCol.setPercentWidth(mainColWidth);
            aiOptimizeCol.setPercentWidth(aiColWidth);
        });
        activetimeline.setOnFinished(event -> {
            aiOptimizePanel.setContent(null);
            mainCol.setPercentWidth(100);
            aiOptimizeCol.setPercentWidth(0);
        });
        activetimeline.play();
    }
}