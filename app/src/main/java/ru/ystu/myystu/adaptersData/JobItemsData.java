package ru.ystu.myystu.adaptersData;

import android.os.Parcel;
import android.os.Parcelable;

public class JobItemsData implements Parcelable {

    private final int id;
    private final String organization;
    private final String post;
    private final String url;
    private final String date;

    public JobItemsData(final int id,
                        final String organization,
                        final String post,
                        final String url,
                        final String date) {
        this.id = id;
        this.organization = organization;
        this.post = post;
        this.url = url;
        this.date = date;
    }

    private JobItemsData(Parcel in) {
        id = in.readInt();
        organization = in.readString();
        post = in.readString();
        url = in.readString();
        date = in.readString();
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
        parcel.writeString(date);
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
    public String getDate() {
        return date;
    }
}
