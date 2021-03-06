package ru.ystu.myystu.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.ystu.myystu.Activitys.SettingsActivity;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Adapters.MenuItemsAdapter;
import ru.ystu.myystu.AdaptersData.MenuItemsData;
import ru.ystu.myystu.Utils.SettingsController;

public class MenuFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ArrayList<MenuItemsData> mList;

    private AppCompatImageView menuBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);
    }

    @Override
    public void onStart() {
        super.onStart();

        if(mList.size() > 0)
            mList.clear();

        mList.add(new MenuItemsData(0, R.drawable.ic_schedule, getString(R.string.menu_text_schedule)));
        mList.add(new MenuItemsData(2, R.drawable.ic_event, getString(R.string.menu_text_event)));
        mList.add(new MenuItemsData(4, R.drawable.ic_user, getString(R.string.menu_text_users)));
        mList.add(new MenuItemsData(3, R.drawable.ic_job, getString(R.string.menu_text_job)));
        mList.add(new MenuItemsData(5, R.drawable.ic_document_text, getString(R.string.menu_text_documents)));
        mList.add(new MenuItemsData(1, R.drawable.ic_map, getString(R.string.menu_text_map)));


        final RecyclerView.Adapter mRecyclerViewAdapter = new MenuItemsAdapter(mList, getContext());
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        menuBtn.setOnClickListener(view -> {
            final Intent mIntent = new Intent(getContext(), SettingsActivity.class);
            getActivity().startActivity(mIntent);
            if (!SettingsController.isEnabledAnim(getContext())) {
                getActivity().overridePendingTransition(0, 0);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View mView = inflater.inflate(R.layout.fragment_menu, container, false);

        if(mView != null){
            mRecyclerView = mView.findViewById(R.id.recycler_menu_items);
            menuBtn = mView.findViewById(R.id.menu_settings);
        }

        mRecyclerView.setHasFixedSize(true);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }

        mList = new ArrayList<>();

        return mView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
