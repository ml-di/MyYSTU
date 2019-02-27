package ru.ystu.myystu.DataFragments;

/*
 *  Данный фрагмент используется для сохранения состояния списка новостей,
 *  так как его размер может превышать 1024 кб
 */

import android.os.Bundle;
import android.os.Parcelable;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DataFragment_News_List extends Fragment {

    private ArrayList<Parcelable> mList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    public void setList(ArrayList<Parcelable> mList){
        this.mList = mList;
    }

    public ArrayList<Parcelable> getList(){
        return mList;
    }
}
