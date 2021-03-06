package com.projects.tasksmanager.usermodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.ArrayList;

public class UserData {
    private String username;
    private ObservableList<Task> tasks;

    public UserData() {
        this.username = "";
        this.tasks = FXCollections.observableArrayList();
    }

    public UserData(String username) {
        this.username = username;
        this.tasks = FXCollections.observableArrayList();
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

    public void saveUser() throws IOException {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(System.getProperty("user.dir")+"\\UsersDatabase\\"+this.username+".bin"))) {
            outputStream.writeObject(this.username);
            outputStream.writeObject(new ArrayList<Task>(this.tasks));
        }
    }

    @SuppressWarnings("unchecked")
    public void loadUser(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filePath))) {
            this.username = (String)inputStream.readObject();
            this.tasks = FXCollections.observableArrayList((ArrayList<Task>) inputStream.readObject());
        }
    }

    public void copyUser(UserData userData) throws CloneNotSupportedException {
        this.username = new String(userData.username);
        this.tasks.clear();
        for(Task task : userData.tasks) {
            Task copiedTask = (Task) task.clone();
            this.tasks.add(copiedTask);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof UserData) {
            UserData userData = (UserData) obj;
            return username.equals(userData.username) &&
                    tasks.equals(userData.tasks);
        }
        return false;
    }
}
