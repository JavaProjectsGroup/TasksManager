package com.projects.tasksmanager;

import com.projects.tasksmanager.usermodel.Task;

import java.time.Duration;

public class PassTask {
    private static PassTask instance = new PassTask();
    private Task task;

    public static PassTask getInstance() {
        return instance;
    }

    private PassTask() {
        task = new Task("Task",Duration.ofSeconds(0),0);
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}
