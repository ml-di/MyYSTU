package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "event_full_doc")
public class EventAdditionalData_Documents implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private final int id;

    @ColumnInfo(name = "uid")
    private final int uid;

    @ColumnInfo(name = "title")
    private final String title;

    @ColumnInfo(name = "link")
    private final String link;

    @ColumnInfo(name = "ext")
    private final String ext;

    @ColumnInfo(name = "size")
    private final String size;


    public EventAdditionalData_Documents(final int id,
                                         final int uid,
                                         final String title,
                                         final String link,
                                         final String ext,
                                         final String size) {
        this.id = id;
        this.uid = uid;
        this.title = title;
        this.link = link;
        this.ext = ext;
        this.size = size;
    }

    @Ignore
    private EventAdditionalData_Documents (Parcel in) {
        id = in.readInt();
        uid = in.readInt();
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
        parcel.writeInt(id);
        parcel.writeInt(uid);
        parcel.writeString(title);
        parcel.writeString(link);
        parcel.writeString(ext);
        parcel.writeString(size);
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
