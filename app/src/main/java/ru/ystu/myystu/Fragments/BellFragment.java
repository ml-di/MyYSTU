package ru.ystu.myystu.Fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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

    private Context mContext;

    BellHelper bellHelper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mContext = getActivity();
        bellHelper = new BellHelper(mContext);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimary);

        mSwipeRefreshLayout.setOnRefreshListener(this::update);
        mSwipeRefreshLayout.setEnabled(false);
        update();
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

    private void update(){

        title.setText(bellHelper.getHalfYear());

        final String week = bellHelper.getCountWeek();
        final String lesson = bellHelper.getCountLesson();
        Spannable text;

        if(!week.equals("-")){
            text = new SpannableString(week + " нед.");
            text.setSpan(new TextAppearanceSpan(mContext, R.style.BellCountStyle), 0, week.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            text.setSpan(new TextAppearanceSpan(mContext, R.style.BellTextStyle), week.length() + 1, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            countWeek.setText(text);
            weekLayout.setVisibility(View.VISIBLE);
        } else {
            weekLayout.setVisibility(View.GONE);
        }

        if(!lesson.equals("-")){
            if(lesson.length() > 2){
                // TODO надписи в другом шрифте
                text = new SpannableString(lesson);
            } else {
                text = new SpannableString(lesson + " пара");
                text.setSpan(new TextAppearanceSpan(mContext, R.style.BellCountStyle), 0, lesson.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                text.setSpan(new TextAppearanceSpan(mContext, R.style.BellTextStyle), lesson.length() + 1, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            countLesson.setText(text);
            lessonLayout.setVisibility(View.VISIBLE);
        } else {
            lessonLayout.setVisibility(View.GONE);
        }
    }
}
