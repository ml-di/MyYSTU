package ru.ystu.myystu.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.Adapters.ScheduleMenuItemsAdapter;
import ru.ystu.myystu.AdaptersData.ScheduleMenuItemsData;
import ru.ystu.myystu.R;

public class ScheduleTabTwoFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ScheduleMenuItemsData> mList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mList.add(new ScheduleMenuItemsData(5, R.drawable.ic_schedule_zf, R.color.colorPrimary, getResources().getString(R.string.schedule_item_zf)));
        mList.add(new ScheduleMenuItemsData(6, R.drawable.ic_schedule_zf, R.color.colorPrimary, getResources().getString(R.string.schedule_item_ozf)));

        mRecyclerViewAdapter = new ScheduleMenuItemsAdapter(mList, getContext());
        mRecyclerViewAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View mView = inflater.inflate(R.layout.fragment_schedule_tab_two, container, false);
        mRecyclerView = mView.findViewById(R.id.recycler_schedule_two);
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
}
