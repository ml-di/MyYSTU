package ru.ystu.myystu.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;
import ru.ystu.myystu.AdaptersData.EventItemsData_Event;
import ru.ystu.myystu.AdaptersData.EventItemsData_Header;
import ru.ystu.myystu.AdaptersData.StringData;

@Dao
public interface EventsItemsDao {

    @Insert
    void insertDividers(StringData... stringData);

    @Insert
    void insertEventItems(EventItemsData_Event... eventItemsData_events);

    @Insert
    void insertEventHeader(EventItemsData_Header... eventItemsData_headers);

    @Query("DELETE FROM divider")
    void deleteAllDividers();

    @Query("DELETE FROM event_items")
    void deleteAllEventItems();

    @Query("DELETE FROM event_header")
    void deleteEventHeader();

    @Query("SELECT COUNT(*) FROM divider")
    int getCountDividers();

    @Query("SELECT COUNT(*) FROM event_items")
    int getCountEventItems();

    @Query("SELECT COUNT(*) FROM event_header")
    int getCountEventHeader();

    @Query("SELECT * FROM divider WHERE id = :id")
    StringData getDividers (int id);

    @Query("SELECT * FROM event_items WHERE id = :id")
    EventItemsData_Event getEvents (int id);

    @Query("SELECT * FROM event_header WHERE id = :id")
    EventItemsData_Header getEventHeader (int id);

    @Query("SELECT * FROM event_items")
    List<EventItemsData_Event> getAllEvent();

    @Query("SELECT EXISTS(SELECT id FROM divider WHERE id = :id)")
    boolean isExistsDivider (int id);

    @Query("SELECT EXISTS(SELECT id FROM event_items WHERE id = :id)")
    boolean isExistsEventItems (int id);

    @Query("SELECT EXISTS(SELECT id FROM event_header WHERE id = :id)")
    boolean isExistsEventHeader (int id);

    @Query("SELECT EXISTS (SELECT link FROM event_items WHERE link = :link)")
    boolean isExistsEventByLink (String link);
}
