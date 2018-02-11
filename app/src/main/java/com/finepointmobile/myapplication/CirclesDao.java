package com.finepointmobile.myapplication;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Роман on 20.01.2018.
 */
@Dao
public interface CirclesDao {
    @Query("SELECT * FROM circles")
    List<Circles> getAll();

    @Query("SELECT * FROM circles WHERE id = :id")
    List<Circles> getCirclesById(int id);

    @Query("SELECT * FROM circles WHERE package_name = :name")
    List<Circles> getCirclesByName(String name);

    @Query("UPDATE circles SET time = 0")
    void reWriteAll();

    @Query("UPDATE circles SET time = time + :add WHERE package_name = :name")
    void updateTime(String name, long add);

    @Query("DELETE FROM circles WHERE id = :id")
    void deleteById(int id);

    @Delete
    void deleteCircles(Circles circles);

    @Insert
    void insertAll(Circles... circles);
}
