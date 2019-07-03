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
import ru.ystu.myystu.Application;
import ru.ystu.myystu.Database.AppDatabase;
import ru.ystu.myystu.Database.Data.EventFullData;
import ru.ystu.myystu.Database.Data.EventFullDivider;
import ru.ystu.myystu.Network.GetFullEventFromURL;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.Converter;
import ru.ystu.myystu.Utils.ErrorMessage;
import ru.ystu.myystu.Utils.LightStatusBar;
import ru.ystu.myystu.Utils.NetworkInformation;
import ru.ystu.myystu.Utils.SettingsController;
import ru.ystu.myystu.Utils.StringFormatter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

public class EventFullActivity extends AppCompatActivity {

    Context mContext;
    StringFormatter stringFormatter;

    private int id;
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
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_full);

        LightStatusBar.setLight(true, this);
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
        mSwipeRefreshLayout.setProgressViewOffset(true, 0, (int) Converter.convertDpToPixel(70, mContext));

        if(getIntent().getExtras() != null) {
            id = getIntent().getExtras().getInt("id");
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

        if (db == null || !db.isOpen())
            db = Application.getInstance().getDatabase();

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

        if(mDisposable != null)
            mDisposable.dispose();

        if (db != null && db.isOpen())
            db.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()){
            if (SettingsController.isEnabledAnim(this)) {
                overridePendingTransition(R.anim.activity_slide_right_show_reverse, R.anim.activity_slide_left_out_reverse);
            } else {
                overridePendingTransition(0, 0);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("id", id);
        outState.putInt("position", scroll.getScrollY());
        outState.putString("subTitle", (String) titleText.getText());
        outState.putString("text", text.getText().toString());
        outState.putString("url", url);
        outState.putParcelableArrayList("aList", additionalsList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        scroll.smoothScrollTo(0, savedInstanceState.getInt("position", 0));
        url = savedInstanceState.getString("url");
        id = savedInstanceState.getInt("id");
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
                if (text != null && text.getText() != null && text.getText().length() > 0) {
                    final ClipboardManager mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    final String clipText = text.getText().toString()
                            .replaceAll("<br>", "\n")
                            .replaceAll("&nbsp;", " ")
                            .replaceAll("<a href=\"", "")
                            .replaceAll("\">", "")
                            .replaceAll("</a>", "");

                    final ClipData mClipData = ClipData.newPlainText("event_text", clipText);
                    mClipboardManager.setPrimaryClip(mClipData);

                    Toast.makeText(this, getResources().getString(R.string.toast_isCopyText), Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(this, getResources().getString(R.string.toast_dont_text), Toast.LENGTH_SHORT).show();

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

        final int[] index = {id * 100};

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
                                        new EventFullDivider(index[0], id,
                                                mContext.getResources()
                                                .getString(R.string.activity_eventFull_sitebar_title)));

                                index[0]++;
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
                                additionalsList.add(new EventAdditionalData_Additional(index[0], id, title, description));
                                index[0]++;

                            } else if (s.startsWith("doc_title: ")) {
                                additionalsList.add(new EventFullDivider(index[0], id, s.substring(s.indexOf(": ") + 2)));
                                index[0]++;

                            } else if (s.startsWith("doc_file: ")) {

                                final String name = s.substring(s.indexOf(": ") + 2, s.indexOf("*"));
                                final String link = s.substring(s.indexOf("*") + 1, s.indexOf("`"));
                                final String info = s.substring(s.indexOf("`") + 1);
                                final String ext = info.substring(0, info.indexOf(", "));
                                final String size = info.substring(info.indexOf(", ") + 2);

                                additionalsList.add(new EventAdditionalData_Documents(index[0], id, name, link, ext, size));
                                index[0]++;
                            }
                        }

                        @Override
                        public void onComplete() {
                            mRecyclerViewAdapter = new EventAdditionalItemsAdapter(additionalsList, mContext);
                            mRecyclerView.setAdapter(mRecyclerViewAdapter);
                            mSwipeRefreshLayout.setRefreshing(false);

                            try {
                                new Thread(() -> {
                                    if (db.isOpen()) {
                                        // Удаляем все записи, если они есть
                                        if (db.eventFullDao().isExistsGeneral(id))
                                            db.eventFullDao().deleteGeneral(id);
                                        if (db.eventFullDao().getCountAdditional(id) > 0)
                                            db.eventFullDao().deleteAllAdditional(id);
                                        if (db.eventFullDao().getCountDocuments(id) > 0)
                                            db.eventFullDao().deleteAllDocuments(id);
                                        if (db.eventFullDao().getCountDividers(id) > 0)
                                            db.eventFullDao().deleteAllDividers(id);

                                        final EventFullData eventFullData = new EventFullData();
                                        eventFullData.setId(id);
                                        eventFullData.setUid(id);
                                        eventFullData.setTitle(titleText.getText().toString());
                                        eventFullData.setText(text.getText().toString());
                                        db.eventFullDao().insertGeneral(eventFullData);

                                        // Добавляем новые записи
                                        for (Parcelable parcelable : additionalsList) {
                                            if (parcelable instanceof EventFullDivider) {
                                                db.eventFullDao().insertDividers((EventFullDivider) parcelable);
                                            } else if (parcelable instanceof EventAdditionalData_Additional) {
                                                db.eventFullDao().insertAdditional((EventAdditionalData_Additional) parcelable);
                                            } else if (parcelable instanceof EventAdditionalData_Documents) {
                                                db.eventFullDao().insertDocuments((EventAdditionalData_Documents) parcelable);
                                            }
                                        }
                                    }
                                }).start();
                            } catch (Exception e) {
                                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
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
            try {
                new Thread(() -> {

                    if (db.isOpen() && db.eventFullDao().isExistsGeneral(id)) {
                        final EventFullData eventFullData = db.eventFullDao().getGeneral(id);
                        titleText.setText(eventFullData.getTitle());

                        Spanned spanText = Html.fromHtml(eventFullData.getText());
                        spanText = stringFormatter.getFormattedString(spanText.toString());
                        text.setText(spanText);
                        text.setMovementMethod(LinkMovementMethod.getInstance());
                    }

                    final int pref = id * 100;
                    final int count = db.eventFullDao().getCountAdditional(id)
                            + db.eventFullDao().getCountDocuments(id)
                            + db.eventFullDao().getCountDividers(id);

                    if (count > 0) {
                        if (additionalsList.size() > 0)
                            additionalsList.clear();

                        for (int i = 0; i < count; i++) {
                            if (db.eventFullDao().isExistsAdditional(id, pref + i)) {
                                additionalsList.add(db.eventFullDao().getAdditionals(id, pref + i));
                            } else if (db.eventFullDao().isExistsDocuments(id, pref + i)) {
                                additionalsList.add(db.eventFullDao().getDocuments(id, pref + i));
                            } else if (db.eventFullDao().isExistsDividers(id, pref + i)) {
                                additionalsList.add(db.eventFullDao().getDividers(id, pref + i));
                            }
                        }

                        mRecyclerViewAdapter = new EventAdditionalItemsAdapter(additionalsList, this);
                        mRecyclerView.post(() -> {
                            mRecyclerView.setAdapter(mRecyclerViewAdapter);
                            // SnackBar с предупреждением об отсутствие интернета
                            final Snackbar snackbar = Snackbar
                                    .make(
                                            mainLayout,
                                            getResources().getString(R.string.toast_no_connection_the_internet),
                                            Snackbar.LENGTH_INDEFINITE)
                                    .setAction(
                                            getResources().getString(R.string.error_message_refresh),
                                            view -> {
                                                // Обновление данных
                                                getEvent();
                                            });

                            ((TextView)snackbar
                                    .getView()
                                    .findViewById(com.google.android.material.R.id.snackbar_text))
                                    .setTextColor(Color.BLACK);

                            snackbar.show();

                            mSwipeRefreshLayout.setRefreshing(false);
                        });

                    } else {
                        runOnUiThread(() -> {
                            ErrorMessage.show(mainLayout, 0, null, mContext);
                            mSwipeRefreshLayout.setRefreshing(false);
                        });
                    }
                }).start();
            } catch (Exception e) {
                ErrorMessage.show(mainLayout, -1, e.getMessage(), mContext);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }
}
