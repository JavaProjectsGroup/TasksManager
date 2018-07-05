package com.projects.tasksmanager;

import com.projects.tasksmanager.usermodel.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.time.Duration;
import java.util.function.UnaryOperator;

public class EditTask {
    @FXML
    private TextField editTaskText;
    @FXML
    private Spinner<Integer> editTaskTimeMinutes;
    @FXML
    private Spinner<Integer> editTaskTimeSeconds;
    private static final String MAX_VALUE_MIN = "1439";
    private static final String MAX_VALUE_SEC = "59";

    public void initialize() {
        editTaskTimeMinutes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,Integer.parseInt(MAX_VALUE_MIN),Integer.parseInt(MAX_VALUE_MIN)));
        editTaskTimeSeconds.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,Integer.parseInt(MAX_VALUE_SEC),Integer.parseInt(MAX_VALUE_SEC)));

        UnaryOperator<TextFormatter.Change> newUnaryOperator = new UnaryOperator<TextFormatter.Change>() {
            @Override
            public TextFormatter.Change apply(TextFormatter.Change change) {
                String newInput = change.getControlNewText();
                if(newInput.isEmpty()) {
                    change.setText("0");
                    return change;
                }
                else if (newInput.matches("^([0-9]{1,4})$")) {
                    return change;
                } else {
                    return null;
                }
            }
        };
        editTaskTimeMinutes.getEditor().setTextFormatter(new TextFormatter<Object>(newUnaryOperator));
        editTaskTimeSeconds.getEditor().setTextFormatter(new TextFormatter<Object>(newUnaryOperator));

        Long m = PassTask.getInstance().getTaskTime().getSeconds()/60;
        Long s = PassTask.getInstance().getTaskTime().getSeconds()%60;
        editTaskText.setText(PassTask.getInstance().getTaskName());
        editTaskTimeMinutes.getValueFactory().setValue(m.intValue());
        editTaskTimeSeconds.getValueFactory().setValue(s.intValue());
    }

    public Task loadResult() {
        String editTaskString = editTaskText.getText().trim();
        long editTaskTime = editTaskTimeMinutes.getValue()*60 + editTaskTimeSeconds.getValue();
        Task editedTask = new Task(editTaskString, Duration.ofSeconds(editTaskTime), 0);
        return editedTask;
    }
}
