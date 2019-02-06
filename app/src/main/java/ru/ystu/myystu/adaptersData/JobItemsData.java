package ru.ystu.myystu.adaptersData;

import android.os.Parcel;
import android.os.Parcelable;

public class JobItemsData implements Parcelable {

    private int id;
    private String organization;
    private String post;
    private String url;
    private String date;

    public JobItemsData(int id, String organization, String post, String url, String date) {
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
    public void setId(int id) {
        this.id = id;
    }

    public String getOrganization() {
        return organization;
    }
    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getPost() {
        return post;
    }
    public void setPost(String post) {
        this.post = post;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
}
