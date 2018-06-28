package com.projects.tasksmanager;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.util.Callback;
import javafx.util.Duration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Controller {
    @FXML
    private Label userLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private ListView<Task> tasksList;

    public void initialize() {
        //Creating temporary user for testing
        //TODO -> loading user from file
        UserData tempNewUser = new UserData("Test");
        Task task1 = new Task("Do job 1", java.time.Duration.ofSeconds(60),5);
        Task task2 = new Task("Do job 2", java.time.Duration.ofSeconds(500),3);
        Task task3 = new Task("Do job 3", java.time.Duration.ofSeconds(120),5);
        Task task4 = new Task("Do job 4", java.time.Duration.ofSeconds(1050),2);
        tempNewUser.addNewTask(task1);
        tempNewUser.addNewTask(task2);
        tempNewUser.addNewTask(task3);
        tempNewUser.addNewTask(task4);

        //Populating ListView with user's tasks and auto-selecting the first one
        tasksList.setItems(tempNewUser.getTasks());
        tasksList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tasksList.getSelectionModel().selectFirst();

        tasksList.setCellFactory(new Callback<ListView<Task>, ListCell<Task>>() {
            @Override
            public ListCell<Task> call(ListView<Task> param) {
                ListCell<Task> listCell = new ListCell<>() {
                    @Override
                    protected void updateItem(Task item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty) {
                            setText(null);
                        } else {
                            setText(item.getName());
                        }
                    }
                };
                return listCell;
            }
        });

        //Displaying current user's name
        userLabel.setText("Hello, "+tempNewUser.getUsername());

        //Formatting and displaying current time
        DateFormat timeFormat = new SimpleDateFormat("EEEE, HH:mm:ss");
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), event -> {
                                timeLabel.setText(timeFormat.format(System.currentTimeMillis()));
                        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
