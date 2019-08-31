package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "sto_subtitle")
public class StoItemsData_Subtitle implements Parcelable {

    @PrimaryKey (autoGenerate = true)
    private int id;

    @ColumnInfo (name = "subtitle")
    private final String subtitle;

    public StoItemsData_Subtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    @Ignore
    private StoItemsData_Subtitle(Parcel in) {
        subtitle = in.readString();
    }

    public static final Creator<StoItemsData_Subtitle> CREATOR = new Creator<StoItemsData_Subtitle>() {
        @Override
        public StoItemsData_Subtitle createFromParcel(Parcel in) {
            return new StoItemsData_Subtitle(in);
        }

        @Override
        public StoItemsData_Subtitle[] newArray(int size) {
            return new StoItemsData_Subtitle[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(subtitle);
    }

    public int getId() {
        return id;
    }
    public String getSubtitle() {
        return subtitle;
    }
}
