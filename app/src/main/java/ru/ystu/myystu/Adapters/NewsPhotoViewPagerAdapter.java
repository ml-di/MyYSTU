package ru.ystu.myystu.Adapters;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import me.relex.photodraweeview.PhotoDraweeView;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Network.FrescoLoadProgressBar;

public class NewsPhotoViewPagerAdapter extends PagerAdapter {

    private Context context;
    private String[] imageUrls;

    public NewsPhotoViewPagerAdapter(Context context, String[] imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        // Загрузка фото
        final PhotoDraweeView photoDraweeView = new PhotoDraweeView(context);
        PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
        controller.setUri(Uri.parse(imageUrls[position]));
        controller.setOldController(photoDraweeView.getController());
        controller.setControllerListener(new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                if (imageInfo == null) {
                    return;
                }
                photoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
            }
        });
        photoDraweeView.setController(controller.build());

        // Ставим progressbar
        final FrescoLoadProgressBar progressBar = new FrescoLoadProgressBar();
        progressBar.setColor(context.getResources().getColor(R.color.colorAccent));
        progressBar.setBackgroundColor(context.getResources().getColor(R.color.colorTextPrimary));
        photoDraweeView.getHierarchy().setProgressBarImage(progressBar);

        container.addView(photoDraweeView);

        return photoDraweeView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return imageUrls.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
