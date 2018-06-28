package com.projects.tasksmanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserData {
    private String username;
    private ObservableList<Task> tasks = FXCollections.observableArrayList();

    public UserData(String username) {
        this.username = username;
    }

    public void addNewTask(Task newTask) {
        tasks.add(newTask);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ObservableList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ObservableList<Task> tasks) {
        this.tasks = tasks;
    }
}
