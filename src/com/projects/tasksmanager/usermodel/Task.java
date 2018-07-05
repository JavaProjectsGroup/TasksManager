package com.projects.tasksmanager.usermodel;

import java.io.Serializable;
import java.time.Duration;

public class Task implements Serializable, Cloneable {
    private String name;
    private Duration duration;
    private int dayOfWeek;
    private String comment = "";

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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Task) {
            Task task = (Task) obj;
            return name.equals(task.name) &&
                    duration.equals(task.duration) &&
                    (dayOfWeek == task.dayOfWeek) &&
                    comment.equals(task.comment);
        }
        return false;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
