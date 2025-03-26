package com.se.aiconomy.client.Application;

//import atlantafx.base.theme.PrimerDark;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AIconomyApplication extends Application {
    /**
     * Entry point for the JavaFX application
     *
     * @param stage The main window
     * @throws IOException If loading the FXML file fails
     */
    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
//        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet()); // TODO: dark mode
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1080, 720);
        stage.setTitle("AIconomy");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
