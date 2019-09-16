package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;

public class UpdateItemsTitle implements Parcelable {

    private final String title;

    public UpdateItemsTitle(final String title) {
        this.title = title;
    }

    private UpdateItemsTitle(Parcel in){
        title = in.readString();
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
    }

    public String getTitle() {
        return title;
    }

}
