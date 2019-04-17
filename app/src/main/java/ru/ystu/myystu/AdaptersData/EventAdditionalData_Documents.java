package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;

public class EventAdditionalData_Documents implements Parcelable {

    private final String title;
    private final String link;
    private final String ext;
    private final String size;


    public EventAdditionalData_Documents(final String title,
                                         final String link,
                                         final String ext,
                                         final String size) {
        this.title = title;
        this.link = link;
        this.ext = ext;
        this.size = size;
    }

    private EventAdditionalData_Documents (Parcel in) {
        title = in.readString();
        link = in.readString();
        ext = in.readString();
        size = in.readString();
    }

    public static final Creator<EventAdditionalData_Documents> CREATOR = new Creator<EventAdditionalData_Documents>() {
        @Override
        public EventAdditionalData_Documents createFromParcel(Parcel in) {
            return new EventAdditionalData_Documents(in);
        }

        @Override
        public EventAdditionalData_Documents[] newArray(int size) {
            return new EventAdditionalData_Documents[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(title);
        parcel.writeString(link);
        parcel.writeString(ext);
        parcel.writeString(size);
    }

    public String getTitle() {
        return title;
    }
    public String getLink() {
        return link;
    }
    public String getExt() {
        return ext;
    }
    public String getSize() {
        return size;
    }
}
