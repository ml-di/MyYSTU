package ru.ystu.myystu.Utils;

import android.content.Context;
import android.net.Uri;

import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

public class FrescoHelper {

    public static ImageRequest getImageRequest (Context mContext, String url) {

        if (url != null && Uri.parse(url) != null) {
            if (SettingsController.isImageRAMCache(mContext)) {
                return ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                        .setProgressiveRenderingEnabled(true)
                        .build();
            } else {
                return ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                        .disableMemoryCache()
                        .setProgressiveRenderingEnabled(true)
                        .build();
            }
        } else
            return null;
    }
}
