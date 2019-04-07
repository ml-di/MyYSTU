package ru.ystu.myystu.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.Adapters.ScheduleChangeAdapter;
import ru.ystu.myystu.AdaptersData.ScheduleChangeData;
import ru.ystu.myystu.R;
import android.os.Bundle;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;

public class ScheduleChangeActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ScheduleChangeData> mList;
    private Parcelable mRecyclerState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_change);

        final Toolbar mToolbar = findViewById(R.id.toolBar_schedule_change);
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());

        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView = findViewById(R.id.recycler_schdeule_change);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if(getIntent().getExtras() != null){
            ArrayList<String> tempList = getIntent().getExtras().getStringArrayList("mList");
            mList = new ArrayList<>();
            for (String temp : tempList) {

                final String date = temp.substring(0, temp.lastIndexOf(": "));
                final String text = temp.substring(temp.lastIndexOf(": ") + 2);

                mList.add(new ScheduleChangeData(date, text));
            }

            Collections.reverse(mList);

            mRecyclerViewAdapter = new ScheduleChangeAdapter(mList, getApplicationContext());
            mRecyclerViewAdapter.setHasStableIds(true);
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mRecyclerState != null)
            mLayoutManager.onRestoreInstanceState(mRecyclerState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        mRecyclerState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable("recyclerViewState", mRecyclerState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mRecyclerState = savedInstanceState.getParcelable("recyclerViewState");
    }
}
