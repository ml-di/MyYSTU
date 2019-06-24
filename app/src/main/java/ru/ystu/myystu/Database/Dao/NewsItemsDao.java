package ru.ystu.myystu.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import ru.ystu.myystu.AdaptersData.NewsItemsData;
import ru.ystu.myystu.AdaptersData.NewsItemsData_DontAttach;

@Dao
public interface NewsItemsDao {

    @Insert
    void insertNewsDontAttach(NewsItemsData_DontAttach... newsItemsData_dontAttaches);

    @Insert
    void insertNewsAttach(NewsItemsData... newsItemsData);

    @Query("DELETE FROM news_dont_attach")
    void deleteNewsDontAttach();

    @Query("DELETE FROM news_attach")
    void deleteNewsAttach();

    @Query("DELETE FROM news_photos")
    void deleteNewsAllPhotos();

    @Query("SELECT COUNT(*) FROM news_dont_attach")
    int getCountNewsDontAttach();

    @Query("SELECT COUNT(*) FROM news_attach")
    int getCountNewsAttach();

    @Query("SELECT COUNT(*) FROM news_photos")
    int getCountPhotos();

    @Query("SELECT EXISTS(SELECT id FROM news_attach WHERE id = :id)")
    boolean isExistsAttach(int id);

    @Query("SELECT EXISTS(SELECT id FROM news_dont_attach WHERE id = :id)")
    boolean isExistsDontAttach(int id);

    @Query("SELECT * FROM news_dont_attach WHERE id = :id")
    NewsItemsData_DontAttach getNewsDontAttach (int id);

    @Query("SELECT * FROM news_attach WHERE id = :id")
    NewsItemsData getNewsAttach (int id);

}
