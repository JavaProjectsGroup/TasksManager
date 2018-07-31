package com.projects.tasksmanager;

import com.projects.tasksmanager.usermodel.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.Duration;
import java.util.function.UnaryOperator;

public class EditTask {
    @FXML
    private GridPane gridPaneEditTask;
    @FXML
    private TextField editTaskText;
    @FXML
    private Spinner<Integer> editTaskTimeMinutes;
    @FXML
    private Spinner<Integer> editTaskTimeSeconds;

    private Label editCommentLabel = new Label("Task's comment");
    private TextField editCommentText = new TextField();

    private static final String MAX_VALUE_MIN = "1439";
    private static final String MAX_VALUE_SEC = "59";

    public void initialize() {
        editTaskTimeMinutes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,Integer.parseInt(MAX_VALUE_MIN),Integer.parseInt(MAX_VALUE_MIN)));
        editTaskTimeSeconds.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,Integer.parseInt(MAX_VALUE_SEC),Integer.parseInt(MAX_VALUE_SEC)));

        UnaryOperator<TextFormatter.Change> timeUnaryOperator = new UnaryOperator<TextFormatter.Change>() {
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
        UnaryOperator<TextFormatter.Change> commentUnaryOperator = (change) -> {
            String newInput = change.getControlNewText();
                if (newInput.matches("^([0-9a-zA-Z ]{1,30}|)$")) {
                    return change;
                } else {
                    return null;
                }
        };

        editTaskTimeMinutes.getEditor().setTextFormatter(new TextFormatter<Object>(timeUnaryOperator));
        editTaskTimeSeconds.getEditor().setTextFormatter(new TextFormatter<Object>(timeUnaryOperator));

        if(PassTask.getInstance().getTask().isFinished()) {
            editCommentText.setTextFormatter(new TextFormatter<Object>(commentUnaryOperator));

            GridPane.setColumnIndex(editCommentLabel, 0);
            GridPane.setRowIndex(editCommentLabel, 2);
            GridPane.setColumnIndex(editCommentText, 1);
            GridPane.setRowIndex(editCommentText, 2);

            editCommentText.setText(PassTask.getInstance().getTask().getComment());

            gridPaneEditTask.getChildren().add(editCommentLabel);
            gridPaneEditTask.getChildren().add(editCommentText);
        }

        Long m = PassTask.getInstance().getTask().getDuration().getSeconds()/60;
        Long s = PassTask.getInstance().getTask().getDuration().getSeconds()%60;
        editTaskText.setText(PassTask.getInstance().getTask().getName());
        editTaskTimeMinutes.getValueFactory().setValue(m.intValue());
        editTaskTimeSeconds.getValueFactory().setValue(s.intValue());
    }

    public Task loadResult() {
        String editComment = editCommentText.getText().trim();
        String editTaskString = editTaskText.getText().trim();
        long editTaskTime = editTaskTimeMinutes.getValue()*60 + editTaskTimeSeconds.getValue();
        Task editedTask = new Task(editTaskString, Duration.ofSeconds(editTaskTime), 0);

        if(PassTask.getInstance().getTask().isFinished()) {
            editedTask.setFinishedOn(PassTask.getInstance().getTask().getFinishedOn());
            editedTask.setComment(editComment);
            editedTask.setFinished(true);
        }

        return editedTask;
    }
}
