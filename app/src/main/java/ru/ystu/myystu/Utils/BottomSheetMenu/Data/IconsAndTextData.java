package ru.ystu.myystu.Utils.BottomSheetMenu.Data;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class IconsAndTextData implements Parcelable {

    private int itemId;
    private String title;
    private Drawable icon;
    private boolean isEnabled = true;

    public IconsAndTextData(int itemId,
                            String title,
                            Drawable icon) {
        this.itemId = itemId;
        this.title = title;
        this.icon = icon;
    }

    private IconsAndTextData(Parcel in) {
        itemId = in.readInt();
        title = in.readString();

        final Bitmap bitmap = in.readParcelable(getClass().getClassLoader());
        icon = new BitmapDrawable(Resources.getSystem(), bitmap);
    }

    public static final Creator<IconsAndTextData> CREATOR = new Creator<IconsAndTextData>() {
        @Override
        public IconsAndTextData createFromParcel(Parcel in) {
            return new IconsAndTextData(in);
        }

        @Override
        public IconsAndTextData[] newArray(int size) {
            return new IconsAndTextData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(itemId);
        parcel.writeString(title);

        final Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
        parcel.writeParcelable(bitmap, i);
    }

    public int getItemId() {
        return itemId;
    }
    public String getTitle() {
        return title;
    }
    public Drawable getIcon() {
        return icon;
    }

    public boolean isEnabled() {
        return isEnabled;
    }
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
