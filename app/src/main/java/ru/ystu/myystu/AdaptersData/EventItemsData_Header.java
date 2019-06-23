package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import ru.ystu.myystu.Database.Converters.StringArraysConverter;

@Entity(tableName = "event_header")
public class EventItemsData_Header implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private final int id;

    @ColumnInfo(name = "title")
    @TypeConverters({StringArraysConverter.class})
    private final String[] title;

    @ColumnInfo(name = "url")
    @TypeConverters({StringArraysConverter.class})
    private final String[] url;

    @ColumnInfo(name = "selected_id")
    private final int selected_id;

    public EventItemsData_Header(final int id,
                                 final String[] title,
                                 final String[] url,
                                 final int selected_id) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.selected_id = selected_id;
    }

    @Ignore
    private EventItemsData_Header(Parcel in){
        id = in.readInt();
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
        parcel.writeInt(id);
        parcel.writeStringArray(title);
        parcel.writeStringArray(url);
        parcel.writeInt(selected_id);
    }

    public int getId() {
        return id;
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
