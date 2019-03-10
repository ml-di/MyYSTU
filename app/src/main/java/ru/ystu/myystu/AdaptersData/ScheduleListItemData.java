package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;

public class ScheduleListItemData implements Parcelable {

    private final int id;
    private final String name;
    private final String size;
    private final String type;

    public ScheduleListItemData(final int id,
                                final String name,
                                final String size,
                                final String type) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.type = type;
    }

    private ScheduleListItemData(Parcel in){
        id = in.readInt();
        name = in.readString();
        size = in.readString();
        type = in.readString();
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
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(size);
        parcel.writeString(type);
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getSize() {
        return size;
    }
    public String getType() {
        return type;
    }
}
