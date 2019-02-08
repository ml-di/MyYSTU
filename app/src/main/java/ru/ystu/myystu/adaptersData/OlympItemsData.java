package ru.ystu.myystu.adaptersData;

import android.os.Parcel;
import android.os.Parcelable;

public class OlympItemsData implements Parcelable {

    private int id;
    private String title;
    private String text;

    public OlympItemsData(int id, String title, String text) {
        this.id = id;
        this.title = title;
        this.text = text;
    }

    private OlympItemsData(Parcel in){
        id = in.readInt();
        title = in.readString();
        text = in.readString();
    }

    public static final Creator<OlympItemsData> CREATOR = new Creator<OlympItemsData>() {
        @Override
        public OlympItemsData createFromParcel(Parcel in) {
            return new OlympItemsData(in);
        }

        @Override
        public OlympItemsData[] newArray(int size) {
            return new OlympItemsData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(text);
    }

    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getText() {
        return text;
    }
}
