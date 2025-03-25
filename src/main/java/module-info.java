module com.se.aiconomy {
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;
    requires org.slf4j;
    requires org.apache.commons.csv;
    requires jsondb.core;
    requires com.google.gson;
    requires langchain4j.core;
    requires langchain4j.open.ai;
    requires jinjava;
    requires io.github.cdimascio.dotenv.java;


    opens com.se.aiconomy to javafx.fxml;
    opens com.se.aiconomy.controller to javafx.fxml;
    exports com.se.aiconomy;
    exports com.se.aiconomy.controller;
}