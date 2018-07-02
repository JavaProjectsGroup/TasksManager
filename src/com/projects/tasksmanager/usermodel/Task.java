package com.projects.tasksmanager.usermodel;

import java.time.Duration;

public class Task {
    private String name;
    private Duration duration;
    private int dayOfWeek;
    private String comment;

    public Task(String name, Duration duration, int dayOfWeek) {
        this.name = name;
        this.duration = duration;
        this.dayOfWeek = dayOfWeek;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
