package com.example.s2o_mobile.data.source.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.s2o_mobile.data.model.User;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Query("SELECT * FROM user LIMIT 1")
    User getUser();

    @Query("DELETE FROM user")
    void clearUser();
}
