package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;

public class ToolbarPlaceholderData implements Parcelable {

    private final int id;

    public ToolbarPlaceholderData(final int id) {
        this.id = id;
    }

    private ToolbarPlaceholderData (Parcel in) {
        id = in.readInt();
    }

    public static final Creator<ToolbarPlaceholderData> CREATOR = new Creator<ToolbarPlaceholderData>() {
        @Override
        public ToolbarPlaceholderData createFromParcel(Parcel in) {
            return new ToolbarPlaceholderData(in);
        }

        @Override
        public ToolbarPlaceholderData[] newArray(int size) {
            return new ToolbarPlaceholderData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
    }

    public int getId() {
        return id;
    }
}
