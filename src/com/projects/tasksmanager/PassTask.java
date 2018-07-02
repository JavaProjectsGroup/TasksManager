package com.projects.tasksmanager;

import java.time.Duration;

public class PassTask {
    private static PassTask instance = new PassTask();
    private String taskName;
    private Duration taskTime;

    public static PassTask getInstance() {
        return instance;
    }

    private PassTask() {
        taskName = "Name";
        taskTime = Duration.ofSeconds(0);
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Duration getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(Duration taskTime) {
        this.taskTime = taskTime;
    }
}
