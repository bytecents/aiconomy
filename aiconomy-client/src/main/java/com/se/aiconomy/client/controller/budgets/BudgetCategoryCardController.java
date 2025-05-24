package com.se.aiconomy.client.controller.budgets;

import com.se.aiconomy.client.common.MyFXMLLoader;
import com.se.aiconomy.client.controller.BaseController;
import com.se.aiconomy.client.controller.accounts.UpdateAccountController;
import com.se.aiconomy.server.handler.BudgetRequestHandler;
import com.se.aiconomy.server.model.dto.budget.request.BudgetRemoveRequest;
import com.se.aiconomy.server.model.dto.budget.response.BudgetCategoryInfo;
import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Controller for the budget category card UI component.
 * Handles displaying category information, updating, and deleting categories.
 */
public class BudgetCategoryCardController extends BaseController {

    /** Handler for budget-related requests. */
    private final BudgetRequestHandler budgetRequestHandler = new BudgetRequestHandler();

    /** The root pane of the card. */
    @FXML
    @Setter
    @Getter
    StackPane rootPane;

    /** The colored circle background for the icon. */
    @FXML
    private Circle circle;

    /** The icon image for the category. */
    @FXML
    private ImageView icon;

    /** The label displaying the category name. */
    @FXML
    private Label categoryLabel;

    /** The label displaying the budget amount. */
    @FXML
    private Label budgetLabel;

    /** The progress bar showing budget usage. */
    @FXML
    private ProgressBar progressBar;

    /** The label displaying the usage percentage. */
    @FXML
    private Label percentageLabel;

    /** The label displaying the status text. */
    @FXML
    private Label statusLabel;

    /** The budget category info data. */
    @Setter
    @Getter
    private BudgetCategoryInfo budgetCategoryInfo;

    /** The clickable icon area for showing the popover. */
    @FXML
    private StackPane clickableIcon;

    /** The parent budget controller. */
    @Setter
    @Getter
    private BudgetController budgetController;

    /**
     * Initializes the controller and popover.
     */
    @FXML
    private void initialize() {
        initPopover();
    }

    /**
     * Initializes the popover for update and delete actions.
     */
    private void initPopover() {
        VBox popoverContent = new VBox(10);
        popoverContent.setStyle("""
                    -fx-background-color: rgba(255, 255, 255, 0.95);
                    -fx-padding: 10;
                    -fx-border-color: #ccc;
                    -fx-border-width: 1;
                    -fx-border-radius: 6;
                    -fx-background-radius: 6;
                """);

        Button updateBtn = new Button("Update");
        Button deleteBtn = new Button("Delete");

        updateBtn.setStyle("-fx-background-color: #e0f2fe; -fx-padding: 5 10; -fx-cursor: hand;");
        deleteBtn.setStyle("-fx-background-color: #fee2e2; -fx-padding: 5 10; -fx-cursor: hand;");

        popoverContent.getChildren().addAll(updateBtn, deleteBtn);

        Popup popup = new Popup();
        popup.setAutoHide(true);
        popup.getContent().add(popoverContent);

        // Fade in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), popoverContent);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // Fade out animation
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), popoverContent);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        clickableIcon.setOnMouseClicked(e -> {
            if (!popup.isShowing()) {
                popoverContent.setOpacity(0);
                popup.show(clickableIcon, e.getScreenX(), e.getScreenY());
                fadeIn.playFromStart();
            } else {
                fadeOut.setOnFinished(event -> popup.hide());
                fadeOut.playFromStart();
            }
        });

        updateBtn.setOnAction(e -> {
            onUpdateButtonClick();
            fadeOut.setOnFinished(event -> popup.hide());
            fadeOut.playFromStart();
        });

        deleteBtn.setOnAction(e -> {
            onDeleteButtonClick();
            fadeOut.setOnFinished(event -> popup.hide());
            fadeOut.playFromStart();
        });
    }

    /**
     * Handles the update button click event.
     * Opens the update dialog for the budget category.
     */
    private void onUpdateButtonClick() {
        MyFXMLLoader loader = new MyFXMLLoader("/fxml/budgets/budget_update_card.fxml");
        Parent dialogContent = loader.load();
        BudgetUpdateCardController controller = loader.getController();
        rootPane = budgetController.getRootPane();
        controller.setRootPane(rootPane);
        controller.setUserInfo(this.userInfo);
        controller.setBudget(this.budgetCategoryInfo);
        controller.setBudgetController(this.budgetController);
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
    }

    /**
     * Handles the delete button click event.
     * Deletes the budget category.
     */
    private void onDeleteButtonClick() {
        deleteBudgetCategory();
    }

    /**
     * Deletes the budget category and refreshes the parent controller.
     */
    private void deleteBudgetCategory() {
        BudgetRemoveRequest budgetRemoveRequest = new BudgetRemoveRequest(budgetCategoryInfo.getCategoryName());
        budgetRemoveRequest.setUserId(userInfo.getId());
        budgetRequestHandler.handleRemoveBudgetRequest(budgetRemoveRequest);
        budgetController.refresh();
    }

    /**
     * Sets the data for the card UI.
     *
     * @param category the category name
     * @param budget the budget amount
     * @param imagePath the icon image path
     * @param colorHex the color hex for the circle
     * @param progress the progress ratio (0-1)
     * @param percentageText the percentage text
     * @param statusText the status text
     */
    public void setCardData(String category, String budget, String imagePath, String colorHex,
                            double progress, String percentageText, String statusText) {
        categoryLabel.setText(category);
        budgetLabel.setText(budget);
        icon.setImage(new Image(imagePath));
        circle.setFill(Color.web(colorHex));
        adjustProgressBar(progressBar, progress);
        percentageLabel.setText(percentageText);
        adjustRatioText(percentageLabel, progress);
        statusLabel.setText(statusText);
    }

    /**
     * Adjusts the ratio text color based on the ratio value.
     *
     * @param label the label to update
     * @param ratio the ratio value (0-1)
     */
    private void adjustRatioText(Label label, double ratio) {
        if (label != null) {
            String redTextClass = "text-red-500";
            String greenTextClass = "text-green-500";
            String yellowTextClass = "text-yellow-500";
            String targetTextClass;
            DecimalFormat decimalFormat = new DecimalFormat("##.##%");
            String percentage = decimalFormat.format(ratio);
            if (0 <= ratio && ratio < 0.33) {
                targetTextClass = greenTextClass;
            } else if (ratio >= 0.33 && ratio < 0.67) {
                targetTextClass = yellowTextClass;
            } else if (ratio >= 0.67) {
                targetTextClass = redTextClass;
            } else {
                targetTextClass = redTextClass;
            }
            ObservableList<String> styleClasses = label.getStyleClass();
            styleClasses.removeAll(redTextClass, greenTextClass, yellowTextClass);
            if (!styleClasses.contains(targetTextClass)) {
                styleClasses.add(targetTextClass);
            }
        }
    }

    /**
     * Adjusts the progress bar color and value based on the ratio.
     *
     * @param progressBar the progress bar to update
     * @param ratio the ratio value (0-1)
     */
    private void adjustProgressBar(ProgressBar progressBar, double ratio) {
        if (progressBar != null) {
            String redClass = "red-bar";
            String greenClass = "green-bar";
            String yellowClass = "yellow-bar";

            String targetClass;
            if (0 <= ratio && ratio < 0.33) {
                targetClass = greenClass;
            } else if (ratio >= 0.33 && ratio < 0.67) {
                targetClass = yellowClass;
            } else if (ratio >= 0.67) {
                targetClass = redClass;
            } else {
                targetClass = redClass;
            }

            ObservableList<String> styleClasses = progressBar.getStyleClass();
            styleClasses.removeAll(redClass, greenClass, yellowClass);
            if (!styleClasses.contains(targetClass)) {
                styleClasses.add(targetClass);
            }
            double progress = ratio;
            if (ratio < 0) progress = 0;
            if (ratio > 1) progress = 1;
            progressBar.setProgress(progress);
        }
    }
}