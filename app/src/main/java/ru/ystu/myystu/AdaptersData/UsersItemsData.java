package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "users_items")
public class UsersItemsData implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private final int id;

    @ColumnInfo(name = "link")
    private final String link;

    @ColumnInfo(name = "image")
    private final String image;

    @ColumnInfo(name = "name")
    private final String name;

    @ColumnInfo(name = "information")
    private final String information;

    public UsersItemsData (final int id,
                           final String link,
                           final String image,
                           final String name,
                           final String information) {
        this.id = id;
        this.link = link;
        this.image = image;
        this.name = name;
        this.information = information;
    }

    @Ignore
    private UsersItemsData (Parcel in) {
        id = in.readInt();
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
        parcel.writeInt(id);
        parcel.writeString(link);
        parcel.writeString(image);
        parcel.writeString(name);
        parcel.writeString(information);
    }

    public int getId() {
        return id;
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
