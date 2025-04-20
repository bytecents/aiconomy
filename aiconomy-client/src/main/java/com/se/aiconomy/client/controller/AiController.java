package com.se.aiconomy.client.controller;

import javafx.fxml.FXML;

import java.awt.*;

public class AiController {
    public interface OnCloseListener {
        void onCloseAiPanel();
    }

    private OnCloseListener closeListener;

    @FXML
    public void setOnCloseListener(OnCloseListener listener) {
        this.closeListener = listener;
    }

    @FXML
    public void closeAiPanel() {
        if (closeListener != null) {
            closeListener.onCloseAiPanel();
        }
    }
}
