package ru.ystu.myystu.AdaptersData;

import android.os.Parcel;
import android.os.Parcelable;

public class AboutLicensesData implements Parcelable {

    private final String title;
    private final String text;

    public AboutLicensesData(final String title,
                             final String text) {
        this.title = title;
        this.text = text;
    }

    private AboutLicensesData (Parcel in) {
        title = in.readString();
        text = in.readString();
    }

    public static final Creator<AboutLicensesData> CREATOR = new Creator<AboutLicensesData>() {
        @Override
        public AboutLicensesData createFromParcel(Parcel in) {
            return new AboutLicensesData(in);
        }

        @Override
        public AboutLicensesData[] newArray(int size) {
            return new AboutLicensesData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(text);
    }

    public String getTitle() {
        return title;
    }
    public String getText() {
        return text;
    }
}
