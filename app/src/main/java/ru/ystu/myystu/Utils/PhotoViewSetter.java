package ru.ystu.myystu.Utils;

import android.content.Context;
import com.stfalcon.frescoimageviewer.ImageViewer;
import java.util.ArrayList;
import ru.ystu.myystu.R;

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

        final ImageOverlayView imageOverlayView = new ImageOverlayView(mContext);
        if (photoUrlList.size() > 1) {
            imageOverlayView.setToolbarTitle(position + 1 + " " + mContext.getResources().getString(R.string.other_of) + " " + photoUrlList.size());
        }

        final ImageViewer imageViewer = new ImageViewer.Builder<>(mContext, photoUrlList)
                .setStartPosition(position)
                .hideStatusBar(false)
                .setOverlayView(imageOverlayView)
                .allowZooming(true)
                .allowSwipeToDismiss(true)
                .setImageChangeListener(pos -> {
                    imageOverlayView.setToolbarTitle(pos + 1 + " " + mContext.getResources().getString(R.string.other_of) + " " + photoUrlList.size());
                    imageOverlayView.setUrl(photoUrlList.get(pos));
                })
                .show();

        imageOverlayView.setImageViewer(imageViewer);

    }
}
