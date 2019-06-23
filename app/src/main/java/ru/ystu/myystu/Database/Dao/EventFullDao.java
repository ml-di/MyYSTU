package ru.ystu.myystu.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import ru.ystu.myystu.AdaptersData.EventAdditionalData_Additional;
import ru.ystu.myystu.AdaptersData.EventAdditionalData_Documents;
import ru.ystu.myystu.Database.Data.EventFullData;
import ru.ystu.myystu.Database.Data.EventFullDivider;

@Dao
public interface EventFullDao {

    @Insert
    void insertGeneral (EventFullData... eventFullData);

    @Insert
    void insertAdditional (EventAdditionalData_Additional... eventAdditionalData_additionals);

    @Insert
    void insertDocuments (EventAdditionalData_Documents... eventAdditionalData_documents);

    @Insert
    void insertDividers (EventFullDivider... eventFullDividers);

    @Query("DELETE FROM event_full WHERE uid = :uid")
    void deleteGeneral (int uid);

    @Query("DELETE FROM event_full_additional WHERE uid = :uid")
    void deleteAllAdditional (int uid);

    @Query("DELETE FROM event_full_doc WHERE uid = :uid")
    void deleteAllDocuments (int uid);

    @Query("DELETE FROM event_full_divider WHERE uid = :uid")
    void deleteAllDividers (int uid);

    @Query("SELECT EXISTS(SELECT id FROM event_full WHERE uid = :uid)")
    boolean isExistsGeneral (int uid);

    @Query("SELECT EXISTS(SELECT id FROM event_full_additional WHERE uid = :uid AND id = :id)")
    boolean isExistsAdditional (int uid, int id);

    @Query("SELECT EXISTS(SELECT id FROM event_full_doc WHERE uid = :uid AND id = :id)")
    boolean isExistsDocuments (int uid, int id);

    @Query("SELECT EXISTS(SELECT id FROM event_full_divider WHERE uid = :uid AND id = :id)")
    boolean isExistsDividers (int uid, int id);

    @Query("SELECT COUNT(*) FROM event_full_additional WHERE uid = :uid")
    int getCountAdditional (int uid);

    @Query("SELECT COUNT(*) FROM event_full_doc WHERE uid = :uid")
    int getCountDocuments (int uid);

    @Query("SELECT COUNT(*) FROM event_full_divider WHERE uid = :uid")
    int getCountDividers (int uid);

    @Query("SELECT * FROM event_full WHERE uid = :uid")
    EventFullData getGeneral(int uid);

    @Query("SELECT * FROM event_full_additional WHERE uid = :uid AND id = :id")
    EventAdditionalData_Additional getAdditionals (int uid, int id);

    @Query("SELECT * FROM event_full_doc WHERE uid = :uid AND id = :id")
    EventAdditionalData_Documents getDocuments (int uid, int id);

    @Query("SELECT * FROM event_full_divider WHERE uid = :uid AND id = :id")
    EventFullDivider getDividers (int uid, int id);

}
