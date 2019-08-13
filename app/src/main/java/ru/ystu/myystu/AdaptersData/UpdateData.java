package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;

public class UpdateData implements Parcelable {

    private final String type;
    private final int count;
    private final int countItem;

    public UpdateData(final String type,
                      final int count,
                      final int countItem) {
        this.type = type;
        this.count = count;
        this.countItem = countItem;
    }

    private UpdateData (Parcel in) {
        type = in.readString();
        count = in.readInt();
        countItem = in.readInt();
    }

    public static final Creator<UpdateData> CREATOR = new Creator<UpdateData>() {
        @Override
        public UpdateData createFromParcel(Parcel in) {
            return new UpdateData(in);
        }

        @Override
        public UpdateData[] newArray(int size) {
            return new UpdateData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(type);
        parcel.writeInt(count);
        parcel.writeInt(countItem);
    }

    public String getType() {
        return type;
    }
    public int getCount() {
        return count;
    }
    public int getCountItem() {
        return countItem;
    }
}
