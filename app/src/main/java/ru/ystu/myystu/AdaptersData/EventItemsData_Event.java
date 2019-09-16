package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "event_items")
public class EventItemsData_Event implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private final int id;

    @ColumnInfo(name = "link")
    private final String link;

    @ColumnInfo(name = "title")
    private final String title;

    @ColumnInfo(name = "date")
    private final String date;

    @ColumnInfo(name = "location")
    private final String location;

    @ColumnInfo(name = "photoUrl")
    private final String photoUrl;

    @Ignore
    private boolean isNew = false;

    public EventItemsData_Event(final int id,
                                final String link,
                                final String title,
                                final String date,
                                final String location,
                                final String photoUrl) {
        this.id = id;
        this.link = link;
        this.title = title;
        this.date = date;
        this.location = location;
        this.photoUrl = photoUrl;
    }

    @Ignore
    private EventItemsData_Event(Parcel in){
        id = in.readInt();
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
        parcel.writeInt(id);
        parcel.writeString(link);
        parcel.writeString(title);
        parcel.writeString(date);
        parcel.writeString(location);
        parcel.writeString(photoUrl);
    }

    public int getId() {
        return id;
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
    public boolean isNew() {
        return isNew;
    }
    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
