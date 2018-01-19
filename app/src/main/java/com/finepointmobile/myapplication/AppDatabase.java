package com.finepointmobile.myapplication;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;



@Database(entities = { Task.class, Check.class}, version = 28)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
    public abstract CheckDao checkDao();
}
