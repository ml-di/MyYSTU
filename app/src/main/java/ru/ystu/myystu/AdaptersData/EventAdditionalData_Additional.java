package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;

public class EventAdditionalData_Additional implements Parcelable {

    private final String title;
    private final String description;

    public EventAdditionalData_Additional(final String title,
                                          final String description) {
        this.title = title;
        this.description = description;
    }

    private EventAdditionalData_Additional(Parcel in){
        title = in.readString();
        description = in.readString();
    }

    public static final Creator<EventAdditionalData_Additional> CREATOR = new Creator<EventAdditionalData_Additional>() {
        @Override
        public EventAdditionalData_Additional createFromParcel(Parcel in) {
            return new EventAdditionalData_Additional(in);
        }

        @Override
        public EventAdditionalData_Additional[] newArray(int size) {
            return new EventAdditionalData_Additional[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(title);
        parcel.writeString(description);
    }

    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
}
