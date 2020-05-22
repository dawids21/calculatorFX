package com.dawids;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.StringConverter;

import java.util.Arrays;
import java.util.HashMap;

public class Calculator extends GridPane {
    private static final String FONT_NAME = "Noto Sans";
    private static final ButtonOperation[] PERFORMING_OPERATIONS = {ButtonOperation.PERCENT,
                                                                    ButtonOperation.ADD,
                                                                    ButtonOperation.SUBTRACT,
                                                                    ButtonOperation.MULTIPLY,
                                                                    ButtonOperation.DIVIDE,
                                                                    ButtonOperation.POWER};

    private final TextField fieldActual = new TextField();
    private final SimpleDoubleProperty valueActual = new SimpleDoubleProperty(0.0);
    private final SimpleDoubleProperty valueResult = new SimpleDoubleProperty(0.0);
    private final SimpleDoubleProperty valueMemory = new SimpleDoubleProperty(0.0);
    private final SimpleIntegerProperty currentOperation = new SimpleIntegerProperty(ButtonOperation.NONE.ordinal());

    public Calculator() {
        super();
        setAlignment(Pos.CENTER);
        setPadding(new Insets(10));
        setHgap(5);
        setVgap(5);
        final var font = Font.font(FONT_NAME);
        final var fontSize = new SimpleDoubleProperty(15);
        fontSize.bind(Bindings.min(widthProperty().divide(20), heightProperty().divide(20)));

        var fieldMemory = new TextField();
        fieldMemory.prefWidthProperty().bind(widthProperty().divide(5).multiply(2));
        fieldMemory.setFont(font);
        fieldMemory.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));
        fieldMemory.setEditable(false);
        fieldMemory.setAlignment(Pos.CENTER_RIGHT);
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

        var fieldResult = new TextField();
        fieldResult.prefWidthProperty().bind(widthProperty().divide(5).multiply(3));
        fieldResult.setFont(font);
        fieldResult.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));
        fieldResult.setEditable(false);
        fieldResult.setAlignment(Pos.CENTER_RIGHT);
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
        var formatterResult = new TextFormatter<>(converterResult);
        formatterResult.valueProperty().bind(valueResult);
        fieldResult.setTextFormatter(formatterResult);

        final var fieldOperation = new TextField();
        fieldOperation.prefWidthProperty().bind(widthProperty().divide(5));
        fieldOperation.setFont(font);
        fieldOperation.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));
        fieldOperation.setAlignment(Pos.CENTER);
        fieldOperation.setEditable(false);
        currentOperation.addListener(((observable, oldValue, newValue) -> {
            fieldOperation.setText(ButtonOperation.values()[(int) newValue].getSymbol());
        }));

        fieldActual.prefWidthProperty().bind(widthProperty().divide(5).multiply(4));
        fieldActual.setFont(font);
        fieldActual.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));
        fieldActual.setAlignment(Pos.CENTER_RIGHT);
        fieldActual.setEditable(false);
        fieldActual.setText("0");
        fieldActual.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                valueActual.set(Double.parseDouble(newValue));
            } else {
                valueActual.set(0.0);
            }
        });

        add(fieldMemory, 0, 0, 2, 1);
        add(fieldResult, 2, 0, 3, 1);
        add(fieldOperation, 0, 1);
        add(fieldActual, 1, 1, 4, 1);

        final var buttons = createButtons();
        for (ButtonOperation operation : ButtonOperation.values()) {
            if (operation == ButtonOperation.ALL_CLEAR) {
                break;
            }
            add(buttons.get(operation), operation.ordinal() % 5, operation.ordinal() / 5 + 3);
        }
        add(buttons.get(ButtonOperation.ALL_CLEAR), 4, 2);
    }

    private HashMap<ButtonOperation, CalculatorButton> createButtons() {
        //TODO add backspace
        //todo consider add ANS button
        final var buttons = new HashMap<ButtonOperation, CalculatorButton>();
        for (ButtonOperation operation : ButtonOperation.values()) {
            var button = new CalculatorButton(operation.getSymbol(), operation);
            button.setOnAction(event -> buttonAction(event));
            buttons.put(operation, button);
        }

        final var font = Font.font(FONT_NAME);
        final var fontSize = new SimpleDoubleProperty();
        fontSize.bind(Bindings.min(widthProperty().divide(20), heightProperty().divide(20)));
        for (CalculatorButton button : buttons.values()) {
            button.prefHeightProperty().bind(heightProperty().divide(8));
            button.prefWidthProperty().bind(widthProperty().divide(5));
            button.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));
            button.setFont(font);
        }
        return buttons;
    }

    private void buttonAction(ActionEvent event) {
        if (event.getSource() instanceof CalculatorButton) {
            ButtonOperation operation = ((CalculatorButton) event.getSource()).getOperation();
            switch (operation) {
                case ZERO:
                case ONE:
                case TWO:
                case THREE:
                case FOUR:
                case FIVE:
                case SIX:
                case SEVEN:
                case EIGHT:
                case NINE:
                case POINT:
                    addToActual(operation.getSymbol());
                    break;
                //todo memory add to add from result not actual
                case MEMORY_CLEAR:
                    valueMemory.set(0.0);
                    break;

                case MEMORY_SHOW:
                    if (valueMemory.get() % 1 == 0) {
                        fieldActual.setText(String.valueOf((int) valueMemory.get()));
                    } else {
                        fieldActual.setText(String.valueOf(valueMemory.get()));
                    }
                    break;

                case MEMORY_ADD:
                    valueMemory.set(valueMemory.get() + valueResult.get());
                    break;

                case MEMORY_SUBTRACT:
                    valueMemory.set(valueMemory.get() - valueResult.get());
                    break;

                case PERCENT:
                case ADD:
                case SUBTRACT:
                case MULTIPLY:
                case DIVIDE:
                case POWER:
                    if (Arrays.asList(PERFORMING_OPERATIONS)
                              .contains(ButtonOperation.values()[currentOperation.get()])) {
                        performAction();
                    }
                    currentOperation.set(operation.ordinal());
                    fieldActual.setText("0");
                    break;
                case ROOT:
                    valueResult.set(Math.sqrt(valueResult.get()));
                    fieldActual.setText("0");
                    break;
                case NEGATE:
                    negateActual();
                    break;
                case CLEAR:
                    fieldActual.setText("0");
                    currentOperation.set(ButtonOperation.NONE.ordinal());
                    break;
                case EQUAL:
                    performAction();
                    break;
                case ALL_CLEAR:
                    fieldActual.setText("0");
                    currentOperation.set(ButtonOperation.NONE.ordinal());
                    valueResult.set(0.0);
                    break;
            }
        }
    }

    private void performAction() {
        switch (ButtonOperation.values()[currentOperation.get()]) {
            case ADD:
                valueResult.set(valueResult.get() + valueActual.get());
                break;
            case SUBTRACT:
                valueResult.set(valueResult.get() - valueActual.get());
                break;
            case MULTIPLY:
                valueResult.set(valueResult.get() * valueActual.get());
                break;
            case DIVIDE:
                if (valueActual.get() != 0) {
                    valueResult.set(valueResult.get() / valueActual.get());
                }
                break;
            case PERCENT:
                valueResult.set(valueResult.get() * (valueActual.get() / 100));
                break;
            case POWER:
                valueResult.set(Math.pow(valueResult.get(), valueActual.get()));
                break;
            default:
                break;
        }
        fieldActual.setText("0");
        currentOperation.set(ButtonOperation.NONE.ordinal());
    }

    private void addToActual(String text) {
        var actualText = fieldActual.getText();
        if (text.equals(".")) {
            if (!actualText.contains(".")) {
                fieldActual.setText(actualText + text);
            }
        } else if (actualText.equals("0")) {
            fieldActual.setText(text);
        } else if (actualText.equals("-0")) {
            fieldActual.setText("-" + text);
        } else {
            fieldActual.setText(actualText + text);
        }
        if (ButtonOperation.values()[currentOperation.get()] == ButtonOperation.NONE) {
            valueResult.set(valueActual.get());
        }
    }

    private void negateActual() {
        String actualText = fieldActual.getText();
        if (actualText.charAt(0) == '-') {
            fieldActual.setText(actualText.substring(1));
        } else {
            fieldActual.setText("-" + actualText);
        }
    }

    enum ButtonOperation {
        MEMORY_CLEAR("MC"),
        MEMORY_SHOW("MS"),
        MEMORY_ADD("M+"),
        MEMORY_SUBTRACT("M-"),
        PERCENT("%"),
        SEVEN("7"),
        EIGHT("8"),
        NINE("9"),
        ADD("+"),
        SUBTRACT("-"),
        FOUR("4"),
        FIVE("5"),
        SIX("6"),
        MULTIPLY("*"),
        DIVIDE("/"),
        ONE("1"),
        TWO("2"),
        THREE("3"),
        POWER("^"),
        ROOT("√"),
        NEGATE("+/-"),
        ZERO("0"),
        POINT("."),
        CLEAR("C"),
        EQUAL("="),
        ALL_CLEAR("AC"),
        NONE("");

        private final String symbol;

        ButtonOperation(String symbol) {
            this.symbol = symbol;
        }

        private String getSymbol() {
            return symbol;
        }
    }

    private static class CalculatorButton extends Button {
        private final ButtonOperation operation;

        CalculatorButton(String text, ButtonOperation operation) {
            super(text);
            this.operation = operation;
        }

        ButtonOperation getOperation() {
            return operation;
        }
    }
}
