package ru.ystu.myystu.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
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
    private ConstraintLayout weekLayout;
    private ConstraintLayout lessonLayout;
    private ConstraintLayout timeLayout;

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

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        update();

        if(savedInstanceState == null){
            if (getArguments() != null) {
                update = getArguments().getStringArrayList("update");
            }
            formattingList();
        } else {
            mList = savedInstanceState.getParcelableArrayList("mList");
            mRecyclerState = savedInstanceState.getParcelable("recyclerViewState");
            mLayoutManager.onRestoreInstanceState(mRecyclerState);
            mRecyclerViewAdapter = new BellItemsAdapter(mList, mContext);
            mRecyclerView.setAdapter(mRecyclerViewAdapter);

            new ItemTouchHelper(simpleCallback).attachToRecyclerView(mRecyclerView);
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

            mRecyclerView = mView.findViewById(R.id.recycler_bell_items);

            mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                    R.color.colorPrimary);

            mSwipeRefreshLayout.setOnRefreshListener(this::update);
            mSwipeRefreshLayout.setEnabled(false);

            mLayoutManager = new LinearLayoutManager(mContext);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext,
                    DividerItemDecoration.VERTICAL));
        }

        return mView;
    }

    @Override
    public void onAttach(Context mContext) {
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

        mRecyclerState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable("recyclerViewState", mRecyclerState);
        outState.putParcelableArrayList("mList", mList);
    }

    private void update(){

        title.setText(bellHelper.getHalfYear());

        final String week = bellHelper.getCountWeek();
        final String lesson = bellHelper.getCountLesson();
        final String time = bellHelper.getTime();
        Spannable text;

        if(!week.equals("-")){
            text = new SpannableString(week + " " + getResources().getString(R.string.bell_text_week));
            text.setSpan(new TextAppearanceSpan(mContext, R.style.BellCountStyle), 0, week.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            text.setSpan(new TextAppearanceSpan(mContext, R.style.BellTextStyle), week.length() + 1, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            countWeek.setText(text);
            weekLayout.setVisibility(View.VISIBLE);
        } else {
            weekLayout.setVisibility(View.GONE);
        }

        if(!lesson.equals("-")){
            // Надписи
            if(lesson.length() > 2){
                text = new SpannableString(lesson);
                text.setSpan(new TextAppearanceSpan(mContext, R.style.BellTextOtherStyle), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            // Счетчик
            else {
                text = new SpannableString(lesson + " " + getResources().getString(R.string.bell_text_lesson));
                text.setSpan(new TextAppearanceSpan(mContext, R.style.BellCountStyle), 0, lesson.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                text.setSpan(new TextAppearanceSpan(mContext, R.style.BellTextStyle), lesson.length() + 1, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            countLesson.setText(text);
            lessonLayout.setVisibility(View.VISIBLE);
        } else {
            lessonLayout.setVisibility(View.GONE);
        }

        if(!time.equals("-")){
            text = new SpannableString(time + " " + getResources().getString(R.string.bell_text_time));
            text.setSpan(new TextAppearanceSpan(mContext, R.style.BellCountStyle), 0, time.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            text.setSpan(new TextAppearanceSpan(mContext, R.style.BellTextStyle), time.length() + 1, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            countTime.setText(text);
            timeLayout.setVisibility(View.VISIBLE);
        } else {
            timeLayout.setVisibility(View.GONE);
        }
    }

    private void formattingList() {

        mList = new ArrayList<>();
        final String[] prefix = new String[]{"АСФ", "ИЭФ", "АФ", "МСФ", "ХТФ", "ЗФ", "ОУОП ЗФ"};

        for(int i = 0; i < update.size(); i++){

            String temp = update.get(i);
            String[] var = new String[3];
            for(int v = 0; v < var.length; v++){
                var[v] = temp.substring(0, temp.indexOf("*"));
                temp = temp.substring(temp.indexOf("*") + 1);
            }

            final int idType = Integer.parseInt(var[0]);
            final int idSubType = Integer.parseInt(var[1]);
            final String link = var[2];

            String date = null;
            String text = null;
            if(!temp.equals("")){
                date = temp.substring(0, temp.indexOf(":"));
                text = temp.substring(temp.indexOf(":") + 2);
            }

            String title = null;
            // Обновлено расписание
            if(idType == 0){
                title = getResources().getString(R.string.bell_item_title_schedule) + " " + prefix[idSubType];
            }

            mList.add(new BellItemsData(i, idType, idSubType, 0, title, text, date));
        }

        mRecyclerViewAdapter = new BellItemsAdapter(mList, mContext);
        mRecyclerViewAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(mRecyclerView);
    }

    private ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int pos = viewHolder.getAdapterPosition();
            ((BellItemsAdapter) mRecyclerViewAdapter).removeItem(pos);
            ((MainActivity) Objects.requireNonNull(getActivity())).removeItemUpdate(pos);
            ((MainActivity) getActivity()).badgeChange(mRecyclerViewAdapter.getItemCount());
            mRecyclerViewAdapter.notifyItemRemoved(pos);

        }
    };
}
