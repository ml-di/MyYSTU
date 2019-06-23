package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "event_full_additional")
public class EventAdditionalData_Additional implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private final int id;

    @ColumnInfo(name = "uid")
    private final int uid;

    @ColumnInfo(name = "title")
    private final String title;

    @ColumnInfo(name = "description")
    private final String description;

    public EventAdditionalData_Additional(final int id,
                                          final int uid,
                                          final String title,
                                          final String description) {
        this.id = id;
        this.uid = uid;
        this.title = title;
        this.description = description;
    }

    @Ignore
    private EventAdditionalData_Additional(Parcel in){
        id = in.readInt();
        uid = in.readInt();
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
        parcel.writeInt(id);
        parcel.writeInt(uid);
        parcel.writeString(title);
        parcel.writeString(description);
    }

    public int getId() {
        return id;
    }
    public int getUid() {
        return uid;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
}
