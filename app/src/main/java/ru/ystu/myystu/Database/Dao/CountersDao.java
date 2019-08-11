package ru.ystu.myystu.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import ru.ystu.myystu.Database.Data.CountersData;

@Dao
public interface CountersDao {

    @Insert
    void insertCounter (CountersData... countersData);

    @Query("UPDATE counters SET count = :count WHERE type = :type")
    void setCount (String type, int count);

    @Query("DELETE FROM counters WHERE type = :type")
    void deleteCounter (String type);

    @Query("SELECT EXISTS(SELECT type FROM counters WHERE type = :type )")
    boolean isExistsCounter (String type);

    @Query("SELECT count FROM counters WHERE type = :type")
    int getCountCounter (String type);

    @Query("SELECT COUNT(*) FROM counters")
    int getCountCounters();
}
