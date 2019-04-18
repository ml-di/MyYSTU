package ru.ystu.myystu.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.ystu.myystu.Adapters.EventAdditionalItemsAdapter;
import ru.ystu.myystu.AdaptersData.EventAdditionalData_Additional;
import ru.ystu.myystu.AdaptersData.EventAdditionalData_Documents;
import ru.ystu.myystu.AdaptersData.EventItemsData_Divider;
import ru.ystu.myystu.Network.GetFullEventFromURL;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.ErrorMessage;
import ru.ystu.myystu.Utils.NetworkInformation;
import ru.ystu.myystu.Utils.StringFormatter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Objects;

public class EventFullActivity extends AppCompatActivity {

    Context mContext;
    StringFormatter stringFormatter;

    private String titleStr;
    private String url;
    private String urlPhoto;
    private String dateStr;
    private String locationStr;

    private String textTemp;

    private SimpleDraweeView image;
    private AppCompatTextView date;
    private AppCompatTextView location;
    private AppCompatTextView title;
    private AppCompatTextView locationTitle;
    private AppCompatTextView text;
    private AppCompatTextView titleText;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NestedScrollView scroll;
    private ConstraintLayout mainLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mRecyclerViewAdapter;

    private GetFullEventFromURL getFullEventFromURL;
    private CompositeDisposable mDisposable;

    private ArrayList<Parcelable> additionalsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_full);

        mContext = this;
        stringFormatter = new StringFormatter();

        image = findViewById(R.id.eventFull_image);
        date = findViewById(R.id.eventFull_date);
        location = findViewById(R.id.eventFull_location);
        title = findViewById(R.id.eventFull_title);
        locationTitle = findViewById(R.id.eventFull_locationTitle);
        text = findViewById(R.id.eventFull_text);
        titleText = findViewById(R.id.eventFull_titleText);
        mSwipeRefreshLayout = findViewById(R.id.refresh_eventFull);
        scroll = findViewById(R.id.eventFull_scroll);
        mainLayout = findViewById(R.id.eventFull_mainLayout);

        mLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mRecyclerView = findViewById(R.id.recycler_eventAdditional_items);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

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
        mToolbar.setOnClickListener(e -> scroll.smoothScrollTo(0, 0));
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.activity_event_title);

        image.setImageURI(urlPhoto);
        date.setText(dateStr);
        title.setText(titleStr);

        if(locationStr.equals("")) {
            location.setVisibility(View.GONE);
            locationTitle.setVisibility(View.GONE);
        } else {
            location.setText(locationStr);
        }

        if(savedInstanceState == null) {
            getEvent();
        } else {

            titleText.setText(savedInstanceState.getString("subTitle"));

            textTemp = savedInstanceState.getString("text");
            Spanned spanText = Html.fromHtml(textTemp);
            spanText = stringFormatter.getFormattedString(spanText.toString());
            text.setText(spanText);
            text.setMovementMethod(LinkMovementMethod.getInstance());

            additionalsList = savedInstanceState.getParcelableArrayList("aList");
            mRecyclerViewAdapter = new EventAdditionalItemsAdapter(additionalsList, mContext);
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mDisposable != null) {
            mDisposable.dispose();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()){
            overridePendingTransition(R.anim.activity_slide_right_show_reverse, R.anim.activity_slide_left_out_reverse);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("position", scroll.getScrollY());
        outState.putString("subTitle", (String) titleText.getText());
        outState.putString("text", textTemp);
        outState.putString("url", url);
        outState.putParcelableArrayList("aList", additionalsList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        scroll.smoothScrollTo(0, savedInstanceState.getInt("position", 0));
        url = savedInstanceState.getString("url");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_full, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_event_openInBrowser:
                final Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(mIntent);
                return true;

            case R.id.menu_event_copyText:
                final ClipboardManager mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                final String clipText = textTemp
                        .replaceAll("<br>", "\n")
                        .replaceAll("&nbsp;", " ")
                        .replaceAll("<a href=\"", "")
                        .replaceAll("\">", "")
                        .replaceAll("</a>", "");

                final ClipData mClipData = ClipData.newPlainText("event_text", clipText);
                mClipboardManager.setPrimaryClip(mClipData);

                Toast.makeText(this, getResources().getString(R.string.toast_isCopyText), Toast.LENGTH_SHORT).show();
                return true;

            case R.id.menu_event_shareLink:
                final Intent shareLink = new Intent(Intent.ACTION_SEND)
                        .putExtra(Intent.EXTRA_TEXT, titleStr + "\n\n" + url)
                        .setType("text/plain");
                startActivity(shareLink);
                return true;
        }

        return false;
    }

    private void getEvent() {

        getFullEventFromURL = new GetFullEventFromURL();
        mDisposable = new CompositeDisposable();
        mSwipeRefreshLayout.setRefreshing(true);

        if(additionalsList == null)
            additionalsList = new ArrayList<>();
        else
            additionalsList.clear();

        if(NetworkInformation.hasConnection(this)){

            final Observable<String> mObservable = getFullEventFromURL.getObservableEventFull(url);
            mDisposable.add(mObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String s) {

                            if(additionalsList.size() == 0) {
                                additionalsList.add(
                                        new EventItemsData_Divider(mContext
                                                .getResources()
                                                .getString(R.string.activity_eventFull_sitebar_title)));
                            }

                            if (s.startsWith("title: ")) {

                                titleText.setText(s.substring(s.indexOf(": ") + 2));

                            } else if (s.startsWith("cont: ")) {

                                textTemp = s.substring(s.indexOf(": ") + 2);
                                Spanned spanText = Html.fromHtml(textTemp);
                                spanText = stringFormatter.getFormattedString(spanText.toString());
                                text.setText(spanText);
                                text.setMovementMethod(LinkMovementMethod.getInstance());

                            } else if (s.startsWith("addit: ")) {

                                final String title = s.substring(s.indexOf(": ") + 2, s.indexOf("*"));
                                final String description = s.substring(s.indexOf("*") + 1);
                                additionalsList.add(new EventAdditionalData_Additional(title, description));

                            } else if (s.startsWith("doc_title: ")) {

                                additionalsList.add(new EventItemsData_Divider(s.substring(s.indexOf(": ") + 2)));

                            } else if (s.startsWith("doc_file: ")) {

                                final String name = s.substring(s.indexOf(": ") + 2, s.indexOf("*"));
                                final String link = s.substring(s.indexOf("*") + 1, s.indexOf("`"));
                                final String info = s.substring(s.indexOf("`") + 1);
                                final String ext = info.substring(0, info.indexOf(", "));
                                final String size = info.substring(info.indexOf(", ") + 2);

                                additionalsList.add(new EventAdditionalData_Documents(name, link, ext, size));
                            }
                        }

                        @Override
                        public void onComplete() {
                            mRecyclerViewAdapter = new EventAdditionalItemsAdapter(additionalsList, mContext);
                            mRecyclerView.setAdapter(mRecyclerViewAdapter);
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
