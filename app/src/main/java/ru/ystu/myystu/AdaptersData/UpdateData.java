package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "updates")
public class UpdateData implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private final int id;

    @ColumnInfo(name = "typeId")
    private final int typeId;

    @ColumnInfo(name = "count")
    private final int count;

    public UpdateData(final int id,
                      final int typeId,
                      final int count) {
        this.id = id;
        this.typeId = typeId;
        this.count = count;
    }

    @Ignore
    private UpdateData (Parcel in) {
        id = in.readInt();
        typeId = in.readInt();
        count = in.readInt();
    }

    public static final Creator<UpdateData> CREATOR = new Creator<UpdateData>() {
        @Override
        public UpdateData createFromParcel(Parcel in) {
            return new UpdateData(in);
        }

        @Override
        public UpdateData[] newArray(int size) {
            return new UpdateData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeInt(typeId);
        parcel.writeInt(count);
    }

    public int getId() {
        return id;
    }
    public int getTypeId() {
        return typeId;
    }
    public int getCount() {
        return count;
    }
}
