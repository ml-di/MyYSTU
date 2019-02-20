package ru.ystu.myystu.adaptersData;

import android.os.Parcel;
import android.os.Parcelable;

public class NewsItemsPhotoData implements Parcelable {

    private final int height;
    private final int width;
    private final String urlPreview;
    private final String urlFull;
    private final String urlSmall;

    public NewsItemsPhotoData(final int height,
                              final int width,
                              final String urlPreview,
                              final String urlFull,
                              final String urlSmall) {
        this.height = height;
        this.width = width;
        this.urlPreview = urlPreview;
        this.urlFull = urlFull;
        this.urlSmall = urlSmall;
    }

    private NewsItemsPhotoData(Parcel in){
        height = in.readInt();
        width = in.readInt();
        urlPreview = in.readString();
        urlFull = in.readString();
        urlSmall = in.readString();
    }

    public static final Parcelable.Creator<NewsItemsPhotoData> CREATOR = new Parcelable.Creator<NewsItemsPhotoData>() {
        @Override
        public NewsItemsPhotoData createFromParcel(Parcel in) {
            return new NewsItemsPhotoData(in);
        }

        @Override
        public NewsItemsPhotoData[] newArray(int size) {
            return new NewsItemsPhotoData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(height);
        parcel.writeInt(width);
        parcel.writeString(urlPreview);
        parcel.writeString(urlFull);
        parcel.writeString(urlSmall);
    }

    public int getHeight() {
        return height;
    }
    public int getWidth() {
        return width;
    }
    public String getUrlPreview() {
        return urlPreview;
    }
    public String getUrlFull() {
        return urlFull;
    }

    public String getUrlSmall() {
        return urlSmall;
    }
}
