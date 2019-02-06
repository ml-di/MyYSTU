package ru.ystu.myystu;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.ContentFrameLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.adapters.MenuItemsAdapter;
import ru.ystu.myystu.adaptersData.MenuItemsData;

public class MenuFragment extends Fragment {

    //region переменные
    private View mView;
    private OnFragmentInteractionListener mListener;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;

    private List<MenuItemsData> mList;

    private ContentFrameLayout frameItems;
    //endregion

    public static MenuFragment newInstance(String param1, String param2) {

        return new MenuFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onStart() {
        super.onStart();

        if(mList.size() > 0)
            mList.clear();

        //region добавление пунктов меню
        mList.add(new MenuItemsData(0, R.drawable.ic_schedule, getResources().getString(R.string.menu_text_schedule)));
        mList.add(new MenuItemsData(1, R.drawable.ic_map, getResources().getString(R.string.menu_text_map)));
        mList.add(new MenuItemsData(2, R.drawable.ic_olymp, getResources().getString(R.string.menu_text_olymp)));
        mList.add(new MenuItemsData(3, R.drawable.ic_job, getResources().getString(R.string.menu_text_job)));
        mList.add(new MenuItemsData(4, R.drawable.ic_chat, getResources().getString(R.string.menu_text_chat)));
        mList.add(new MenuItemsData(5, R.drawable.ic_settings, getResources().getString(R.string.menu_text_settings)));
        //endregion

        //region Подгон размера под количество итемов recyclerview
        ViewGroup.LayoutParams params = frameItems.getLayoutParams();

        int maxViewItem;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            maxViewItem = 5;    // Макс количество видимых пунктов меню в альбомной ориентации
        else
            maxViewItem = 6;    // Макс количество видимых пунктов меню в портретной ориентации

        if(mList.size() > maxViewItem)
            params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, maxViewItem * 50, getResources().getDisplayMetrics());
        else
            params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mList.size() * 50, getResources().getDisplayMetrics());

        params.width = ViewGroup.LayoutParams.MATCH_PARENT;

        frameItems.setLayoutParams(params);
        //endregion

        mRecyclerViewAdapter = new MenuItemsAdapter(mList, getContext());
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        this.mView = view;

        //region инициализация объектов

        if(mView != null){
            mRecyclerView = mView.findViewById(R.id.recycler_menu_items);
            frameItems = mView.findViewById(R.id.menu_frame_items);
        }

        //endregion

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mList = new ArrayList<>();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
