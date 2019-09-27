package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.DrawableRes;

public class UpdateItemsTitleData implements Parcelable {

    private final String title;
    private final @DrawableRes int iconRes;

    public UpdateItemsTitleData(final String title,
                                final @DrawableRes int iconRes) {
        this.title = title;
        this.iconRes = iconRes;
    }

    private UpdateItemsTitleData(Parcel in){
        title = in.readString();
        iconRes = in.readInt();
    }

    public static final Creator<UpdateItemsTitleData> CREATOR = new Creator<UpdateItemsTitleData>() {
        @Override
        public UpdateItemsTitleData createFromParcel(Parcel in) {
            return new UpdateItemsTitleData(in);
        }

        @Override
        public UpdateItemsTitleData[] newArray(int size) {
            return new UpdateItemsTitleData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(title);
        parcel.writeInt(iconRes);
    }

    public String getTitle() {
        return title;
    }
    public int getIconRes() {
        return iconRes;
    }
}
