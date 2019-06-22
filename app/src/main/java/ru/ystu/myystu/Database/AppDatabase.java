package ru.ystu.myystu.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import ru.ystu.myystu.AdaptersData.JobItemsData;
import ru.ystu.myystu.AdaptersData.ScheduleListItemData;
import ru.ystu.myystu.AdaptersData.UsersItemsData;
import ru.ystu.myystu.Database.Dao.JobItemsDao;
import ru.ystu.myystu.Database.Dao.ScheduleItemDao;
import ru.ystu.myystu.Database.Dao.UserFullDao;
import ru.ystu.myystu.Database.Dao.UsersItemsDao;
import ru.ystu.myystu.Database.Data.ScheduleChangeBDData;
import ru.ystu.myystu.Database.Data.UserFullData;

@Database(entities = {
        JobItemsData.class,
        UsersItemsData.class,
        UserFullData.class,
        ScheduleListItemData.class,
        ScheduleChangeBDData.class},
        version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract JobItemsDao jobItemsDao();
    public abstract UsersItemsDao usersItemsDao();
    public abstract UserFullDao userFullDao();
    public abstract ScheduleItemDao scheduleItemDao();

}
