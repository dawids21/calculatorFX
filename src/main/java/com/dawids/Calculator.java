package com.dawids;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

public class Calculator extends GridPane {
    private final TextField fieldMemory = new TextField();
    private final TextField fieldResult = new TextField();
    private final TextField fieldOperation = new TextField();
    private final TextField fieldActual = new TextField();
    private final Button[] buttons = new Button[26];
    private final Font font = Font.font("Noto Sans");
    private final SimpleDoubleProperty fontSize = new SimpleDoubleProperty(15);

    public Calculator() {
        super();
        setAlignment(Pos.CENTER);
        setPadding(new Insets(10));
        setHgap(5);
        setVgap(5);
        fontSize.bind(Bindings.min(widthProperty().divide(20), heightProperty().divide(20)));
        fieldMemory.setPrefWidth(100);
        fieldResult.setPrefWidth(150);
        fieldOperation.setPrefWidth(50);
        fieldActual.setPrefWidth(200);
        add(fieldMemory, 0, 0, 2, 1);
        add(fieldResult, 2, 0, 3, 1);
        add(fieldOperation, 0, 1);
        add(fieldActual, 1, 1, 4, 1);
        createButtons();
        for (var i = 0; i < 5; i++) {
            for (var j = 0; j < 5; j++) {
                add(buttons[i * 5 + j], j, i + 3);
            }
        }
        add(buttons[25], 4, 2);
    }

    private void createButtons() {
        buttons[0] = new Button("MC");
        buttons[1] = new Button("MR");
        buttons[2] = new Button("M+");
        buttons[3] = new Button("M-");
        buttons[4] = new Button("%");
        buttons[5] = new Button("7");
        buttons[6] = new Button("8");
        buttons[7] = new Button("9");
        buttons[8] = new Button("+");
        buttons[9] = new Button("-");
        buttons[10] = new Button("4");
        buttons[11] = new Button("5");
        buttons[12] = new Button("6");
        buttons[13] = new Button("*");
        buttons[14] = new Button("/");
        buttons[15] = new Button("1");
        buttons[16] = new Button("2");
        buttons[17] = new Button("3");
        buttons[18] = new Button("^");
        buttons[19] = new Button("\u221A");
        buttons[20] = new Button("+/-");
        buttons[21] = new Button("0");
        buttons[22] = new Button(".");
        buttons[23] = new Button("C");
        buttons[24] = new Button("=");
        buttons[25] = new Button("AC");
        for (Button button : buttons) {
            button.prefHeightProperty().bind(heightProperty().divide(8));
            button.prefWidthProperty().bind(widthProperty().divide(5));
            button.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));
            button.setFont(font);
        }
    }
}
