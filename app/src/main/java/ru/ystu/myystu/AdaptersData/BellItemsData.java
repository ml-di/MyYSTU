package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;

public class BellItemsData implements Parcelable {

    private final int idType;
    private final int idSubType;
    private final String title;
    private final String subTitle;
    private final String date;
    private final String link;

    public BellItemsData(final int idType,
                         final int idSubType,
                         final String title,
                         final String subTitle,
                         final String date,
                         final String link) {
        this.idType = idType;
        this.idSubType = idSubType;
        this.title = title;
        this.subTitle = subTitle;
        this.date = date;
        this.link = link;
    }

    private BellItemsData(Parcel in) {
        idType = in.readInt();
        idSubType = in.readInt();
        title = in.readString();
        subTitle = in.readString();
        date = in.readString();
        link = in.readString();
    }

    public static final Creator<BellItemsData> CREATOR = new Creator<BellItemsData>() {
        @Override
        public BellItemsData createFromParcel(Parcel in) {
            return new BellItemsData(in);
        }

        @Override
        public BellItemsData[] newArray(int size) {
            return new BellItemsData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(idType);
        parcel.writeInt(idSubType);
        parcel.writeString(title);
        parcel.writeString(subTitle);
        parcel.writeString(date);
        parcel.writeString(link);
    }

    public int getIdType() {
        return idType;
    }
    public int getIdSubType() {
        return idSubType;
    }
    public String getTitle() {
        return title;
    }
    public String getSubTitle() {
        return subTitle;
    }
    public String getDate() {
        return date;
    }
    public String getLink() {
        return link;
    }
}
