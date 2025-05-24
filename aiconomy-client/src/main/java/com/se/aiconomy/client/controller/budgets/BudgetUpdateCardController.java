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

/**
 * Controller for updating budget cards in the UI.
 * Handles user interactions for editing budget categories, amounts, and alert settings.
 */
@Setter
public class BudgetUpdateCardController extends BaseController {
    /** Handler for budget update requests. */
    private final BudgetRequestHandler budgetRequestHandler = new BudgetRequestHandler();

    /** Input field for budget amount. */
    public TextField budgetAmountInput;

    /** Input field for additional notes. */
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

    /** Listener for opening the budget panel. */
    private BudgetController.OnOpenListener openListener;

    /** The currently selected category. */
    private String selectedCategory;

    /** The current budget category info. */
    private BudgetCategoryInfo budgetCategoryInfo;

    /**
     * Sets the listener for opening the budget panel.
     * @param listener the listener to set
     */
    @FXML
    public void setOnOpenListener(BudgetController.OnOpenListener listener) {
        this.openListener = listener;
    }

    /**
     * Initializes the controller after its root element has been completely processed.
     */
    @FXML
    public void initialize() {
        if (userInfo == null) {
            Platform.runLater(() -> {
                // Delay initialization to the event dispatch thread
                if (userInfo != null) {
                    init();
                }
            });
        } else {
            init();
        }
    }

    /**
     * Initializes toggle group and sets default selection.
     */
    private void init() {
        toggleGroup = new ToggleGroup();
        option1RadioButton.setToggleGroup(toggleGroup);
        option2RadioButton.setToggleGroup(toggleGroup);
        option3RadioButton.setToggleGroup(toggleGroup);
        option1RadioButton.setSelected(true);
    }

    /**
     * Handles the cancel button action.
     * @param event the action event
     */
    @FXML
    private void onCancel(ActionEvent event) {
        closeDialog(event);
    }

    /**
     * Handles the save button action.
     * @param event the action event
     */
    @FXML
    private void onSave(ActionEvent event) {
        if (!saveBudget()) {
            return;
        }
        closeDialog(event);
    }

    /**
     * Checks if the form input is valid.
     * @return true if valid, false otherwise
     */
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

    /**
     * Constructs a {@link BudgetUpdateRequest} from the current form input.
     * @return the budget update request
     */
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

    /**
     * Saves the budget by validating the form and sending the update request.
     * @return true if saved successfully, false otherwise
     */
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

    /**
     * Refreshes the category panel UI to reflect the current selection.
     */
    @FXML
    private void refreshCategoryPanel() {
        List<VBox> allBoxes = List.of(vbox1, vbox2, vbox3, vbox4, vbox5, vbox6, vbox7, vbox8);
        for (VBox box : allBoxes) {
            box.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 15;");
            for (Node node : box.getChildren()) {
                if (node instanceof Label label) {
                    label.setStyle("-fx-font-size: 12px; -fx-font-weight: normal; -fx-text-fill: #6b7280;");
                }
            }
            for (Node node : box.getChildren()) {
                if (node instanceof StackPane stackPane) {
                    for (Node child : stackPane.getChildren()) {
                        if (child instanceof Circle circle) {
                            circle.setFill(Color.web("#f3f4f6"));
                        } else if (child instanceof ImageView imageView) {
                            String oldUrl = imageView.getImage().getUrl();
                            if (oldUrl.contains("_blue.png")) {
                                String originalUrl = oldUrl.replace("_blue.png", ".png");
                                imageView.setImage(new Image(originalUrl));
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
                                circle.setFill(Color.web("#bfdbfe"));
                            } else if (child instanceof ImageView imageView) {
                                String oldUrl = imageView.getImage().getUrl();
                                if (oldUrl.contains(".png")) {
                                    String blueUrl = oldUrl.replace(".png", "_blue.png");
                                    imageView.setImage(new Image(blueUrl));
                                }
                            }
                        }
                    }
                }
                for (Node node : box.getChildren()) {
                    if (node instanceof Label label) {
                        label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2563eb;");
                    }
                }
            }
        }
    }

    /**
     * Closes the dialog and refreshes the parent budget controller.
     * @param event the action event
     */
    private void closeDialog(ActionEvent event) {
        if (rootPane != null) {
            rootPane.getChildren().removeIf(node ->
                    node != rootPane.getChildren().getFirst()
            );
        }
        budgetController.refresh();
    }

    /**
     * Sets the budget category info and updates the UI accordingly.
     * @param budgetCategoryInfo the budget category info to set
     */
    public void setBudget(BudgetCategoryInfo budgetCategoryInfo) {
        this.budgetCategoryInfo = budgetCategoryInfo;
        budgetAmountInput.setText(String.valueOf(budgetCategoryInfo.getBudgetAmount()));
        selectedCategory = budgetCategoryInfo.getCategoryName();
        refreshCategoryPanel();
    }
}