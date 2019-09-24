package ru.ystu.myystu.Database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.ystu.myystu.AdaptersData.JobItemsData;

@Dao
public interface JobItemsDao {

    @Insert
    void insert(JobItemsData... jobItemsData);

    @Query("DELETE FROM job_items")
    void deleteAll();

    @Query("SELECT * FROM job_items")
    List<JobItemsData> getAllJobItems();

    @Query("SELECT COUNT(*) FROM job_items")
    int getCount();

    @Query("SELECT EXISTS (SELECT organization FROM job_items WHERE organization = :name)")
    boolean isExistsJobByName (String name);

}
