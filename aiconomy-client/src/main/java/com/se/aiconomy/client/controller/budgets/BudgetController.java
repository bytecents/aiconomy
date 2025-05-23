package com.se.aiconomy.client.controller.budgets;

import com.se.aiconomy.client.common.MyFXMLLoader;
import com.se.aiconomy.client.controller.BaseController;
import com.se.aiconomy.server.handler.BudgetRequestHandler;
import com.se.aiconomy.server.model.dto.budget.request.BudgetCategoryInfoRequest;
import com.se.aiconomy.server.model.dto.budget.request.BudgetTotalInfoRequest;
import com.se.aiconomy.server.model.dto.budget.response.BudgetCategoryInfo;
import com.se.aiconomy.server.model.dto.budget.response.TotalBudgetInfo;
import com.se.aiconomy.server.service.impl.BudgetServiceImpl;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;

public class BudgetController extends BaseController {


    private final BudgetRequestHandler handler = new BudgetRequestHandler(new BudgetServiceImpl(JSONStorageServiceImpl.getInstance()));
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
    @FXML
    private GridPane budgetCardsContainer; // 存放每个类别预算卡片的容器
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
    @FXML
    @Setter
    @Getter
    private StackPane rootPane; // 这个是 main.fxml 的最外层 StackPane

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

    private void init() {
        userId = userInfo.getId();
        loadTotalBudgetInfo();
        loadCategoryBudgets();
    }

    public void refresh() {
        init();
    }

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
            daysLeftLabel.setText(+getDaysLeftThisMonth() + " days left");

        } catch (Exception e) {
            showError("加载总预算失败：" + e.getMessage());
        }
    }

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
            showError("加载分类预算失败：" + e.getMessage());
        }
    }

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
        String progressBarStyle;
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

    private int getDaysLeftThisMonth() {
        LocalDate today = LocalDate.now();
        YearMonth yearMonth = YearMonth.of(today.getYear(), today.getMonth());
        return yearMonth.lengthOfMonth() - today.getDayOfMonth();
    }

    private void showError(String msg) {
        System.err.println("❌ " + msg);
        // 或者弹窗提示
    }

    @FXML
    public void onAddBudgetClick(ActionEvent event) {
        try {
            // 加载 add_budget.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/budgets/add_budget.fxml"));
            Parent dialogContent = loader.load();
            // 获取 controller 并传入 rootPane
            AddBudgetController controller = loader.getController();
            controller.setRootPane(rootPane);
            controller.setBudgetController(this);
            controller.setUserInfo(userInfo);
            // 设置弹窗样式（你可以在 FXML 里设也行）
            dialogContent.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 20;");

            // 创建遮罩
            Region overlay = new Region();
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.5);");
            overlay.setPrefSize(rootPane.getWidth(), rootPane.getHeight());

            // 弹窗容器（居中）
            StackPane dialogWrapper = new StackPane(dialogContent);
            dialogWrapper.setMaxWidth(500);
            dialogWrapper.setMaxHeight(600);

            // 点击遮罩关闭弹窗
            overlay.setOnMouseClicked((MouseEvent e) -> {
                rootPane.getChildren().removeAll(overlay, dialogWrapper);
            });

            // 添加遮罩和弹窗到页面顶层
            rootPane.getChildren().addAll(overlay, dialogWrapper);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setBudgetCategoryInfo(BudgetCategoryInfo info) {
        this.info = info;
        updateView();
    }

    private void updateView() {
        categoryLabel.setText(info.getCategoryName());
        budgetAmountLabel.setText("预算：¥" + info.getBudgetAmount());
        spendingLabel.setText("已花费：¥" + info.getSpentAmount());

        double budget = info.getBudgetAmount();
        double spent = info.getSpentAmount();
        double progress = Math.min(spent / budget, 1.0); // cap at 1.0
        budgetProgressBar.setProgress(progress);

        if (spent > budget) {
            remainingOrOverLabel.setText("超支 ¥" + String.format("%.2f", spent - budget));
            remainingOrOverLabel.setStyle("-fx-text-fill: red;");
        } else {
            remainingOrOverLabel.setText("剩余 ¥" + String.format("%.2f", budget - spent));
            remainingOrOverLabel.setStyle("-fx-text-fill: green;");
        }
    }

    @FXML
    public void setOnOpenListener(BudgetController.OnOpenListener listener) {
    }

    public interface OnOpenListener {
        void onOpenBudgetPanel();
    }

}


