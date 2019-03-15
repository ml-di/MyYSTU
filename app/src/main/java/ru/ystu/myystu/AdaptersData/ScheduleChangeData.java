package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ScheduleChangeData implements Parcelable {

    private final int id;
    private final String date;
    private final String text;

    public ScheduleChangeData(final int id,
                              final String date,
                              final String text) {
        this.id = id;
        this.date = date;
        this.text = text;
    }

    private ScheduleChangeData(Parcel in){
        id = in.readInt();
        date = in.readString();
        text = in.readString();
    }

    public static final Creator<ScheduleChangeData> CREATOR = new Creator<ScheduleChangeData>() {
        @Override
        public ScheduleChangeData createFromParcel(Parcel in) {
            return new ScheduleChangeData(in);
        }

        @Override
        public ScheduleChangeData[] newArray(int size) {
            return new ScheduleChangeData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(date);
        parcel.writeString(text);
    }

    public int getId() {
        return id;
    }
    public String getDate() {
        return date;
    }
    public String getText() {
        return text;
    }
}
