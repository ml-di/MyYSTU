package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity (tableName = "sto_doc")
public class StoItemsData_Doc implements Parcelable {

    @PrimaryKey (autoGenerate = true)
    private int id;

    @ColumnInfo (name = "fileName")
    private final String fileName;

    @ColumnInfo (name = "fileExt")
    private final String fileExt;

    @ColumnInfo (name = "summary")
    private final String summary;

    @ColumnInfo (name = "url")
    private final String url;

    public StoItemsData_Doc(final String fileName,
                            final String fileExt,
                            final String summary,
                            final String url) {
        this.fileName = fileName;
        this.fileExt = fileExt;
        this.summary = summary;
        this.url = url;
    }

    @Ignore
    private StoItemsData_Doc (Parcel in) {
        fileName = in.readString();
        fileExt = in.readString();
        summary = in.readString();
        url = in.readString();
    }

    public static final Creator<StoItemsData_Doc> CREATOR = new Creator<StoItemsData_Doc>() {
        @Override
        public StoItemsData_Doc createFromParcel(Parcel in) {
            return new StoItemsData_Doc(in);
        }

        @Override
        public StoItemsData_Doc[] newArray(int size) {
            return new StoItemsData_Doc[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(fileName);
        parcel.writeString(fileExt);
        parcel.writeString(summary);
        parcel.writeString(url);
    }

    public int getId() {
        return id;
    }
    public String getFileName() {
        return fileName;
    }
    public String getFileExt() {
        return fileExt;
    }
    public String getSummary() {
        return summary;
    }
    public String getUrl() {
        return url;
    }
}
