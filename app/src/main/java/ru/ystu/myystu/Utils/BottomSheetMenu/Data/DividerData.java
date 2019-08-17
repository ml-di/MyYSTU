package ru.ystu.myystu.Utils.BottomSheetMenu.Data;

import android.os.Parcel;
import android.os.Parcelable;

public class DividerData implements Parcelable {

    private boolean isIcon;

    public DividerData(boolean isIcon) {
        this.isIcon = isIcon;
    }

    private DividerData (Parcel in) {
        isIcon = in.readByte() != 0;
    }

    public static final Creator<DividerData> CREATOR = new Creator<DividerData>() {
        @Override
        public DividerData createFromParcel(Parcel in) {
            return new DividerData(in);
        }

        @Override
        public DividerData[] newArray(int size) {
            return new DividerData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isIcon ? 1 : 0));
    }

    public boolean isIcon() {
        return isIcon;
    }
}
