package ru.ystu.myystu.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import ru.ystu.myystu.AdaptersData.EventAdditionalData_Additional;
import ru.ystu.myystu.AdaptersData.EventAdditionalData_Documents;
import ru.ystu.myystu.AdaptersData.EventItemsData_Event;
import ru.ystu.myystu.AdaptersData.EventItemsData_Header;
import ru.ystu.myystu.AdaptersData.JobItemsData;
import ru.ystu.myystu.AdaptersData.NewsItemsData;
import ru.ystu.myystu.AdaptersData.NewsItemsData_DontAttach;
import ru.ystu.myystu.AdaptersData.NewsItemsPhotoData;
import ru.ystu.myystu.AdaptersData.ScheduleListItemData;
import ru.ystu.myystu.AdaptersData.StringData;
import ru.ystu.myystu.AdaptersData.UsersItemsData;
import ru.ystu.myystu.Database.Converters.ListJsonConverter;
import ru.ystu.myystu.Database.Converters.StringArraysConverter;
import ru.ystu.myystu.Database.Dao.CountersDao;
import ru.ystu.myystu.Database.Dao.EventFullDao;
import ru.ystu.myystu.Database.Dao.EventsItemsDao;
import ru.ystu.myystu.Database.Dao.JobItemsDao;
import ru.ystu.myystu.Database.Dao.NewsItemsDao;
import ru.ystu.myystu.Database.Dao.ScheduleItemDao;
import ru.ystu.myystu.Database.Dao.UserFullDao;
import ru.ystu.myystu.Database.Dao.UsersItemsDao;
import ru.ystu.myystu.Database.Data.CountersData;
import ru.ystu.myystu.Database.Data.EventFullData;
import ru.ystu.myystu.Database.Data.EventFullDivider;
import ru.ystu.myystu.Database.Data.ScheduleChangeBDData;
import ru.ystu.myystu.Database.Data.UserFullData;

@Database(entities = {
        JobItemsData.class,
        UsersItemsData.class,
        UserFullData.class,
        ScheduleListItemData.class,
        ScheduleChangeBDData.class,
        StringData.class,
        EventItemsData_Event.class,
        EventItemsData_Header.class,
        EventFullData.class,
        EventAdditionalData_Additional.class,
        EventAdditionalData_Documents.class,
        EventFullDivider.class,
        NewsItemsData.class,
        NewsItemsData_DontAttach.class,
        NewsItemsPhotoData.class,
        CountersData.class},
        version = 2, exportSchema = false)
@TypeConverters({
        StringArraysConverter.class,
        ListJsonConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract JobItemsDao jobItemsDao();
    public abstract UsersItemsDao usersItemsDao();
    public abstract UserFullDao userFullDao();
    public abstract ScheduleItemDao scheduleItemDao();
    public abstract EventsItemsDao eventsItemsDao();
    public abstract EventFullDao eventFullDao();
    public abstract NewsItemsDao newsItemsDao();
    public abstract CountersDao countersDao();

}
