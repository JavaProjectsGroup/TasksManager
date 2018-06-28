package com.projects.tasksmanager;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Controller {
    @FXML
    private Label timeLabel;

    public void initialize() {
        DateFormat timeFormat = new SimpleDateFormat("EEEE, HH:mm:ss");
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), event -> {
                                timeLabel.setText(timeFormat.format(System.currentTimeMillis()));
                        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
