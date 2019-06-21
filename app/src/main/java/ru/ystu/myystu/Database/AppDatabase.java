package ru.ystu.myystu.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import ru.ystu.myystu.AdaptersData.EventItemsData_Event;
import ru.ystu.myystu.AdaptersData.JobItemsData;
import ru.ystu.myystu.Database.Dao.JobItemsDao;

@Database(entities = {JobItemsData.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract JobItemsDao jobItemsDao();

}
