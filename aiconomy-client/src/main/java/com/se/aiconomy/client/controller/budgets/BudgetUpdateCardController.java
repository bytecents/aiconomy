package com.se.aiconomy.client.controller.budgets;

import com.se.aiconomy.client.controller.BaseController;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import lombok.Setter;

public class BudgetUpdateCardController extends BaseController {
    @FXML
    @Setter
    @Getter
    StackPane rootPane;
    @Setter
    @Getter
    private BudgetController budgetController;
}
