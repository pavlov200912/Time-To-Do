package com.finepointmobile.myapplication;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;



@Entity(tableName = "checks")
public class Check{

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "check_text")
    String checkText;

    @ColumnInfo(name = "complete")
    int isComplete;

    @ColumnInfo(name = "task_id")
    String taskId;

    public Check(String checkText, int isComplete, String taskId) {
        this.checkText = checkText;
        this.isComplete = isComplete;
        this.taskId = taskId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCheckText() {
        return checkText;
    }

    public void setCheckText(String checkText) {
        this.checkText = checkText;
    }

    public int getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(int isComplete) {
        this.isComplete = isComplete;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
