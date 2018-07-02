package com.projects.tasksmanager.usermodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserData {
    private String username;
    private ObservableList<Task> tasks = FXCollections.observableArrayList();

    public UserData(String username) {
        this.username = username;
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

    public void addTaskToList(Task newTask) {
        tasks.add(newTask);
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void removeTaskFromList(Task selectedTask) {
        tasks.remove(selectedTask);
    }

    public void editTaskOnList(Task selectedTask, Task editedTask) {
        tasks.set(tasks.indexOf(selectedTask),editedTask);
    }
}
