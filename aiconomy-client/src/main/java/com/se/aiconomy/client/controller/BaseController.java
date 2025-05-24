package com.se.aiconomy.client.controller;

import com.se.aiconomy.server.model.dto.user.response.UserInfo;
import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Abstract base controller class for JavaFX controllers.
 * Provides common fields such as user information and main sidebar controller.
 */
@ToString
@Setter
@Getter
public abstract class BaseController {
    /**
     * The user information associated with the current session.
     */
    @FXML
    protected UserInfo userInfo;

    /**
     * Reference to the main sidebar controller.
     */
    public SidebarController mainController;
}