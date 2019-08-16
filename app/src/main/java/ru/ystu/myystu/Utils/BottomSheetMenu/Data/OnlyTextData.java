package ru.ystu.myystu.Utils.BottomSheetMenu.Data;

import android.os.Parcel;
import android.os.Parcelable;

public class OnlyTextData implements Parcelable {

    private int itemId;
    private String title;
    private boolean isEnabled = true;

    public OnlyTextData(int itemId,
                        String stringResId) {
        this.itemId = itemId;
        this.title = stringResId;
    }

    private OnlyTextData(Parcel in) {
        itemId = in.readInt();
        title = in.readString();
    }

    public static final Creator<OnlyTextData> CREATOR = new Creator<OnlyTextData>() {
        @Override
        public OnlyTextData createFromParcel(Parcel in) {
            return new OnlyTextData(in);
        }

        @Override
        public OnlyTextData[] newArray(int size) {
            return new OnlyTextData[size];
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

    public String getTitle() {
        return title;
    }
    public int getItemId() {
        return itemId;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
    public boolean isEnabled() {
        return isEnabled;
    }
}
