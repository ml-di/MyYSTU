package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class NewsItemsData implements Parcelable {

    private final int id;
    private final int isPinned;
    private final String urlPost;
    private final String date;
    private final String text;

    private final ArrayList<NewsItemsPhotoData> listPhoto;

    public NewsItemsData(final int id,
                         final int isPinned,
                         final String urlPost,
                         final String date,
                         final String text,
                         final ArrayList<NewsItemsPhotoData> listPhoto) {
        this.id = id;
        this.isPinned = isPinned;
        this.urlPost = urlPost;
        this.date = date;
        this.text = text;
        this.listPhoto = listPhoto;
    }

    private NewsItemsData(Parcel in){
        id = in.readInt();
        isPinned = in.readInt();
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
