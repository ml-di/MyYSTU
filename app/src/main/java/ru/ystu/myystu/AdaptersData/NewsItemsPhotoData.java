package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import io.reactivex.annotations.NonNull;

@Entity(tableName = "news_photos")
public class NewsItemsPhotoData implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private final int id;

    @NonNull
    @ColumnInfo(name = "uid")
    private final int uid;

    @ColumnInfo(name = "height")
    private final int height;

    @ColumnInfo(name = "width")
    private final int width;

    @ColumnInfo(name = "urlSmall")
    private final String urlSmall;

    @ColumnInfo(name = "urlPreview")
    private final String urlPreview;

    @ColumnInfo(name = "urlFull")
    private final String urlFull;

    public NewsItemsPhotoData(final int id,
                              final int uid,
                              final int height,
                              final int width,
                              final String urlSmall,
                              final String urlPreview,
                              final String urlFull) {
        this.id = id;
        this.uid = uid;
        this.height = height;
        this.width = width;
        this.urlSmall = urlSmall;
        this.urlPreview = urlPreview;
        this.urlFull = urlFull;
    }

    @Ignore
    private NewsItemsPhotoData(Parcel in){
        id = in.readInt();
        uid = in.readInt();
        height = in.readInt();
        width = in.readInt();
        urlSmall = in.readString();
        urlPreview = in.readString();
        urlFull = in.readString();
    }

    public static final Parcelable.Creator<NewsItemsPhotoData> CREATOR = new Parcelable.Creator<NewsItemsPhotoData>() {
        @Override
        public NewsItemsPhotoData createFromParcel(Parcel in) {
            return new NewsItemsPhotoData(in);
        }

        @Override
        public NewsItemsPhotoData[] newArray(int size) {
            return new NewsItemsPhotoData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(uid);
        parcel.writeInt(height);
        parcel.writeInt(width);
        parcel.writeString(urlSmall);
        parcel.writeString(urlPreview);
        parcel.writeString(urlFull);
    }

    public int getId() {
        return id;
    }
    public int getUid() {
        return uid;
    }
    public int getHeight() {
        return height;
    }
    public int getWidth() {
        return width;
    }
    public String getUrlSmall() {
        return urlSmall;
    }
    public String getUrlPreview() {
        return urlPreview;
    }
    public String getUrlFull() {
        return urlFull;
    }

}
