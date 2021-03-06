package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "job_items")
public class JobItemsData implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private final int id;

    @ColumnInfo(name = "organization")
    private final String organization;

    @ColumnInfo(name = "post")
    private final String post;

    @ColumnInfo(name = "url")
    private final String url;

    @ColumnInfo(name = "fileType")
    private final String fileType;

    @Ignore
    private boolean isNew;

    public JobItemsData(final int id,
                        final String organization,
                        final String post,
                        final String url,
                        final String fileType) {
        this.id = id;
        this.organization = organization;
        this.post = post;
        this.url = url;
        this.fileType = fileType;
    }

    @Ignore
    private JobItemsData(Parcel in) {
        id = in.readInt();
        organization = in.readString();
        post = in.readString();
        url = in.readString();
        fileType = in.readString();
    }

    public static final Creator<JobItemsData> CREATOR = new Creator<JobItemsData>() {
        @Override
        public JobItemsData createFromParcel(Parcel in) {
            return new JobItemsData(in);
        }

        @Override
        public JobItemsData[] newArray(int size) {
            return new JobItemsData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(organization);
        parcel.writeString(post);
        parcel.writeString(url);
        parcel.writeString(fileType);
    }

    public int getId() {
        return id;
    }
    public String getOrganization() {
        return organization;
    }
    public String getPost() {
        return post;
    }
    public String getUrl() {
        return url;
    }
    public String getFileType() {
        return fileType;
    }
    public boolean isNew() {
        return isNew;
    }
    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
