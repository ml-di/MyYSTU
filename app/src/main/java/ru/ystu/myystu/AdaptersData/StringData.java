package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "divider")
public class StringData implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private final int id;

    @ColumnInfo(name = "title")
    private final String title;

    public StringData(final int id,
                      final String title) {
        this.id = id;
        this.title = title;
    }

    @Ignore
    private StringData(Parcel in){
        id = in.readInt();
        title = in.readString();
    }

    public static final Creator<StringData> CREATOR = new Creator<StringData>() {
        @Override
        public StringData createFromParcel(Parcel in) {
            return new StringData(in);
        }

        @Override
        public StringData[] newArray(int size) {
            return new StringData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeString(title);
    }

    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
}
