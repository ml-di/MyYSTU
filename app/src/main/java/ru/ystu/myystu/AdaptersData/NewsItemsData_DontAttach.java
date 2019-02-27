package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;

public class NewsItemsData_DontAttach implements Parcelable {

    private final int id;
    private final int isPinned;
    private final String urlPost;
    private final String date;
    private final String text;

    public NewsItemsData_DontAttach(final int id,
                                    final int isPinned,
                                    final String urlPost,
                                    final String date,
                                    final String text) {
        this.id = id;
        this.isPinned = isPinned;
        this.urlPost = urlPost;
        this.date = date;
        this.text = text;
    }

    private NewsItemsData_DontAttach(Parcel in) {
        id = in.readInt();
        isPinned = in.readInt();
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
