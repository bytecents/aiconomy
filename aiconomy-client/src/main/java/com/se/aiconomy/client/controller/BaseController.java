package com.se.aiconomy.client.controller;

import com.se.aiconomy.server.model.dto.user.response.UserInfo;
import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
public abstract class BaseController {
    @FXML
    protected UserInfo userInfo;

    public SidebarController mainController;
}
