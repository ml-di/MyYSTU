package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.DrawableRes;

public class UpdateItemsTitle implements Parcelable {

    private final String title;
    private final @DrawableRes int iconRes;

    public UpdateItemsTitle(final String title,
                            final @DrawableRes int iconRes) {
        this.title = title;
        this.iconRes = iconRes;
    }

    private UpdateItemsTitle(Parcel in){
        title = in.readString();
        iconRes = in.readInt();
    }

    public static final Creator<UpdateItemsTitle> CREATOR = new Creator<UpdateItemsTitle>() {
        @Override
        public UpdateItemsTitle createFromParcel(Parcel in) {
            return new UpdateItemsTitle(in);
        }

        @Override
        public UpdateItemsTitle[] newArray(int size) {
            return new UpdateItemsTitle[size];
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
