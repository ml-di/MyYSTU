package ru.ystu.myystu.Utils.BottomSheetMenu.Data;

import android.os.Parcel;
import android.os.Parcelable;

public class NullIconsAndTextData implements Parcelable {

    private int itemId;
    private String title;
    private boolean isEnabled = true;

    public NullIconsAndTextData(int itemId,
                                String title) {
        this.itemId = itemId;
        this.title = title;
    }

    private NullIconsAndTextData(Parcel in) {
        itemId = in.readInt();
        title = in.readString();
    }

    public static final Creator<NullIconsAndTextData> CREATOR = new Creator<NullIconsAndTextData>() {
        @Override
        public NullIconsAndTextData createFromParcel(Parcel in) {
            return new NullIconsAndTextData(in);
        }

        @Override
        public NullIconsAndTextData[] newArray(int size) {
            return new NullIconsAndTextData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(itemId);
        parcel.writeString(title);
    }

    public int getItemId() {
        return itemId;
    }
    public String getTitle() {
        return title;
    }

    public boolean isEnabled() {
        return isEnabled;
    }
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
