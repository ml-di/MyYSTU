package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;

public class EventItemsData_Header implements Parcelable {

    private final String[] title;
    private final String[] url;
    private final int selected_id;

    public EventItemsData_Header(final String[] title,
                                 final String[] url,
                                 final int selected_id) {
        this.title = title;
        this.url = url;
        this.selected_id = selected_id;
    }

    private EventItemsData_Header(Parcel in){
        title = in.createStringArray();
        url = in.createStringArray();
        selected_id = in.readInt();
    }

    public static final Creator<EventItemsData_Header> CREATOR = new Creator<EventItemsData_Header>() {
        @Override
        public EventItemsData_Header createFromParcel(Parcel in) {
            return new EventItemsData_Header(in);
        }

        @Override
        public EventItemsData_Header[] newArray(int size) {
            return new EventItemsData_Header[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeStringArray(title);
        parcel.writeStringArray(url);
        parcel.writeInt(selected_id);
    }

    public String[] getTitle() {
        return title;
    }
    public String[] getUrl() {
        return url;
    }
    public int getSelected_id() {
        return selected_id;
    }
}
