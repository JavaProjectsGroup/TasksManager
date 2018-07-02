package com.projects.tasksmanager;

import com.projects.tasksmanager.usermodel.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

import java.time.Duration;

public class EditTask {
    @FXML
    private TextField editTaskText;
    @FXML
    private Spinner<Integer> editTaskTimeMinutes;
    @FXML
    private Spinner<Integer> editTaskTimeSeconds;

    public void initialize() {
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
