package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "news_dont_attach")
public class NewsItemsData_DontAttach implements Parcelable {

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

    public NewsItemsData_DontAttach(final int id,
                                    final int isPinned,
                                    final int signer,
                                    final String urlPost,
                                    final String date,
                                    final String text) {
        this.id = id;
        this.isPinned = isPinned;
        this.signer = signer;
        this.urlPost = urlPost;
        this.date = date;
        this.text = text;
    }

    @Ignore
    private NewsItemsData_DontAttach(Parcel in) {
        id = in.readInt();
        isPinned = in.readInt();
        signer = in.readInt();
        urlPost = in.readString();
        date = in.readString();
        text = in.readString();
    }

    public static final Creator<NewsItemsData_DontAttach> CREATOR = new Creator<NewsItemsData_DontAttach>() {
        @Override
        public NewsItemsData_DontAttach createFromParcel(Parcel in) {
            return new NewsItemsData_DontAttach(in);
        }

        @Override
        public NewsItemsData_DontAttach[] newArray(int size) {
            return new NewsItemsData_DontAttach[size];
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
    }

    public int getId() {
        return id;
    }
    public int getIsPinned() {
        return isPinned;
    }
    public int getSigner(){
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

}
