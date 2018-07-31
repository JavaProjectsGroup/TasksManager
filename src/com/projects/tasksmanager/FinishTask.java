package com.projects.tasksmanager;

import com.projects.tasksmanager.usermodel.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class FinishTask {
    @FXML
    private TextField finishCommentText;

    public void initialize() {
        UnaryOperator<TextFormatter.Change> newUnaryOperator = new UnaryOperator<TextFormatter.Change>() {
            @Override
            public TextFormatter.Change apply(TextFormatter.Change change) {
                String newInput = change.getControlNewText();
                if (newInput.matches("^([0-9a-zA-Z ]{1,30}|)$")) {
                    return change;
                } else {
                    return null;
                }
            }
        };
        finishCommentText.setTextFormatter(new TextFormatter<Object>(newUnaryOperator));
    }

    public String loadResult() {
        String comment = finishCommentText.getText().trim();
        return comment;
    }
}
