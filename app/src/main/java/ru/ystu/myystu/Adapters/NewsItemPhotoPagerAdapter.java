package ru.ystu.myystu.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.ArrayList;
import ru.ystu.myystu.R;

public class NewsItemPhotoPagerAdapter extends PagerAdapter {

    private final ArrayList<String> photoUrls;
    private final Context mContext;

    public NewsItemPhotoPagerAdapter(final ArrayList<String> photoUrls,
                                     final Context mContext) {
        this.photoUrls = photoUrls;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        final LayoutInflater inflater = LayoutInflater.from(mContext);
        final ViewGroup view = (ViewGroup) inflater.inflate(R.layout.viewpager_news_photo_item, container, false);
        final SimpleDraweeView image = view.findViewById(R.id.test_photo);

        image.setImageURI(photoUrls.get(position));

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return photoUrls.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
