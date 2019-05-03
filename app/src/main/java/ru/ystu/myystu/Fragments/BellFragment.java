package ru.ystu.myystu.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.ContentFrameLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import ru.ystu.myystu.Activitys.MainActivity;
import ru.ystu.myystu.Adapters.BellItemsAdapter;
import ru.ystu.myystu.AdaptersData.BellItemsData;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.BellHelper;

public class BellFragment extends Fragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
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
    private ArrayList<BellItemsData> mList;
    private Parcelable mRecyclerState;
    private Context mContext;

    private BellHelper bellHelper;

    private ArrayList<String> update;


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

        update = new ArrayList<>();
        update();

        // Получить значения уведомлений
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences("UPDATE_LIST", Context.MODE_PRIVATE);
        if(mSharedPreferences.getAll().size() > 0){

            for(Map.Entry<String, ?> entry : mSharedPreferences.getAll().entrySet()) {
                final int type = Integer.parseInt(entry.getKey().substring(0, 1));
                final int id = Integer.parseInt(entry.getKey().substring(1, 2));
                final String text = (String) entry.getValue();
                update.add(type + "" + id + "" + text);
            }
        }

        // Отрисовать RecyclerView при уведомлениях
        // или placeholder при их отсутствии
        if(mSharedPreferences.getAll().size() > 0){
            showPlaceHolder(false);
        } else {
            showPlaceHolder(true);
        }

        if(savedInstanceState == null){
            // Заполнить RecyclerView
            formattingList();
        } else {
            mList = savedInstanceState.getParcelableArrayList("mList");
            mRecyclerState = savedInstanceState.getParcelable("recyclerViewState");
            if (mRecyclerState != null) {
                mLayoutManager.onRestoreInstanceState(mRecyclerState);
            }
            mRecyclerViewAdapter = new BellItemsAdapter(mList, mContext);
            if (mRecyclerView != null) {
                mRecyclerView.setAdapter(mRecyclerViewAdapter);
                new ItemTouchHelper(simpleCallback).attachToRecyclerView(mRecyclerView);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View mView = inflater.inflate(R.layout.fragment_bell, container, false);

        if(mView != null){
            mSwipeRefreshLayout = mView.findViewById(R.id.refresh_bell);
            title = mView.findViewById(R.id.bell_title);
            countWeek = mView.findViewById(R.id.bell_count_week);
            countLesson = mView.findViewById(R.id.bell_count_lesson);
            countTime = mView.findViewById(R.id.bell_count_time);

            weekLayout = mView.findViewById(R.id.week_layout);
            lessonLayout = mView.findViewById(R.id.lesson_layout);
            timeLayout = mView.findViewById(R.id.time_layout);
            mainLayout = mView.findViewById(R.id.main_layout_bell);

            //mRecyclerView = mView.findViewById(R.id.recycler_bell_items);

            mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                    R.color.colorPrimary);

            mSwipeRefreshLayout.setOnRefreshListener(this::update);
            mSwipeRefreshLayout.setEnabled(false);
        }

        return mView;
    }

    @Override
    public void onAttach(@NonNull Context mContext) {
        super.onAttach(mContext);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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

        if(update.size() > 0){
            if(mList == null)
                mList = new ArrayList<>();
            else
                mList.clear();


            final String[] prefix = new String[]{"АСФ", "ИЭФ", "АФ", "МСФ", "ХТФ", "ЗФ", "ОУОП ЗФ"};

            for(int i = 0; i < update.size(); i++){

                String temp = update.get(i);
                final int idType = Integer.parseInt(temp.substring(0, 1));
                final int idSubType = Integer.parseInt(temp.substring(1, 2));
                final String date = temp.substring(2, temp.indexOf("*"));
                String subTitle = temp.substring(temp.indexOf("*") + 1);

                String title = null;
                // Обновлено расписание
                if(idType == 0){
                    title = getResources().getString(R.string.bell_item_title_schedule) + " " + prefix[idSubType];
                    if(subTitle.contains(":")) {
                        subTitle = subTitle.substring(subTitle.indexOf(":") + 2);
                    }
                }

                mList.add(new BellItemsData(idType, idSubType, title, subTitle, date, null));
            }

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
        final float logicalDensity = metrics.density;

        mainLayout.removeAllViews();
        ViewGroup.LayoutParams params;

        if(isShow) {
            final AppCompatTextView placeHolder = new AppCompatTextView(Objects.requireNonNull(getContext()));
            placeHolder.setText("Уведомлений нет");
            params = new ContentFrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            ((FrameLayout.LayoutParams) params).gravity = Gravity.CENTER;
            placeHolder.setTextSize(21);
            placeHolder.setAlpha(0.35f);
            placeHolder.setTypeface(placeHolder.getTypeface(), Typeface.BOLD);

            placeHolder.setLayoutParams(params);
            mainLayout.addView(placeHolder, 0 , params);

        } else {
            params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mRecyclerView = new RecyclerView(mainLayout.getContext());
            mainLayout.addView(mRecyclerView, 0 , params);

            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) Math.ceil(12 * logicalDensity));
            final FrameLayout mContentFrameLayout = new FrameLayout(mainLayout.getContext());
            mContentFrameLayout.setBackground(getResources().getDrawable(R.drawable.recyclerview_grandient));
            mainLayout.addView(mContentFrameLayout, 1 , params);

            mLayoutManager = new LinearLayoutManager(mContext);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext,
                    DividerItemDecoration.VERTICAL));
        }

    }

    public void updateRecycler(ArrayList<String> updateList){
        showPlaceHolder(false);
        update = updateList;
        formattingList();
    }

    private ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int pos = viewHolder.getAdapterPosition();

            final String[] prefix = new String[]{"asf", "ief", "af", "mf", "htf", "zf", "ozf"};

            final String link = ((BellItemsAdapter) mRecyclerViewAdapter).getLink(pos);
            final int id = ((BellItemsAdapter) mRecyclerViewAdapter).getSubId(pos);
            final int type = ((BellItemsAdapter) mRecyclerViewAdapter).getType(pos);

            final SharedPreferences mSharedPreferences = mContext.getSharedPreferences("UPDATE_LIST", Context.MODE_PRIVATE);
            final SharedPreferences.Editor mEditor = mSharedPreferences.edit();
            mEditor.remove(type + "" + id);
            mEditor.apply();

            ((BellItemsAdapter) mRecyclerViewAdapter).removeItem(pos);
            ((MainActivity) Objects.requireNonNull(getActivity())).badgeChange(mSharedPreferences.getAll().size());
            mRecyclerViewAdapter.notifyItemRemoved(pos);

            if(mRecyclerViewAdapter.getItemCount() <= 0){
                showPlaceHolder(true);
            }
        }
    };
}
