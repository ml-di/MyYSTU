package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;

public class EventItemsData_Divider implements Parcelable {

    private final String title;

    public EventItemsData_Divider(final String title) {
        this.title = title;
    }

    private EventItemsData_Divider(Parcel in){
        title = in.readString();
    }

    public static final Creator<EventItemsData_Divider> CREATOR = new Creator<EventItemsData_Divider>() {
        @Override
        public EventItemsData_Divider createFromParcel(Parcel in) {
            return new EventItemsData_Divider(in);
        }

        @Override
        public EventItemsData_Divider[] newArray(int size) {
            return new EventItemsData_Divider[size];
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
