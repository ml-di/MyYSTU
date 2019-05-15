package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;

public class StringData implements Parcelable {

    private final String title;

    public StringData(final String title) {
        this.title = title;
    }

    private StringData(Parcel in){
        title = in.readString();
    }

    public static final Creator<StringData> CREATOR = new Creator<StringData>() {
        @Override
        public StringData createFromParcel(Parcel in) {
            return new StringData(in);
        }

        @Override
        public StringData[] newArray(int size) {
            return new StringData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(title);
    }

    public String getTitle() {
        return title;
    }
}
