package com.finepointmobile.myapplication;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;



@Entity(tableName = "circles")
public class Circles{

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "package_name")
    String packageName;

    @ColumnInfo(name = "time")
    long time;

    @ColumnInfo(name = "limit_time")
    long limitTime;


    public Circles(String packageName, long time, long limitTime) {
        this.packageName = packageName;
        this.time = time;
        this.limitTime = limitTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getLimitTime() {
        return limitTime;
    }

    public void setLimitTime(long limitTime) {
        this.limitTime = limitTime;
    }

}
