package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;

public class NewsItemsData_Header implements Parcelable {

    private final int id;
    private final String text;

    public NewsItemsData_Header(final int id,
                                final String text) {
        this.id = id;
        this.text = text;
    }

    private NewsItemsData_Header(Parcel in) {
        id = in.readInt();
        text = in.readString();
    }

    public static final Creator<NewsItemsData_Header> CREATOR = new Creator<NewsItemsData_Header>() {
        @Override
        public NewsItemsData_Header createFromParcel(Parcel in) {
            return new NewsItemsData_Header(in);
        }

        @Override
        public NewsItemsData_Header[] newArray(int size) {
            return new NewsItemsData_Header[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(text);
    }

    public int getId() {
        return id;
    }
    public String getText() {
        return text;
    }
}
