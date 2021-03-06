package com.dawids;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.HashMap;

public class Calculator extends GridPane {
    //Font for buttons and text fields
    private static final String FONT_NAME = "Noto Sans";
    //Array of operations that should be performed if another operation is chosen
    private static final ButtonOperation[] PERFORMING_OPERATIONS = {ButtonOperation.PERCENT,
                                                                    ButtonOperation.ADD,
                                                                    ButtonOperation.SUBTRACT,
                                                                    ButtonOperation.MULTIPLY,
                                                                    ButtonOperation.DIVIDE,
                                                                    ButtonOperation.POWER};

    //Field with input from user
    private final TextField fieldActual = new TextField();
    //Value of input text field
    private final SimpleObjectProperty<BigDecimal> valueActual = new SimpleObjectProperty<>(new BigDecimal("0.0",
                                                                                                           MathContext.DECIMAL32));
    private final SimpleObjectProperty<BigDecimal> valueResult = new SimpleObjectProperty<>(new BigDecimal("0.0",
                                                                                                           MathContext.DECIMAL32));
    private final SimpleObjectProperty<BigDecimal> valueMemory = new SimpleObjectProperty<>(new BigDecimal("0.0",
                                                                                                           MathContext.DECIMAL32));
    //This field contains answer for use with ANS button
    private final SimpleDoubleProperty answer = new SimpleDoubleProperty(0.0);
    //Property with ordinal value of current operation enum
    private final SimpleIntegerProperty currentOperation = new SimpleIntegerProperty(ButtonOperation.NONE.ordinal());

    public Calculator() {
        super();
        setAlignment(Pos.CENTER);
        setPadding(new Insets(10));
        setHgap(5);
        setVgap(5);

        createTextFields();
        createButtons();
    }

    private void createTextFields() {
        //Each filed is bound to window height and width so they are changing their size to fill all space
        final var font = Font.font(FONT_NAME);
        final var fontSize = new SimpleDoubleProperty(15);
        final var labelMemory = new Label("Memory:");
        final var fieldMemory = new TextField();
        final var labelResult = new Label("Result:");
        final var fieldResult = new TextField();
        final var fieldOperation = new TextField();

        fontSize.bind(Bindings.min(widthProperty().divide(20), heightProperty().divide(20)));

        GridPane.setValignment(labelMemory, VPos.CENTER);
        GridPane.setHalignment(labelMemory, HPos.CENTER);
        labelMemory.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.subtract(5).asString()));

        fieldMemory.prefWidthProperty().bind(widthProperty().divide(5).multiply(2));
        fieldMemory.setFont(font);
        fieldMemory.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));
        fieldMemory.setEditable(false);
        fieldMemory.setAlignment(Pos.CENTER_RIGHT);
        //Formatter shows big numbers in scientific notation
        final var formatterMemory = new TextFormatter<>(new FieldsStringConverter());
        formatterMemory.valueProperty().bind(valueMemory);
        fieldMemory.setTextFormatter(formatterMemory);

        GridPane.setValignment(labelResult, VPos.CENTER);
        GridPane.setHalignment(labelResult, HPos.CENTER);
        labelResult.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.subtract(5).asString()));

        fieldResult.prefWidthProperty().bind(widthProperty().divide(5).multiply(3));
        fieldResult.setFont(font);
        fieldResult.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));
        fieldResult.setEditable(false);
        fieldResult.setAlignment(Pos.CENTER_RIGHT);
        final var formatterResult = new TextFormatter<>(new FieldsStringConverter());
        formatterResult.valueProperty().bind(valueResult);
        fieldResult.setTextFormatter(formatterResult);

        fieldOperation.prefWidthProperty().bind(widthProperty().divide(5));
        fieldOperation.setFont(font);
        fieldOperation.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));
        fieldOperation.setAlignment(Pos.CENTER);
        fieldOperation.setEditable(false);
        //Each operation is stored in enum and has a symbol to display in this field
        currentOperation.addListener(((observable, oldValue, newValue) -> fieldOperation.setText(ButtonOperation.values()[(int) newValue]
                                                                                                         .getSymbol())));

        fieldActual.prefWidthProperty().bind(widthProperty().divide(5).multiply(4));
        fieldActual.setFont(font);
        fieldActual.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));
        fieldActual.setAlignment(Pos.CENTER_RIGHT);
        fieldActual.setEditable(false);
        fieldActual.setText("0");
        fieldActual.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                valueActual.set(new BigDecimal(newValue, MathContext.DECIMAL32));
            } else {
                valueActual.set(new BigDecimal("0.0", MathContext.DECIMAL32));
            }
        });

        add(labelMemory, 0, 0);
        add(fieldMemory, 1, 0, 4, 1);
        add(labelResult, 0, 1);
        add(fieldResult, 1, 1, 4, 1);
        add(fieldOperation, 0, 2);
        add(fieldActual, 1, 2, 4, 1);
    }

    private void createButtons() {
        //Each button has assigned operation and is bound to window dimensions so its size matches window size
        final var buttons = new HashMap<ButtonOperation, CalculatorButton>();
        for (ButtonOperation operation : ButtonOperation.values()) {
            var button = new CalculatorButton(operation.getSymbol(), operation);
            button.setOnAction(this::buttonAction);
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
        for (ButtonOperation operation : ButtonOperation.values()) {
            if (operation == ButtonOperation.ALL_CLEAR) {
                break;
            }
            add(buttons.get(operation), operation.ordinal() % 5, operation.ordinal() / 5 + 4);
        }
        add(buttons.get(ButtonOperation.CLEAR), 3, 3);
        add(buttons.get(ButtonOperation.BACKSPACE), 0, 3);
        add(buttons.get(ButtonOperation.ALL_CLEAR), 4, 3);
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
                case MEMORY_CLEAR:
                    valueMemory.set(new BigDecimal("0.0", MathContext.DECIMAL32));
                    break;

                //Put value from memory to actual
                case MEMORY_SHOW:
                    fieldActual.setText(valueMemory.get().stripTrailingZeros().toString());
                    if (ButtonOperation.values()[currentOperation.get()] == ButtonOperation.NONE) {
                        valueResult.set(new BigDecimal(valueActual.get().toString(), MathContext.DECIMAL32));
                    }
                    break;

                case MEMORY_ADD:
                    valueMemory.set(valueMemory.get().add(valueResult.get()));
                    break;

                case MEMORY_SUBTRACT:
                    valueMemory.set(valueMemory.get().subtract(valueResult.get()));
                    break;

                case PERCENT:
                case ADD:
                case SUBTRACT:
                case MULTIPLY:
                case DIVIDE:
                case POWER:
                    //No need to press equal sign after every operation
                    //Each operation in this list will be performed after pressing another one
                    if (Arrays.asList(PERFORMING_OPERATIONS)
                              .contains(ButtonOperation.values()[currentOperation.get()])) {
                        performAction();
                    }
                    currentOperation.set(operation.ordinal());
                    fieldActual.setText("0");
                    break;
                case ROOT:
                    valueResult.set(valueResult.get().sqrt(MathContext.DECIMAL32));
                    answer.set(valueResult.get().doubleValue());
                    fieldActual.setText("0");
                    currentOperation.set(ButtonOperation.NONE.ordinal());
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
                case BACKSPACE:
                    final var actualText = fieldActual.getText();
                    if (actualText.length() == 1) {
                        fieldActual.setText("0");
                    } else {
                        fieldActual.setText(fieldActual.getText(0, fieldActual.getText().length() - 1));
                    }
                    if (ButtonOperation.values()[currentOperation.get()] == ButtonOperation.NONE) {
                        valueResult.set(new BigDecimal(valueActual.get().toString(), MathContext.DECIMAL32));
                    }
                    break;
                case ALL_CLEAR:
                    fieldActual.setText("0");
                    currentOperation.set(ButtonOperation.NONE.ordinal());
                    valueResult.set(new BigDecimal("0.0", MathContext.DECIMAL32));
                    break;
                case ANSWER:
                    //Put answer from previous operation to actual field
                    if (answer.get() % 1 == 0) {
                        fieldActual.setText(String.format("%.0f", answer.get()));
                    } else {
                        fieldActual.setText(String.valueOf(answer.get()));
                    }
                    if (ButtonOperation.values()[currentOperation.get()] == ButtonOperation.NONE) {
                        valueResult.set(new BigDecimal(valueActual.get().toString(), MathContext.DECIMAL32));
                    }
                    break;
            }
        }
    }

    private void performAction() {
        switch (ButtonOperation.values()[currentOperation.get()]) {
            case ADD:
                valueResult.set(valueResult.get().add(valueActual.get()));
                break;
            case SUBTRACT:
                valueResult.set(valueResult.get().subtract(valueActual.get()));
                break;
            case MULTIPLY:
                valueResult.set(valueResult.get().multiply(valueActual.get()));
                break;
            case DIVIDE:
                if (!valueActual.get().toString().equals("0.0")) {
                    valueResult.set(valueResult.get().divide(valueResult.get(), MathContext.DECIMAL32));
                }
                break;
            case PERCENT:
                valueResult.set(valueResult.get()
                                           .multiply(valueActual.get())
                                           .divide(BigDecimal.valueOf(100.0), MathContext.DECIMAL32));
                break;
            case POWER:
                valueResult.set(new BigDecimal(Math.pow(valueResult.get().doubleValue(),
                                                        valueActual.get().doubleValue()),
                                               MathContext.DECIMAL32));
                break;
            default:
                break;
        }
        answer.set(valueResult.get().doubleValue());
        fieldActual.setText("0");
        currentOperation.set(ButtonOperation.NONE.ordinal());
    }

    private void addToActual(String text) {
        var actualText = fieldActual.getText();
        //Only one point is acceptable
        if (text.equals(".")) {
            if (!actualText.contains(".")) {
                fieldActual.setText(actualText + text);
            }
        } else if (actualText.equals("0")) {
            //At start 0 is changed to the first number
            fieldActual.setText(text);
        } else if (actualText.equals("-0")) {
            fieldActual.setText("-" + text);
        } else {
            fieldActual.setText(actualText + text);
        }
        //If there is no operation set every character is put to actualField as well as resultField
        if (ButtonOperation.values()[currentOperation.get()] == ButtonOperation.NONE) {
            valueResult.set(new BigDecimal(valueActual.get().toString(), MathContext.DECIMAL32));
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
        ANSWER("ANS"),
        EQUAL("="),
        ALL_CLEAR("AC"),
        BACKSPACE("<"),
        CLEAR("C"),
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
        //Each button has an operation assigned
        private final ButtonOperation operation;

        CalculatorButton(String text, ButtonOperation operation) {
            super(text);
            this.operation = operation;
        }

        ButtonOperation getOperation() {
            return operation;
        }
    }

    //This converter transform numbers to scientific notation when they are too big
    private static class FieldsStringConverter extends StringConverter<BigDecimal> {
        @Override
        public String toString(BigDecimal object) {
            return object.round(MathContext.DECIMAL32).toString();
        }

        @Override
        public BigDecimal fromString(String string) {
            return new BigDecimal(string);
        }
    }
}
