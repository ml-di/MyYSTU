package ru.ystu.myystu.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;
import ru.ystu.myystu.Activitys.ViewPhotoActivity;

public class PhotoViewSetter {

    static public void setPhoto(Context mContext, ArrayList<String> photoUrlList, int position){
        startPhotoView(mContext, photoUrlList, position);
    }

    static public void setPhoto(Context mContext, String photoUrl){
        final ArrayList<String> photoUrlList = new ArrayList<>();
        photoUrlList.add(photoUrl);
        startPhotoView(mContext, photoUrlList, 0);
    }

    static private void startPhotoView(Context mContext, ArrayList<String> photoUrlList, int position) {
        /*final Intent mIntent = new Intent(mContext, ViewPhotoActivity.class);
        mIntent.putExtra("position", position);
        mIntent.putExtra("list", photoUrlList);
        mContext.startActivity(mIntent);
        if (!SettingsController.isEnabledAnim(mContext)) {
            ((Activity) mContext).overridePendingTransition(0, 0);
        }*/

        new ImageViewer.Builder<>(mContext, photoUrlList)
                .setStartPosition(position)
                .hideStatusBar(false)
                .allowZooming(true)
                .allowSwipeToDismiss(true)
                //.setImageChangeListener(imageChangeListener)
                //.setOverlayView(overlayView)
                .show();
    }
}
