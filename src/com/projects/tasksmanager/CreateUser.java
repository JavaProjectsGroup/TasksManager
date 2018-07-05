package com.projects.tasksmanager;

import com.projects.tasksmanager.usermodel.UserData;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class CreateUser {
    @FXML
    private TextField usernameText;

    public void initialize() {
        UnaryOperator<TextFormatter.Change> newUnaryOperator = new UnaryOperator<TextFormatter.Change>() {
            @Override
            public TextFormatter.Change apply(TextFormatter.Change change) {
                String newInput = change.getControlNewText();
                if (newInput.matches("^([0-9a-zA-Z]{1,10}|)$")) {
                    return change;
                } else {
                    return null;
                }
            }
        };
        usernameText.setTextFormatter(new TextFormatter<Object>(newUnaryOperator));
    }

    public UserData loadResult() {
        UserData newUser = new UserData(usernameText.getText().trim());
        return newUser;
    }
}
