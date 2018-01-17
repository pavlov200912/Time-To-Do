package com.finepointmobile.myapplication;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM Task")
    List<Task> getAll();

    @Query("SELECT * FROM Task WHERE task_id = :id")
    List<Task> getTaskById(int id);

    @Query("UPDATE Task SET short_text = :s_t, long_text = :l_t, check_text = :c_t , date_text = :d_t WHERE task_id = :id")
    void updateTask(int id, String s_t, String l_t, String c_t, String d_t);

    @Query("UPDATE Task SET task_id = task_id - 1 WHERE task_id > :id")
    void reindexTasks(int id);

    @Query("DELETE FROM Task WHERE task_id = :id")
    void deleteTaskById(int id);

    @Delete
    void deleteTask(Task task);

    @Insert
    void insertAll(Task... tasks);
}
