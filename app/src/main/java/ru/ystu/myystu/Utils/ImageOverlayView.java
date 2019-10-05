package ru.ystu.myystu.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.stfalcon.frescoimageviewer.ImageViewer;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.ystu.myystu.Network.LoadImageFromURL;
import ru.ystu.myystu.R;

public class ImageOverlayView extends RelativeLayout {

    private ImageViewer imageViewer;
    private Toolbar toolbar;
    private String url;

    public ImageOverlayView(Context context) {
        super(context);

        final View view = inflate(getContext(), R.layout.activity_view_photo, this);
        toolbar = view.findViewById(R.id.toolBar_photoView);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(v -> {
            if (imageViewer != null) {
                imageViewer.onDismiss();
            }
        });
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                // Сохранить изображение на устройство
                case R.id.menu_photo_view_save:

                    if(NetworkInformation.hasConnection()){
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                        } else {
                            final LoadImageFromURL loadImageFromURL = new LoadImageFromURL();
                            final Completable mCompletableLoadImage = loadImageFromURL.getCompletableImage(url, context);
                            final CompletableObserver mObserver = new CompletableObserver() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onComplete() {
                                    Toast.makeText(context, getResources().getString(R.string.photo_view_image_successfully_save), Toast.LENGTH_SHORT).show();
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
                    IntentHelper.shareText(context, url);
                    break;
            }

            return true;
        });
    }

    public void setToolbarTitle (String title) {
        if (toolbar != null) {
            toolbar.setTitle(title);
        }
    }

    public void setUrl (String url) {
        this.url = url;
    }

    public void setImageViewer (ImageViewer imageViewer) {
        this.imageViewer = imageViewer;
    }
}
