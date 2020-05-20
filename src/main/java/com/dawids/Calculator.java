package com.dawids;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.StringConverter;

public class Calculator extends GridPane {
    private final TextField fieldMemory = new TextField();
    private final TextField fieldResult = new TextField();
    private final TextField fieldOperation = new TextField();
    private final TextField fieldActual = new TextField();
    private final Button[] buttons = new Button[26];
    private final Font font = Font.font("Noto Sans");
    private final SimpleDoubleProperty fontSize = new SimpleDoubleProperty(15);
    private final SimpleDoubleProperty valueResult = new SimpleDoubleProperty(0.0);
    private final SimpleDoubleProperty valueMemory = new SimpleDoubleProperty(0.0);

    public Calculator() {
        super();
        setAlignment(Pos.CENTER);
        setPadding(new Insets(10));
        setHgap(5);
        setVgap(5);
        fontSize.bind(Bindings.min(widthProperty().divide(20), heightProperty().divide(20)));

        fieldMemory.prefWidthProperty().bind(widthProperty().divide(5).multiply(2));
        fieldMemory.setFont(font);
        fieldMemory.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));
        fieldMemory.setEditable(false);
        var converterMemory = new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return String.valueOf(object);
                //TODO add conversion
            }

            public Number fromString(String string) {
                return Double.parseDouble(string);
            }
        };
        var formatterMemory = new TextFormatter<>(converterMemory);
        formatterMemory.valueProperty().bind(valueMemory);
        fieldMemory.setTextFormatter(formatterMemory);

        fieldResult.prefWidthProperty().bind(widthProperty().divide(5).multiply(3));
        fieldResult.setFont(font);
        fieldResult.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));
        fieldResult.setEditable(false);
        //fieldResult.setText("0");
        var converterResult = new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return String.valueOf(object);
                //TODO add conversion
            }

            @Override
            public Number fromString(String string) {
                return Double.parseDouble(string);
            }
        };
        var formatterResult = new TextFormatter<Number>(converterResult);
        formatterResult.valueProperty().bind(valueResult);
        fieldResult.setTextFormatter(formatterResult);

        fieldOperation.prefWidthProperty().bind(widthProperty().divide(5));
        fieldOperation.setFont(font);
        fieldOperation.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));
        fieldOperation.setAlignment(Pos.CENTER);
        fieldOperation.setEditable(false);

        fieldActual.prefWidthProperty().bind(widthProperty().divide(5).multiply(4));
        fieldActual.setFont(font);
        fieldActual.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));
        fieldActual.setAlignment(Pos.CENTER_RIGHT);
        fieldActual.setEditable(false);
        fieldActual.setText("0");

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
        buttons[0].setOnAction(event -> valueResult.set(0.0));

        buttons[1] = new Button("MR");
        buttons[1].setOnAction(event -> {
            if (valueMemory.get() % 1 == 0) {
                fieldActual.setText(String.valueOf((int) valueMemory.get()));
            } else {
                fieldActual.setText(String.valueOf(valueMemory.get()));
            }
        });

        buttons[2] = new Button("M+");
        buttons[2].setOnAction(event -> valueMemory.set(valueMemory.get() + Double.parseDouble(fieldActual.getText())));

        buttons[3] = new Button("M-");
        buttons[3].setOnAction(event -> valueMemory.set(valueMemory.get() - Double.parseDouble(fieldActual.getText())));

        buttons[4] = new Button("%");
        buttons[4].setOnAction(event -> fieldOperation.setText("%"));

        buttons[5] = new Button("7");
        buttons[5].setOnAction(event -> addToActual("7"));

        buttons[6] = new Button("8");
        buttons[6].setOnAction(event -> addToActual("8"));

        buttons[7] = new Button("9");
        buttons[7].setOnAction(event -> addToActual("9"));

        buttons[8] = new Button("+");
        buttons[8].setOnAction(event -> fieldOperation.setText("+"));

        buttons[9] = new Button("-");
        buttons[9].setOnAction(event -> fieldOperation.setText("-"));

        buttons[10] = new Button("4");
        buttons[10].setOnAction(event -> addToActual("4"));

        buttons[11] = new Button("5");
        buttons[11].setOnAction(event -> addToActual("5"));

        buttons[12] = new Button("6");
        buttons[12].setOnAction(event -> addToActual("6"));

        buttons[13] = new Button("*");
        buttons[13].setOnAction(event -> fieldOperation.setText("*"));

        buttons[14] = new Button("/");
        buttons[14].setOnAction(event -> fieldOperation.setText("/"));

        buttons[15] = new Button("1");
        buttons[15].setOnAction(event -> addToActual("1"));

        buttons[16] = new Button("2");
        buttons[16].setOnAction(event -> addToActual("2"));

        buttons[17] = new Button("3");
        buttons[17].setOnAction(event -> addToActual("3"));

        buttons[18] = new Button("^");
        buttons[18].setOnAction(event -> fieldOperation.setText("^"));

        buttons[19] = new Button("√");
        buttons[19].setOnAction(event -> fieldOperation.setText("√"));

        buttons[20] = new Button("+/-");

        buttons[21] = new Button("0");
        buttons[21].setOnAction(event -> addToActual("0"));

        buttons[22] = new Button(".");

        buttons[23] = new Button("C");
        buttons[23].setOnAction(event -> {
            fieldActual.setText("0");
            fieldOperation.setText("");
        });

        buttons[24] = new Button("=");

        buttons[25] = new Button("AC");
        buttons[25].setOnAction(event -> {
            fieldActual.setText("0");
            fieldOperation.setText("");
            valueResult.set(0.0);
        });

        for (Button button : buttons) {
            button.prefHeightProperty().bind(heightProperty().divide(8));
            button.prefWidthProperty().bind(widthProperty().divide(5));
            button.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));
            button.setFont(font);
        }
    }

    private void addToActual(String text) {
        var actualText = fieldActual.getText();
        if (actualText.equals("0")) {
            fieldActual.setText(text);
        } else {
            fieldActual.setText(fieldActual.getText() + text);
        }
    }
}
