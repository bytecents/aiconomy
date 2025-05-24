package com.se.aiconomy.client.controller.budgets;

//import com.alibaba.fastjson2.internal.asm.Label;

import com.se.aiconomy.client.common.CustomDialog;
import com.se.aiconomy.client.controller.BaseController;
import com.se.aiconomy.server.handler.BudgetRequestHandler;
import com.se.aiconomy.server.model.dto.budget.request.BudgetAddRequest;
import com.se.aiconomy.server.model.dto.budget.request.BudgetUpdateRequest;
import com.se.aiconomy.server.model.dto.budget.response.BudgetCategoryInfo;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

@Setter
public class BudgetUpdateCardController extends BaseController {
    private final BudgetRequestHandler budgetRequestHandler = new BudgetRequestHandler();
    public TextField budgetAmountInput;
    public TextField additionalNotesInput;
    @FXML
    private RadioButton option1RadioButton;
    @FXML
    private RadioButton option2RadioButton;
    @FXML
    private RadioButton option3RadioButton;

    @FXML
    private ToggleGroup toggleGroup;
    @Setter
    @Getter
    private BudgetController budgetController;
    @FXML
    private StackPane rootPane;
    @FXML
    private VBox vbox1;
    @FXML
    private VBox vbox2;
    @FXML
    private VBox vbox3;
    @FXML
    private VBox vbox4;
    @FXML
    private VBox vbox5;
    @FXML
    private VBox vbox6;
    @FXML
    private VBox vbox7;
    @FXML
    private VBox vbox8;

    @FXML
    private GridPane categoryPanel;

    private BudgetController.OnOpenListener openListener;
    private String selectedCategory;
    private BudgetCategoryInfo budgetCategoryInfo;

    @FXML
    public void setOnOpenListener(BudgetController.OnOpenListener listener) {
        this.openListener = listener;
    }

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
        toggleGroup = new ToggleGroup();
        option1RadioButton.setToggleGroup(toggleGroup);
        option2RadioButton.setToggleGroup(toggleGroup);
        option3RadioButton.setToggleGroup(toggleGroup);
        option1RadioButton.setSelected(true);
    }

    @FXML
    private void onCancel(ActionEvent event) {
        closeDialog(event);
    }

    @FXML
    private void onSave(ActionEvent event) {
        if (!saveBudget()) {
            return;
        }
        closeDialog(event);
    }

    private boolean checkForm() {
        if (selectedCategory == null) {
            CustomDialog.show("Error", "Please select a category!", "error", "Try Again");
            return false;
        }

        String budgetText = budgetAmountInput.getText();
        if (budgetText == null || budgetText.trim().isEmpty()) {
            CustomDialog.show("Error", "Please input budget amount!", "error", "Try Again");
            return false;
        }
        try {
            double budgetAmount = Double.parseDouble(budgetText.trim());
            if (budgetAmount < 0) {
                CustomDialog.show("Error", "Budget amount must be non-negative!", "error", "Try Again");
                return false;
            }
        } catch (NumberFormatException e) {
            CustomDialog.show("Error", "Budget amount must be a valid number!", "error", "Try Again");
            return false;
        }

        if (toggleGroup.getSelectedToggle() == null) {
            CustomDialog.show("Error", "Please select an alert ratio!", "error", "Try Again");
            return false;
        }
        return true;
    }

    private @NotNull BudgetUpdateRequest getBudgetUpdateRequest() {
        double budgetAmount = Double.parseDouble(budgetAmountInput.getText());
        BudgetUpdateRequest budgetUpdateRequest = new BudgetUpdateRequest();
        budgetUpdateRequest.setUserId(userInfo.getId());
        budgetUpdateRequest.setBudgetCategory(selectedCategory);
        budgetUpdateRequest.setBudgetAmount(budgetAmount);
        budgetUpdateRequest.setAlertSettings(Double.parseDouble(budgetAmountInput.getText()));
        budgetUpdateRequest.setNotes(additionalNotesInput.getText());
        return budgetUpdateRequest;
    }

    private boolean saveBudget() {
        if (!checkForm()) {
            return false;
        }
        try {
            BudgetUpdateRequest budgetUpdateRequest = getBudgetUpdateRequest();
            Toggle selectedToggle = toggleGroup.getSelectedToggle();
            double alertRatio = 0.6;
            if (selectedToggle != null) {
                RadioButton selectedRadioButton = (RadioButton) selectedToggle;
                String selectedAlertRatio = selectedRadioButton.getText();
                alertRatio = Double.parseDouble(selectedAlertRatio.replace("%", "")) / 100.0;
            }
            budgetUpdateRequest.setAlertSettings(alertRatio);
            budgetUpdateRequest.setUserId(userInfo.getId());
            budgetUpdateRequest.setBudgetCategory(selectedCategory);
            budgetUpdateRequest.setBudgetAmount(Double.parseDouble(budgetAmountInput.getText()));
            budgetUpdateRequest.setNotes(additionalNotesInput.getText());
            budgetRequestHandler.handleBudgetUpdateRequest(budgetUpdateRequest);
        } catch (Exception e) {
            e.printStackTrace();
            CustomDialog.show("Error", e.getMessage(), "error", "Try Again");
        }
        return true;
    }

    @FXML
    private void refreshCategoryPanel() {
        List<VBox> allBoxes = List.of(vbox1, vbox2, vbox3, vbox4, vbox5, vbox6, vbox7, vbox8);
        for (VBox box : allBoxes) {
            box.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 15;");
            for (Node node : box.getChildren()) {
                if (node instanceof Label label) {
                    label.setStyle("-fx-font-size: 12px; -fx-font-weight: normal; -fx-text-fill: #6b7280;"); // Default label style
                }
            }
            for (Node node : box.getChildren()) {
                if (node instanceof StackPane stackPane) {
                    for (Node child : stackPane.getChildren()) {
                        if (child instanceof Circle circle) {
                            circle.setFill(Color.web("#f3f4f6"));  // Default circle color (gray-100)
                        } else if (child instanceof ImageView imageView) {
                            String oldUrl = imageView.getImage().getUrl();
                            if (oldUrl.contains("_blue.png")) {
                                String originalUrl = oldUrl.replace("_blue.png", ".png");
                                imageView.setImage(new Image(originalUrl));  // Restore original image
                            }
                        }
                    }
                }
            }
        }
        for (VBox box : allBoxes) {
            boolean isSelected = false;
            for (Node node : box.getChildren()) {
                if (node instanceof Label && Objects.equals(selectedCategory, ((Label) node).getText())) {
                    isSelected = true;
                    break;
                }
            }
            if (isSelected) {
                box.setStyle("-fx-background-color: #eff6ff; -fx-border-color: #2563eb; -fx-border-width: 1; -fx-padding: 15; -fx-border-radius: 10; -fx-background-radius: 10;");
                for (Node node : box.getChildren()) {
                    if (node instanceof StackPane stackPane) {
                        for (Node child : stackPane.getChildren()) {
                            if (child instanceof Circle circle) {
                                circle.setFill(Color.web("#bfdbfe"));  // Set the circle color to blue
                            } else if (child instanceof ImageView imageView) {
                                String oldUrl = imageView.getImage().getUrl();
                                if (oldUrl.contains(".png")) {
                                    String blueUrl = oldUrl.replace(".png", "_blue.png");
                                    imageView.setImage(new Image(blueUrl));  // Change the image to the "_blue" version
                                }
                            }
                        }
                    }
                }
                for (Node node : box.getChildren()) {
                    if (node instanceof Label label) {
                        label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2563eb;"); // Set bold and blue for selected box
                    }
                }
            }
        }
    }

    private void closeDialog(ActionEvent event) {
        if (rootPane != null) {
            rootPane.getChildren().removeIf(node ->
                    node != rootPane.getChildren().getFirst() // 保留主页面，移除弹窗和遮罩
            );
        }
        budgetController.refresh();
    }

    public void setBudget(BudgetCategoryInfo budgetCategoryInfo) {
        this.budgetCategoryInfo = budgetCategoryInfo;
        budgetAmountInput.setText(String.valueOf(budgetCategoryInfo.getBudgetAmount()));
        selectedCategory = budgetCategoryInfo.getCategoryName();
        refreshCategoryPanel();
    }
}