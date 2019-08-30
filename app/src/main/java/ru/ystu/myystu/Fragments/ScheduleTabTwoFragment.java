package ru.ystu.myystu.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
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
    private ArrayList<ScheduleMenuItemsData> mList = new ArrayList<>();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mList.add(new ScheduleMenuItemsData(6, R.drawable.ic_schedule_zf, R.color.colorPrimary, getResources().getString(R.string.schedule_item_zf)));
        mList.add(new ScheduleMenuItemsData(7, R.drawable.ic_schedule_zf, R.color.colorPrimary, getResources().getString(R.string.schedule_item_ozf)));

        RecyclerView.Adapter mRecyclerViewAdapter = new ScheduleMenuItemsAdapter(mList, getContext());
        mRecyclerViewAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View mView = inflater.inflate(R.layout.fragment_schedule_tab_two, container, false);
        mRecyclerView = mView.findViewById(R.id.recycler_schedule_two);
        return mView;
    }
}
