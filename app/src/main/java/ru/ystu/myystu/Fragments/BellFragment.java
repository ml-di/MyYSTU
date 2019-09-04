package ru.ystu.myystu.Fragments;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.ContentFrameLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.Activitys.MainActivity;
import ru.ystu.myystu.Adapters.BellItemsAdapter;
import ru.ystu.myystu.AdaptersData.UpdateData;
import ru.ystu.myystu.Application;
import ru.ystu.myystu.Database.AppDatabase;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.BellHelper;

public class BellFragment extends Fragment {

    private AppCompatTextView title;
    private AppCompatTextView countWeek;
    private AppCompatTextView countLesson;
    private AppCompatTextView countTime;
    private ContentFrameLayout weekLayout;
    private ContentFrameLayout lessonLayout;
    private ContentFrameLayout timeLayout;
    private ContentFrameLayout mainLayout;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<UpdateData> mList;
    private Parcelable mRecyclerState;
    private Context mContext;

    private BellHelper bellHelper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);

        mContext = getActivity();
        bellHelper = new BellHelper(mContext);

        if(savedInstanceState == null) {
            ((MainActivity) Objects.requireNonNull(getActivity())).showBottomBar(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        ((MainActivity) Objects.requireNonNull(getActivity())).showBottomBar(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        update();

        if(savedInstanceState == null){
            formattingList();
        } else {
            mList = savedInstanceState.getParcelableArrayList("mList");

            if (mList != null && mList.size() > 0) {
                showPlaceHolder(false);
                mRecyclerState = savedInstanceState.getParcelable("recyclerViewState");
                if (mRecyclerState != null) {
                    mLayoutManager.onRestoreInstanceState(mRecyclerState);
                }
                mRecyclerViewAdapter = new BellItemsAdapter(mList, mContext);
                if (mRecyclerView != null) {
                    mRecyclerView.setAdapter(mRecyclerViewAdapter);
                    new ItemTouchHelper(simpleCallback).attachToRecyclerView(mRecyclerView);
                } else {
                    showPlaceHolder(true);
                }
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View mView = inflater.inflate(R.layout.fragment_bell, container, false);

        if(mView != null){
            title = mView.findViewById(R.id.bell_title);
            countWeek = mView.findViewById(R.id.bell_count_week);
            countLesson = mView.findViewById(R.id.bell_count_lesson);
            countTime = mView.findViewById(R.id.bell_count_time);

            weekLayout = mView.findViewById(R.id.week_layout);
            lessonLayout = mView.findViewById(R.id.lesson_layout);
            timeLayout = mView.findViewById(R.id.time_layout);
            mainLayout = mView.findViewById(R.id.main_layout_bell);
        }

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mRecyclerState != null)
            mLayoutManager.onRestoreInstanceState(mRecyclerState);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if(mLayoutManager != null) {
            mRecyclerState = mLayoutManager.onSaveInstanceState();
        }

        outState.putParcelable("recyclerViewState", mRecyclerState);
        outState.putParcelableArrayList("mList", mList);
    }

    private void update(){

        title.setText(bellHelper.getHalfYear());

        final String week = bellHelper.getCountWeek();
        final String lesson = bellHelper.getCountLesson();
        final String time = bellHelper.getTime();

        if(!week.equals("-")){
            countWeek.setText(week);
            weekLayout.setVisibility(View.VISIBLE);
        } else {
            weekLayout.setVisibility(View.GONE);
        }

        if(!lesson.equals("-")){
            countLesson.setText(lesson);
            lessonLayout.setVisibility(View.VISIBLE);
        } else {
            lessonLayout.setVisibility(View.GONE);
        }

        if(!time.equals("-")){
            countTime.setText(time);
            timeLayout.setVisibility(View.VISIBLE);
        } else {
            timeLayout.setVisibility(View.GONE);
        }
    }

    private void formattingList() {
        mList = ((MainActivity) Objects.requireNonNull(getActivity())).getUpdateList();
        if(mList.size() > 0){
            showPlaceHolder(false);
            mRecyclerViewAdapter = new BellItemsAdapter(mList, mContext);
            mRecyclerViewAdapter.setHasStableIds(true);
            mRecyclerView.setAdapter(mRecyclerViewAdapter);

            new ItemTouchHelper(simpleCallback).attachToRecyclerView(mRecyclerView);
        } else {
            showPlaceHolder(true);
        }
    }

    private void showPlaceHolder(boolean isShow) {

        final DisplayMetrics metrics = new DisplayMetrics();
        Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay().getMetrics(metrics);

        mainLayout.removeAllViews();
        ViewGroup.LayoutParams params;

        if(isShow) {
            final AppCompatTextView placeHolder = new AppCompatTextView(Objects.requireNonNull(getContext()));
            placeHolder.setText(getResources().getString(R.string.bell_placeholder_text));
            params = new ContentFrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            ((FrameLayout.LayoutParams) params).gravity = Gravity.CENTER;
            placeHolder.setTextSize(20);
            placeHolder.setTextColor(getResources().getColor(R.color.colorTextBlack));
            placeHolder.setAlpha(0.5f);

            placeHolder.setLayoutParams(params);
            mainLayout.addView(placeHolder, 0 , params);

        } else {
            params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mRecyclerView = new RecyclerView(mainLayout.getContext());
            mRecyclerView.setBackgroundColor(getResources().getColor(R.color.colorBackgroundBell));
            mainLayout.addView(mRecyclerView, 0 , params);

            mLayoutManager = new LinearLayoutManager(mContext);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(mLayoutManager);
        }
    }

    private ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int pos = viewHolder.getAdapterPosition();
            new Thread(() -> {
                try {
                    AppDatabase db = Application.getInstance().getDatabase();
                    if (db.getOpenHelper().getWritableDatabase().isOpen()) {
                        if (mList.size() > 0 && db.countersDao().isExistsCounter(mList.get(pos).getType())) {
                            db.countersDao().setCount(mList.get(pos).getType(), mList.get(pos).getCountItem());
                        }
                    }
                } catch (SQLiteException e) {
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show());
                }
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> removeItem(pos));
            }).start();
        }
    };

    public void removeItem (int pos) {
        ((BellItemsAdapter) mRecyclerViewAdapter).removeItem(pos);

        final int sizeBandage = mRecyclerViewAdapter.getItemCount();
        ((MainActivity) Objects.requireNonNull(getActivity())).badgeChange(sizeBandage);

        mRecyclerViewAdapter.notifyItemRemoved(pos);
        ((MainActivity) getActivity()).countUpdateDecrement();

        if(mRecyclerViewAdapter.getItemCount() <= 0){
            showPlaceHolder(true);
        }
    }
}
