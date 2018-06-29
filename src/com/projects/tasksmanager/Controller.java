package com.projects.tasksmanager;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Callback;
import javafx.util.Duration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.function.Predicate;

public class Controller {
    @FXML
    private BorderPane mainPane;
    @FXML
    private Label userLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label tempLabel;
    @FXML
    private ListView<Task> tasksList;

    private VBox containerBox = new VBox();
    private HBox buttonsBox = new HBox();
    private Label durationLabel = new Label("mm:ss");
    private Button contButton = new Button("START/PAUSE");
    private Button stopButton = new Button("STOP");

    private FilteredList<Task> filteredList;
    private Predicate<Task> wantDailyTasks;

    public void initialize() {
        //Creating temporary user for testing
        //TODO -> move code to loading user from file
        UserData tempNewUser = new UserData("Test");
        Task task1 = new Task("Do job 1", java.time.Duration.ofSeconds(0),6);
        Task task2 = new Task("Do job 2", java.time.Duration.ofSeconds(500),2);
        Task task3 = new Task("Do job 3", java.time.Duration.ofSeconds(120),6);
        Task task4 = new Task("Do job 4", java.time.Duration.ofSeconds(10500),4);
        tempNewUser.addNewTask(task1);
        tempNewUser.addNewTask(task2);
        tempNewUser.addNewTask(task3);
        tempNewUser.addNewTask(task4);

        //Modifying look of Labels and Buttons
        stopButton.setFont(new Font("Arial bold",20));
        contButton.setFont(new Font("Arial bold",20));
        stopButton.setPrefWidth(180);
        contButton.setPrefWidth(180);
        durationLabel.setFont(new Font("Arial bold",60));
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setSpacing(30);
        containerBox.setAlignment(Pos.CENTER);

        //Creating a VBox container with displayed data
        containerBox.getChildren().add(durationLabel);
        buttonsBox.getChildren().addAll(contButton,stopButton);
        containerBox.getChildren().add(buttonsBox);

        //Hiding the temporary label after loading an user
        tempLabel.setText("Select the task you want to perform");

        //Displaying current user's name
        userLabel.setText("Hello, "+tempNewUser.getUsername());

        //Filtering list of tasks to show only the ones from current day of the week
        wantDailyTasks = new Predicate<Task>() {
            @Override
            public boolean test(Task task) {
                return (task.getDayOfWeek() == Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
            }
        };
        filteredList = new FilteredList<>(tempNewUser.getTasks(),wantDailyTasks);

        //Populating ListView with user's tasks
        tasksList.setItems(filteredList);
        tasksList.setDisable(false);
        tasksList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        //tasksList.getSelectionModel().selectFirst();

        //Modifying displayed text in a cell
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
                            setText(item.getName());//Display task's name
                        }
                    }
                };
                return listCell;
            }
        });

        //Creating a Timeline for changing tasks durations every second
        Timeline durationTimeline = new Timeline(new KeyFrame(Duration.millis(1000), event -> {
            Task task = tasksList.getSelectionModel().getSelectedItem();
            task.setDuration(task.getDuration().plusSeconds(1));
            refreshDurationLabel(task);
            //TODO -> Stop at 24 hours
        }));
        durationTimeline.setCycleCount(Animation.INDEFINITE);

        //Adding setOnAction events to Buttons
        contButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(durationTimeline.getStatus().equals(Animation.Status.STOPPED) || durationTimeline.getStatus().equals(Animation.Status.PAUSED)) {
                    durationTimeline.play();
                } else {
                    durationTimeline.pause();
                }
            }
        });
        stopButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                durationTimeline.stop();
            }
        });

        //Refreshing the duration in Center property of BorderPane after clicking on another task on ListView
        tasksList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Task>() {
            @Override
            public void changed(ObservableValue<? extends Task> observable, Task oldValue, Task newValue) {
                refreshDurationLabel(newValue);
                if(oldValue == null) {
                    mainPane.setCenter(containerBox);
                }
                durationTimeline.pause();
            }
        });

        //Formatting and displaying current time
        DateFormat timeFormat = new SimpleDateFormat("EEEE, HH:mm:ss");
        Timeline clockTimeline = new Timeline(new KeyFrame(Duration.millis(500), event -> {
                                timeLabel.setText(timeFormat.format(System.currentTimeMillis()));
                        }));
        clockTimeline.setCycleCount(Animation.INDEFINITE);
        clockTimeline.play();
    }

    private void refreshDurationLabel(Task selectedTask) {
        long sec = selectedTask.getDuration().getSeconds();
        durationLabel.setText(String.format("%02d:%02d",sec/60,sec%60));
    }
}
