package ru.ystu.myystu.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.ystu.myystu.Network.GetFullEventFromURL;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.ErrorMessage;
import ru.ystu.myystu.Utils.NetworkInformation;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;

public class EventFullActivity extends AppCompatActivity {

    Context mContext;

    private String titleStr;
    private String url;
    private String urlPhoto;
    private String dateStr;
    private String locationStr;

    private SimpleDraweeView image;
    private AppCompatTextView date;
    private AppCompatTextView location;
    private AppCompatTextView title;
    private AppCompatTextView locationTitle;
    private AppCompatTextView text;

    private ConstraintLayout mainLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    GetFullEventFromURL getFullEventFromURL;
    CompositeDisposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_full);

        mContext = this;

        image = findViewById(R.id.eventFull_image);
        date = findViewById(R.id.eventFull_date);
        location = findViewById(R.id.eventFull_location);
        title = findViewById(R.id.eventFull_title);
        locationTitle = findViewById(R.id.eventFull_locationTitle);
        text = findViewById(R.id.eventFull_text);
        mainLayout = findViewById(R.id.eventItem_mainLayout);
        mSwipeRefreshLayout = findViewById(R.id.refresh_eventFull);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this::getEvent);

        if(getIntent().getExtras() != null) {
            titleStr = getIntent().getExtras().getString("title");
            url = getIntent().getExtras().getString("url");
            urlPhoto = getIntent().getExtras().getString("urlPhoto");
            dateStr = getIntent().getExtras().getString("date");
            locationStr = getIntent().getExtras().getString("location");
        }

        final Toolbar mToolbar = findViewById(R.id.toolBar_eventFull);
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());

        image.setImageURI(urlPhoto);
        date.setText(dateStr);
        title.setText(titleStr);

        if(locationStr.equals("")) {
            location.setVisibility(View.GONE);
            locationTitle.setVisibility(View.GONE);
        } else {
            location.setText(locationStr);
        }

        getFullEventFromURL = new GetFullEventFromURL();
        mDisposable = new CompositeDisposable();

        getEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mDisposable.dispose();
    }

    private void getEvent() {

        mSwipeRefreshLayout.setRefreshing(true);

        if(NetworkInformation.hasConnection(this)){

            final Observable<String> mObservable = getFullEventFromURL.getObservableEventFull(url);
            mDisposable.add(mObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String s) {
                            Spanned html = Html.fromHtml(s);
                            text.setText(html);
                        }

                        @Override
                        public void onComplete() {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            if(e.getMessage().equals("Not found")){
                                ErrorMessage.show(mainLayout, 1,
                                        getResources().getString(R.string.error_message_schedule_file_not_found),
                                        mContext);
                            } else
                                ErrorMessage.show(mainLayout, -1, e.getMessage(), mContext);
                        }
                    }));

        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            ErrorMessage.show(mainLayout, 0, null, mContext);
        }

    }
}
