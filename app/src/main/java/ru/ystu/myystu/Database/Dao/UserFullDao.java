package ru.ystu.myystu.Database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import ru.ystu.myystu.Database.Data.UserFullData;

@Dao
public interface UserFullDao {

    @Insert
    void insert(UserFullData... userFullData);

    @Query("DELETE FROM user_full WHERE id = :id")
    void delete(int id);

    @Query("SELECT * FROM user_full WHERE id = :id")
    UserFullData getUserFull (int id);

    @Query("SELECT EXISTS(SELECT id FROM user_full WHERE id = :id)")
    boolean isExists (int id);
}
