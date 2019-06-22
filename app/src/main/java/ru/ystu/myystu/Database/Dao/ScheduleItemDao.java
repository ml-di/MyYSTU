package ru.ystu.myystu.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import ru.ystu.myystu.AdaptersData.ScheduleListItemData;
import ru.ystu.myystu.Database.Data.ScheduleChangeBDData;

@Dao
public interface ScheduleItemDao {

    @Insert
    void insertList (ScheduleListItemData... scheduleListItemData);

    @Insert
    void insertChange (ScheduleChangeBDData... scheduleChangeData);

    @Query("DELETE FROM schedule_list WHERE id = :id")
    void deleteList(int id);

    @Query("DELETE FROM schedule_change WHERE id = :id")
    void deleteChange(int id);

    @Query("SELECT * FROM schedule_list WHERE id = :id")
    List<ScheduleListItemData> getScheduleList(int id);

    @Query("SELECT * FROM schedule_change WHERE id = :id")
    List<ScheduleChangeBDData> getScheduleChange(int id);

    @Query("SELECT COUNT(*) FROM schedule_list WHERE id = :id")
    int getCountScheduleList(int id);

    @Query("SELECT COUNT(*) FROM schedule_change WHERE id = :id")
    int getCountScheduleChange(int id);
}
