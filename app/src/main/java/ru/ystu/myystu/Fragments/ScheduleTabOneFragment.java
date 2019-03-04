package ru.ystu.myystu.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.Adapters.ScheduleMenuItemsAdapter;
import ru.ystu.myystu.AdaptersData.ScheduleMenuItemsData;
import ru.ystu.myystu.R;

public class ScheduleTabOneFragment extends Fragment {

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

        mList.add(new ScheduleMenuItemsData(0, R.drawable.ic_schedule_asf, R.color.colorAsf, getResources().getString(R.string.schedule_item_asf)));
        mList.add(new ScheduleMenuItemsData(1, R.drawable.ic_schedule_ief, R.color.colorIef, getResources().getString(R.string.schedule_item_ief)));
        mList.add(new ScheduleMenuItemsData(2, R.drawable.ic_schedule_af, R.color.colorAf, getResources().getString(R.string.schedule_item_af)));
        mList.add(new ScheduleMenuItemsData(3, R.drawable.ic_schedule_mf, R.color.colorMf, getResources().getString(R.string.schedule_item_mf)));
        mList.add(new ScheduleMenuItemsData(4, R.drawable.ic_schedule_htf, R.color.colorHtf, getResources().getString(R.string.schedule_item_htf)));

        mRecyclerViewAdapter = new ScheduleMenuItemsAdapter(mList, getContext());
        mRecyclerViewAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View mView = inflater.inflate(R.layout.fragment_schedule_tab_one, container, false);
        mRecyclerView = mView.findViewById(R.id.recycler_schedule_one);
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
