package ru.ystu.myystu.Database.Data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "event_full_divider")
public class EventFullDivider implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private final int id;

    @ColumnInfo(name = "uid")
    private final int uid;

    @ColumnInfo(name = "title")
    private final String title;

    public EventFullDivider(final int id,
                            final int uid,
                            final String title) {
        this.id = id;
        this.uid = uid;
        this.title = title;
    }

    @Ignore
    private EventFullDivider(Parcel in) {
        id = in.readInt();
        uid = in.readInt();
        title = in.readString();
    }

    public static final Creator<EventFullDivider> CREATOR = new Creator<EventFullDivider>() {
        @Override
        public EventFullDivider createFromParcel(Parcel in) {
            return new EventFullDivider(in);
        }

        @Override
        public EventFullDivider[] newArray(int size) {
            return new EventFullDivider[size];
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
}
