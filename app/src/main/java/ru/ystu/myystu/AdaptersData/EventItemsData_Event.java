package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;

public class EventItemsData_Event implements Parcelable {

    private final String link;
    private final String title;
    private final String date;
    private final String location;
    private final String photoUrl;

    public EventItemsData_Event(final String link,
                                final String title,
                                final String date,
                                final String location,
                                final String photoUrl) {
        this.link = link;
        this.title = title;
        this.date = date;
        this.location = location;
        this.photoUrl = photoUrl;

    }

    private EventItemsData_Event(Parcel in){
        link = in.readString();
        title = in.readString();
        date = in.readString();
        location = in.readString();
        photoUrl = in.readString();
    }

    public static final Creator<EventItemsData_Event> CREATOR = new Creator<EventItemsData_Event>() {
        @Override
        public EventItemsData_Event createFromParcel(Parcel in) {
            return new EventItemsData_Event(in);
        }

        @Override
        public EventItemsData_Event[] newArray(int size) {
            return new EventItemsData_Event[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(link);
        parcel.writeString(title);
        parcel.writeString(date);
        parcel.writeString(location);
        parcel.writeString(photoUrl);
    }

    public String getLink() {
        return link;
    }
    public String getTitle() {
        return title;
    }
    public String getDate() {
        return date;
    }
    public String getLocation() {
        return location;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }
}
