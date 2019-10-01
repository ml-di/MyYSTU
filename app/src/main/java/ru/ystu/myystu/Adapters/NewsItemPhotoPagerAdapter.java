package ru.ystu.myystu.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.Objects;
import ru.ystu.myystu.AdaptersData.NewsItemsPhotoData;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.FrescoHelper;
import ru.ystu.myystu.Utils.PhotoViewSetter;
import ru.ystu.myystu.Utils.SettingsController;

public class NewsItemPhotoPagerAdapter extends PagerAdapter {

    private final static int ANIMATION_DURATION = 400;

    private final ArrayList<NewsItemsPhotoData> photoUrls;
    private final Context mContext;

    NewsItemPhotoPagerAdapter(final ArrayList<NewsItemsPhotoData> photoUrls,
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

        if (SettingsController.isImageDownload(mContext)) {
            image.setImageRequest(FrescoHelper.getImageRequest(mContext, photoUrls.get(position).getUrlPreview()));

            final ArrayList<String> photoUrlList = new ArrayList<>();
            for (int i = 0; i < photoUrls.size(); i++) {
                photoUrlList.add(photoUrls.get(i).getUrlFull());
            }

            image.setOnClickListener(v -> PhotoViewSetter.setPhoto(mContext, photoUrlList, position));
        }

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

    View getTabView(int position, boolean isSelected) {

        final View view = LayoutInflater.from(mContext).inflate(R.layout.viewpager_news_indicator_item, null);
        final SimpleDraweeView tabImage = view.findViewById(R.id.post_viewPagerIndicator_item_icon);
        tabImage.setImageRequest(FrescoHelper.getImageRequest(mContext, photoUrls.get(position).getUrlSmall()));

        if (isSelected) {
            final AlphaAnimation animationAlpha = new AlphaAnimation(1f, 0.5f);
            animationAlpha.setDuration(ANIMATION_DURATION);
            animationAlpha.setFillAfter(true);
            tabImage.startAnimation(animationAlpha);
        }

        return view;
    }

    void tabSelected(TabLayout.Tab tab) {
        if (tab.getCustomView() != null && tab.getCustomView().findViewById(R.id.post_viewPagerIndicator_item_icon) != null) {
            final SimpleDraweeView tabImage = tab.getCustomView().findViewById(R.id.post_viewPagerIndicator_item_icon);
            final AlphaAnimation animationAlpha = new AlphaAnimation(1f, 0.5f);
            animationAlpha.setDuration(ANIMATION_DURATION);
            animationAlpha.setFillAfter(true);
            tabImage.startAnimation(animationAlpha);
        }
    }

    void tabUnselected(TabLayout.Tab tab) {
        if (tab.getCustomView() != null && tab.getCustomView().findViewById(R.id.post_viewPagerIndicator_item_icon) != null) {
            final SimpleDraweeView tabImage = Objects.requireNonNull(tab.getCustomView()).findViewById(R.id.post_viewPagerIndicator_item_icon);
            final AlphaAnimation animationAlpha = new AlphaAnimation(0.5f, 1f);
            animationAlpha.setDuration(ANIMATION_DURATION);
            animationAlpha.setFillAfter(true);
            tabImage.startAnimation(animationAlpha);
        }
    }
}
