package com.finepointmobile.myapplication;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface CheckDao {

    @Query("SELECT * FROM checks")
    List<Check> getAll();

    @Query("SELECT * FROM checks WHERE task_id = :id")
    List<Check> getChecksById(int id);

    @Query("UPDATE checks SET complete = :i_c WHERE task_id = :id AND check_text = :c_t")
    void updateCheck(int id, String c_t, int i_c);

    @Query("UPDATE checks SET task_id = task_id - 1 WHERE task_id > :id")
    void reindexChecks(int id);

    @Query("DELETE FROM checks WHERE task_id = :id")
    void deleteById(int id);

    @Insert
    void insertAll(Check... checks);
}