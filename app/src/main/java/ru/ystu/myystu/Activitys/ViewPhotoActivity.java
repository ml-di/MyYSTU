package ru.ystu.myystu.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.ystu.myystu.Network.LoadImageFromURL;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Adapters.NewsPhotoViewPagerAdapter;
import ru.ystu.myystu.Utils.IntentHelper;
import ru.ystu.myystu.Utils.MultiTouchViewPager;
import ru.ystu.myystu.Utils.NetworkInformation;
import ru.ystu.myystu.Utils.SettingsController;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import java.util.ArrayList;

public class ViewPhotoActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private ArrayList<String> photoUrlList;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);

        final Toolbar mToolBar = findViewById(R.id.toolBar_photoView);
        setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        mToolBar.setNavigationOnClickListener(view -> onBackPressed());

        final Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            if(bundle.getParcelableArrayList("list") != null)
                photoUrlList = bundle.getStringArrayList("list");
            if(bundle.getInt("position") != -1)
                position = bundle.getInt("position");

            if(photoUrlList != null){

                if (photoUrlList.size() > 1)
                    mToolBar.setTitle(position + 1 + " " + getResources().getString(R.string.other_of) + " " + photoUrlList.size());

                mViewPager = (MultiTouchViewPager) findViewById(R.id.view_pager_news_photo);
                final NewsPhotoViewPagerAdapter adapter = new NewsPhotoViewPagerAdapter(this, photoUrlList);
                mViewPager.setAdapter(adapter);

                mViewPager.setCurrentItem(position);
                mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        mToolBar.setTitle(position + 1 + " " + getResources().getString(R.string.other_of) + " " + photoUrlList.size());
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing() && !SettingsController.isEnabledAnim(this)) {
            overridePendingTransition(0, 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String url = photoUrlList.get(mViewPager.getCurrentItem());

        switch (item.getItemId()){
            // Сохранить изображение на устройство
            case R.id.menu_photo_view_save:

                if(NetworkInformation.hasConnection()){
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    } else {

                        final LoadImageFromURL loadImageFromURL = new LoadImageFromURL();
                        final Completable mCompletableLoadImage = loadImageFromURL.getCompletableImage(url, this);
                        final CompletableObserver mObserver = new CompletableObserver() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(ViewPhotoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onComplete() {
                                Toast.makeText(ViewPhotoActivity.this, getResources().getString(R.string.photo_view_image_successfully_save), Toast.LENGTH_SHORT).show();
                            }
                        };

                        mCompletableLoadImage
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(mObserver);
                    }
                }

                break;
            // Отправить ссылку на изображение
            case R.id.menu_photo_view_share_link:
                IntentHelper.shareText(this, url);
                break;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Разрешение успешно получено, повторите действие", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
