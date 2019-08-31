package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "sto_title")
public class StoItemsData_Title implements Parcelable {

    @PrimaryKey (autoGenerate = true)
    private int id;

    @ColumnInfo(name = "title")
    private final String title;

    public StoItemsData_Title(String title) {
        this.title = title;
    }

    @Ignore
    private StoItemsData_Title (Parcel in) {
        title = in.readString();
    }

    public static final Creator<StoItemsData_Title> CREATOR = new Creator<StoItemsData_Title>() {
        @Override
        public StoItemsData_Title createFromParcel(Parcel in) {
            return new StoItemsData_Title(in);
        }

        @Override
        public StoItemsData_Title[] newArray(int size) {
            return new StoItemsData_Title[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
    }

    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
}
