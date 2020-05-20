package com.dawids;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        var gridPane = new Calculator();
        var scene = new Scene(gridPane, 400, 500);
        stage.setScene(scene);
        gridPane.requestFocus();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}