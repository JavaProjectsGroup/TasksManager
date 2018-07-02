package com.projects.tasksmanager;

import com.projects.tasksmanager.usermodel.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.time.Duration;

public class CreateTask {
    @FXML
    private TextField createTaskText;

    public Task loadResult() {
        String createTaskString = createTaskText.getText().trim();
        Task newTask = new Task(createTaskString, Duration.ofSeconds(0), 0);
        return newTask;
    }
}
