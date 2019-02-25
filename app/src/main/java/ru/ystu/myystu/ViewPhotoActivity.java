package ru.ystu.myystu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import ru.ystu.myystu.adapters.NewsPhotoViewPagerAdapter;
import ru.ystu.myystu.adaptersData.NewsItemsPhotoData;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import java.util.ArrayList;

public class ViewPhotoActivity extends AppCompatActivity {

    private AppCompatTextView newsPhotoViewCount;

    private ArrayList<Parcelable> mList;
    private int position;
    private String[] imageUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);

        newsPhotoViewCount = findViewById(R.id.newsPhotoViewCount);

        Bundle bundle = getIntent().getExtras();

        int asd = 0;

        if(bundle != null){
            if(bundle.getParcelableArrayList("list") != null)
                mList = bundle.getParcelableArrayList("list");
            if(bundle.getInt("position") != -1)
                position = bundle.getInt("position");

            if(mList != null){
                imageUrls = new String[mList.size()];

                for(int i = 0; i < mList.size(); i++)
                    imageUrls[i] = ((NewsItemsPhotoData)mList.get(i)).getUrlFull();

                ViewPager viewPager = findViewById(R.id.view_pager_news_photo);
                NewsPhotoViewPagerAdapter adapter = new NewsPhotoViewPagerAdapter(this, imageUrls);
                viewPager.setAdapter(adapter);

                viewPager.setCurrentItem(position);
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        newsPhotoViewCount.setText(position + 1 + " | " + imageUrls.length);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

                newsPhotoViewCount.setText(position + 1 + " | " + imageUrls.length);
            }
        }
    }
}
