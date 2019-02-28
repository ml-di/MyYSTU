package ru.ystu.myystu.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.relex.photodraweeview.PhotoDraweeView;
import ru.ystu.myystu.Network.LoadImageFromURL;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Adapters.NewsPhotoViewPagerAdapter;
import ru.ystu.myystu.AdaptersData.NewsItemsPhotoData;
import ru.ystu.myystu.Utils.FileInformation;
import ru.ystu.myystu.Utils.MultiTouchViewPager;
import ru.ystu.myystu.Utils.NetworkInformation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;

public class ViewPhotoActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ArrayList<Parcelable> mList;
    private int position;
    private String[] imageUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);

        final Toolbar toolBar = findViewById(R.id.toolBar_photoView);
        setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolBar.setNavigationOnClickListener(view -> onBackPressed());

        final Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            if(bundle.getParcelableArrayList("list") != null)
                mList = bundle.getParcelableArrayList("list");
            if(bundle.getInt("position") != -1)
                position = bundle.getInt("position");

            if(mList != null){
                imageUrls = new String[mList.size()];

                for(int i = 0; i < mList.size(); i++)
                    imageUrls[i] = ((NewsItemsPhotoData)mList.get(i)).getUrlFull();

                toolBar.setTitle(position + 1 + " | " + imageUrls.length);

                viewPager = (MultiTouchViewPager) findViewById(R.id.view_pager_news_photo);
                final NewsPhotoViewPagerAdapter adapter = new NewsPhotoViewPagerAdapter(this, imageUrls);
                viewPager.setAdapter(adapter);

                viewPager.setCurrentItem(position);
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        toolBar.setTitle(position + 1 + " | " + imageUrls.length);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String url = imageUrls[viewPager.getCurrentItem()];

        switch (item.getItemId()){
            // Сохранить изображение на устройство
            case R.id.menu_photo_view_save:

                if(NetworkInformation.hasConnection(this)){
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    } else {

                        final LoadImageFromURL loadImageFromURL = new LoadImageFromURL();
                        final Observable<Boolean> observableLoadImage = loadImageFromURL.getObservableImage(url, this);
                        final Observer<Boolean> observer = new Observer<Boolean>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Boolean aBoolean) {

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

                        observableLoadImage
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(observer);
                    }
                }

                break;
            // Отправить ссылку на изображение
            case R.id.menu_photo_view_share_link:

                final Intent shareLink = new Intent(Intent.ACTION_SEND)
                        .putExtra(Intent.EXTRA_TEXT, url)
                        .setType("text/plain");;
                startActivity(shareLink);

                break;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "Разрешение успешно получено, повторите действие", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }
}
