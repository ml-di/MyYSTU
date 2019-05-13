package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;

public class UsersItemsData implements Parcelable {

    private final String link;
    private final String image;
    private final String name;
    private final String information;

    public UsersItemsData (final String link,
                           final String image,
                           final String name,
                           final String information) {
        this.link = link;
        this.image = image;
        this.name = name;
        this.information = information;
    }

    private UsersItemsData (Parcel in) {
        link = in.readString();
        image = in.readString();
        name = in.readString();
        information = in.readString();
    }

    public static final Creator<UsersItemsData> CREATOR = new Creator<UsersItemsData>() {
        @Override
        public UsersItemsData createFromParcel(Parcel in) {
            return new UsersItemsData(in);
        }

        @Override
        public UsersItemsData[] newArray(int size) {
            return new UsersItemsData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(link);
        parcel.writeString(image);
        parcel.writeString(name);
        parcel.writeString(information);
    }

    public String getLink() {
        return link;
    }
    public String getImage() {
        return image;
    }
    public String getName() {
        return name;
    }
    public String getInformation() {
        return information;
    }
}
