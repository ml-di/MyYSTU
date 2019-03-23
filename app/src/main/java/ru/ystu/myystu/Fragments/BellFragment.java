package ru.ystu.myystu.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
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
        countWeek.setText(bellHelper.getCountWeek());
        countLesson.setText(bellHelper.getCountLesson());
    }
}
