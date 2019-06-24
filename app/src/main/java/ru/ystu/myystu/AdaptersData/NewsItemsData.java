package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.Relation;
import androidx.room.TypeConverters;

import java.util.ArrayList;
import java.util.List;

import ru.ystu.myystu.Database.Converters.ListJsonConverter;

@Entity(tableName = "news_attach")
public class NewsItemsData implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private final int id;

    @ColumnInfo(name = "isPinned")
    private final int isPinned;

    @ColumnInfo(name = "signer")
    private final int signer;

    @ColumnInfo(name = "urlPost")
    private final String urlPost;

    @ColumnInfo(name = "date")
    private final String date;

    @ColumnInfo(name = "text")
    private final String text;

    @ColumnInfo(name = "listPhoto")
    @TypeConverters({ListJsonConverter.class})
    private final ArrayList<NewsItemsPhotoData> listPhoto;

    public NewsItemsData(final int id,
                         final int isPinned,
                         final int signer,
                         final String urlPost,
                         final String date,
                         final String text,
                         final ArrayList<NewsItemsPhotoData> listPhoto) {
        this.id = id;
        this.isPinned = isPinned;
        this.signer = signer;
        this.urlPost = urlPost;
        this.date = date;
        this.text = text;
        this.listPhoto = listPhoto;
    }

    @Ignore
    private NewsItemsData(Parcel in){
        id = in.readInt();
        isPinned = in.readInt();
        signer = in.readInt();
        urlPost = in.readString();
        date = in.readString();
        text = in.readString();
        listPhoto = in.createTypedArrayList(NewsItemsPhotoData.CREATOR);
    }

    public static final Creator<NewsItemsData> CREATOR = new Creator<NewsItemsData>() {
        @Override
        public NewsItemsData createFromParcel(Parcel in) {
            return new NewsItemsData(in);
        }

        @Override
        public NewsItemsData[] newArray(int size) {
            return new NewsItemsData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(isPinned);
        parcel.writeInt(signer);
        parcel.writeString(urlPost);
        parcel.writeString(date);
        parcel.writeString(text);
        parcel.writeTypedList(listPhoto);
    }

    public int getId() {
        return id;
    }
    public int getIsPinned() {
        return isPinned;
    }
    public int getSigner() {
        return signer;
    }
    public String getUrlPost() {
        return urlPost;
    }
    public String getDate() {
        return date;
    }
    public String getText() {
        return text;
    }
    public ArrayList<NewsItemsPhotoData> getListPhoto() {
        return listPhoto;
    }
}
