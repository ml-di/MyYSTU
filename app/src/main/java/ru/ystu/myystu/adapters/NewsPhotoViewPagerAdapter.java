package ru.ystu.myystu.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import me.relex.photodraweeview.PhotoDraweeView;

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

        PhotoDraweeView photoDraweeView = new PhotoDraweeView(context);
        photoDraweeView.setPhotoUri(Uri.parse(imageUrls[position]));

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
