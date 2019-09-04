package ru.ystu.myystu.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import ru.ystu.myystu.AdaptersData.StoItemsData_Doc;
import ru.ystu.myystu.AdaptersData.StoItemsData_Subtitle;
import ru.ystu.myystu.AdaptersData.StoItemsData_Title;

@Dao
public interface StoItemsDao {

    @Insert
    void insertTitles (StoItemsData_Title... stoItemsData_title);

    @Insert
    void insertSubTitles (StoItemsData_Subtitle... stoItemsData_subtitles);

    @Insert
    void insertDoc (StoItemsData_Doc... stoItemsData_docs);

    @Query("DELETE FROM sto_title")
    void deleteAllTitles();

    @Query("DELETE FROM sto_subtitle")
    void deleteAllSubTitles();

    @Query("DELETE FROM sto_doc")
    void deleteAllDoc();

    @Query("SELECT COUNT(*) FROM sto_title")
    int getCountTitles();

    @Query("SELECT COUNT(*) FROM sto_subtitle")
    int getCountSubTitles();

    @Query("SELECT COUNT(*) FROM sto_doc")
    int getCountDocs();

    @Query("SELECT * FROM sto_title WHERE pos = :pos")
    StoItemsData_Title getTitle (int pos);

    @Query("SELECT * FROM sto_subtitle WHERE pos = :pos")
    StoItemsData_Subtitle getSubTitle (int pos);

    @Query("SELECT * FROM sto_doc WHERE pos = :pos")
    StoItemsData_Doc getDoc (int pos);

    @Query("SELECT EXISTS(SELECT pos FROM sto_title WHERE pos = :pos)")
    boolean isExistsTitle (int pos);

    @Query("SELECT EXISTS(SELECT pos FROM sto_subtitle WHERE pos = :pos)")
    boolean isExistsSubTitles (int pos);

    @Query("SELECT EXISTS(SELECT pos FROM sto_doc WHERE pos = :pos)")
    boolean isExistsDoc (int pos);

}
