package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;

public class JobItemsData implements Parcelable {

    private final String organization;
    private final String post;
    private final String url;
    private final String fileType;

    public JobItemsData(final String organization,
                        final String post,
                        final String url,
                        final String fileType) {
        this.organization = organization;
        this.post = post;
        this.url = url;
        this.fileType = fileType;
    }

    private JobItemsData(Parcel in) {
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
        parcel.writeString(organization);
        parcel.writeString(post);
        parcel.writeString(url);
        parcel.writeString(fileType);
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
}
