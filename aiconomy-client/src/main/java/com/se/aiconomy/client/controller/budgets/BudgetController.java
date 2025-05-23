package com.se.aiconomy.client.controller.budgets;

import com.se.aiconomy.client.controller.BaseController;
import com.se.aiconomy.server.handler.BudgetRequestHandler;
import com.se.aiconomy.server.model.dto.budget.request.BudgetCategoryInfoRequest;
import com.se.aiconomy.server.model.dto.budget.request.BudgetTotalInfoRequest;
import com.se.aiconomy.server.model.dto.budget.response.BudgetCategoryInfo;
import com.se.aiconomy.server.model.dto.budget.response.TotalBudgetInfo;
import com.se.aiconomy.server.model.dto.user.response.UserInfo;
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
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Setter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class BudgetController extends BaseController {


    private final BudgetRequestHandler handler =
            new BudgetRequestHandler(new BudgetServiceImpl(JSONStorageServiceImpl.getInstance()));
    @FXML
    private Label totalBudgetLabel;
    @FXML
    private Label totalSpentLabel;
    @FXML
    private Label remainingLabel;
    @FXML
    private Label alertsLabel;
    @FXML
    private Label daysLeftLabel;
    @FXML
    private VBox budgetCardsContainer; // 存放每个类别预算卡片的容器
    @Setter
    private int userId;
    @FXML
    private UserInfo userInfo;
    private OnOpenListener openListener;
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
    private StackPane rootPane; // 这个是 main.fxml 的最外层 StackPane

    @FXML
    public void initialize() {
        if (userInfo == null) {
            Platform.runLater(() -> {
                // 延迟到事件调度线程中处理
                if (userInfo != null) {
                    init();
                }
            });
        } else {
            init();
        }
    }

    private void init() {
        loadTotalBudgetInfo();
        loadCategoryBudgets();
    }

    private void loadTotalBudgetInfo() {
        BudgetTotalInfoRequest request = new BudgetTotalInfoRequest();
        request.setUserId(String.valueOf(userId));

        try {
            TotalBudgetInfo info = handler.handleGetTotalBudgetRequest(request);

            totalBudgetLabel.setText("¥ " + info.getTotalBudget());
            totalSpentLabel.setText("¥ " + info.getTotalSpent());
            remainingLabel.setText("¥ " + info.getTotalRemaining());
            alertsLabel.setText(info.getTotalAlerts() + " 个超支警告");
            daysLeftLabel.setText("本月剩余 " + getDaysLeftThisMonth() + " 天");

        } catch (Exception e) {
            showError("加载总预算失败：" + e.getMessage());
        }
    }

    private void loadCategoryBudgets() {
        BudgetCategoryInfoRequest request = new BudgetCategoryInfoRequest();
        request.setUserId(String.valueOf(userId));

        try {
            List<BudgetCategoryInfo> categoryInfoList = handler.handleGetBudgetByCategory(request);

            budgetCardsContainer.getChildren().clear();
            for (BudgetCategoryInfo info : categoryInfoList) {
                Node card = createBudgetCard(info);
                budgetCardsContainer.getChildren().add(card);
            }

        } catch (Exception e) {
            showError("加载分类预算失败：" + e.getMessage());
        }
    }

    private Node createBudgetCard(BudgetCategoryInfo info) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddBudget.fxml"));
            Node node = loader.load();
            AddBudgetController controller = loader.getController();
            return node;
        } catch (IOException e) {
            showError("加载预算卡片失败：" + e.getMessage());
            return new Label("加载失败");
        }
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_budget.fxml"));
            Parent dialogContent = loader.load();
            // 获取 controller 并传入 rootPane
            AddBudgetController controller = loader.getController();
            controller.setRootPane(rootPane); // ⚠️这里的 rootPane 是你的页面最外层 StackPane

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
        this.openListener = listener;
    }

    public interface OnOpenListener {
        void onOpenBudgetPanel();
    }

}


