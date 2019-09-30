package ru.ystu.myystu.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import ru.ystu.myystu.Adapters.NewsItemPhotoPagerAdapter;
import ru.ystu.myystu.R;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        final ArrayList<String> photoLinks = new ArrayList<>();
        photoLinks.add("https://sun9-63.userapi.com/c858024/v858024079/70f93/Gu3tSNRw7bs.jpg");
        photoLinks.add("https://sun9-2.userapi.com/c858024/v858024079/70f78/VieNv2Bu37c.jpg");
        photoLinks.add("https://sun9-11.userapi.com/c858024/v858024079/70f81/2sW4HQJo3qM.jpg");
        photoLinks.add("https://sun9-20.userapi.com/c858024/v858024079/70f8a/L0VvYd0SD8k.jpg");
        photoLinks.add("https://sun9-70.userapi.com/c858024/v858024079/70f9c/EgHCKFweEmY.jpg");
        photoLinks.add("https://sun9-56.userapi.com/c855120/v855120079/eefdc/gNI2wgcCWTo.jpg");
        photoLinks.add("https://sun9-52.userapi.com/c850524/v850524633/1b5968/5FDhSXya-ns.jpg");
        photoLinks.add("https://sun9-31.userapi.com/c850524/v850524633/1b5972/olo2dvog5D8.jpg");
        photoLinks.add("https://sun9-7.userapi.com/c852136/v852136633/1bec15/D8NKwCNn2-I.jpg");

        final ViewPager pager = findViewById(R.id.test_viewPager);
        final TabLayout pagerIndicator = findViewById(R.id.test_viewPagerIndicator);
        NewsItemPhotoPagerAdapter adapter = new NewsItemPhotoPagerAdapter(photoLinks, this);
        pager.setAdapter(adapter);
        pagerIndicator.setupWithViewPager(pager);

        for (int i = 0; i < pagerIndicator.getTabCount(); i++) {
            pagerIndicator.getTabAt(i).setCustomView(adapter.getTabView(i ,pagerIndicator.getTabAt(i).isSelected()));
        }

        pagerIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                adapter.tabSelected(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                adapter.tabUnselected(tab);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
