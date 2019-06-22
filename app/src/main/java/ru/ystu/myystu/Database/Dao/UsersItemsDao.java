package ru.ystu.myystu.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import ru.ystu.myystu.AdaptersData.UsersItemsData;

@Dao
public interface UsersItemsDao {

    @Insert
    void insert(UsersItemsData... usersItemsData);

    @Query("DELETE FROM users_items")
    void deleteAll();

    @Query("SELECT * FROM users_items")
    List<UsersItemsData> getAllUsersItems();

    @Query("SELECT COUNT(*) FROM users_items")
    int getCount();

}
