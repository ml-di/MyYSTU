package ru.ystu.myystu.adaptersData;

import android.os.Parcel;
import android.os.Parcelable;

public class NewsItemsData implements Parcelable {

    private int id;
    private int countPhoto;
    private int isPinned;
    private String urlPost;
    private String date;
    private String text;
    private String[] urlsPhoto;

    public NewsItemsData(int id, int countPhoto, int isPinned, String urlPost, String date, String text, String[] urlsPhoto) {
        this.id = id;
        this.countPhoto = countPhoto;
        this.isPinned = isPinned;
        this.urlPost = urlPost;
        this.date = date;
        this.text = text;
        this.urlsPhoto = urlsPhoto;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(countPhoto);
        parcel.writeInt(isPinned);
        parcel.writeString(urlPost);
        parcel.writeString(date);
        parcel.writeString(text);
        parcel.writeStringArray(urlsPhoto);
    }

    private NewsItemsData(Parcel in) {
        id = in.readInt();
        countPhoto = in.readInt();
        isPinned = in.readInt();
        urlPost = in.readString();
        date = in.readString();
        text = in.readString();
        urlsPhoto = in.createStringArray();
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

    public int getId() {
        return id;
    }
    public int getCountPhoto() {
        return countPhoto;
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
    public String[] getUrlsPhoto() {
        return urlsPhoto;
    }
}
