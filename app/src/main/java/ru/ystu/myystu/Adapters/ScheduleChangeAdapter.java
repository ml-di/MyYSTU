package ru.ystu.myystu.Adapters;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.AdaptersData.ScheduleChangeData;
import ru.ystu.myystu.AdaptersData.ToolbarPlaceholderData;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.StringFormatter;

public class ScheduleChangeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_TOOLBAR_PLACEHOLDER = 0;
    private static final int ITEM_CHANGE = 1;

    private ArrayList<Parcelable> mList;

    static class PlaceholderViewHolder extends RecyclerView.ViewHolder {

        PlaceholderViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void setPlaceholder (ToolbarPlaceholderData placeholderItem) {

        }
    }

    static class ScheduleChangeViewHolder extends RecyclerView.ViewHolder{

        private AppCompatTextView date;
        private AppCompatTextView text;

        ScheduleChangeViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.itemChange_date);
            text = itemView.findViewById(R.id.itemChange_text);
        }

        void setScheduleChange (ScheduleChangeData changeItem) {
            date.setText(changeItem.getDate());
            text.setText(new StringFormatter().groupFormated(changeItem.getText()));
        }
    }

    public ScheduleChangeAdapter(ArrayList<Parcelable> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder mViewHolder;

        switch (viewType) {
            case ITEM_TOOLBAR_PLACEHOLDER:
                final View viewPlaceholder = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_toolbar_placeholder, parent, false);
                mViewHolder = new PlaceholderViewHolder(viewPlaceholder);
                break;

            case ITEM_CHANGE:
                final View viewChange = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_schedule_item_change, parent, false);
                mViewHolder = new ScheduleChangeViewHolder(viewChange);
                break;

            default:
                mViewHolder = null;
                break;
        }

        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        int viewType = holder.getItemViewType();
        switch (viewType) {
            case ITEM_TOOLBAR_PLACEHOLDER:
                final ToolbarPlaceholderData placeholder = (ToolbarPlaceholderData) mList.get(position);
                ((PlaceholderViewHolder) holder).setPlaceholder(placeholder);
                break;
            case ITEM_CHANGE:
                final ScheduleChangeData change = (ScheduleChangeData) mList.get(position);
                ((ScheduleChangeViewHolder) holder).setScheduleChange(change);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;

        if (mList.get(position) instanceof ToolbarPlaceholderData) {
            viewType = ITEM_TOOLBAR_PLACEHOLDER;
        } else if (mList.get(position) instanceof ScheduleChangeData) {
            viewType = ITEM_CHANGE;
        } else {
            viewType = -1;
        }

        return viewType;
    }
}
