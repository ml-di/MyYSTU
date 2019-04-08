package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;

public class ScheduleListItemData implements Parcelable {

    private final int id;
    private final String name;
    private final String link;

    public ScheduleListItemData(final int id,
                                final String name,
                                final String link) {

        this.id = id;
        this.name = name;
        this.link = link;
    }

    private ScheduleListItemData(Parcel in){
        id = in.readInt();
        name = in.readString();
        link = in.readString();
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
        parcel.writeString(link);
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
}
