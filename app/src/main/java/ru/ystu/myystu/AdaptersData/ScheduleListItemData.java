package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "schedule_list")
public class ScheduleListItemData implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "uid")
    private final int uid;

    @ColumnInfo(name = "id")
    private final int id;

    @ColumnInfo(name = "name")
    private final String name;

    @ColumnInfo(name = "link")
    private final String link;

    @Ignore
    private boolean isDownload = false;

    public ScheduleListItemData(final int uid,
                                final int id,
                                final String name,
                                final String link) {

        this.uid = uid;
        this.id = id;
        this.name = name;
        this.link = link;
    }

    @Ignore
    private ScheduleListItemData(Parcel in){
        uid = in.readInt();
        id = in.readInt();
        name = in.readString();
        link = in.readString();
        isDownload = in.readByte() != 0;
    }

    public static final Creator<ScheduleListItemData> CREATOR = new Creator<ScheduleListItemData>() {
        @Override
        public ScheduleListItemData createFromParcel(Parcel in) {
            return new ScheduleListItemData(in);
        }

        @Override
        public ScheduleListItemData[] newArray(int size) {
            return new ScheduleListItemData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(uid);
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(link);
        parcel.writeByte((byte) (isDownload ? 1 : 0));
    }

    public int getUid() {
        return uid;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getLink() {
        return link;
    }

    public boolean isDownload() {
        return isDownload;
    }
    public void setDownload(boolean download) {
        isDownload = download;
    }
}
