package ru.ystu.myystu.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.AdaptersData.ScheduleChangeData;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.StringFormatter;

public class ScheduleChangeAdapter extends RecyclerView.Adapter<ScheduleChangeAdapter.ScheduleChangeViewHolder> {

    private ArrayList<ScheduleChangeData> mList;
    private Context mContext;

    static class ScheduleChangeViewHolder extends RecyclerView.ViewHolder{

        private int id;
        private AppCompatTextView date;
        private AppCompatTextView text;

        ScheduleChangeViewHolder(@NonNull View itemView, final ArrayList<ScheduleChangeData> mList, final Context mContext) {
            super(itemView);

            date = itemView.findViewById(R.id.itemChange_date);
            text = itemView.findViewById(R.id.itemChange_text);

        }
    }

    public ScheduleChangeAdapter(ArrayList<ScheduleChangeData> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mContext = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ScheduleChangeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_schedule_item_change, parent, false);
        return new ScheduleChangeViewHolder(v, mList, mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleChangeViewHolder holder, int position) {

        holder.date.setText(mList.get(position).getDate());
        holder.text.setText(new StringFormatter().groupFormated(mList.get(position).getText()));

        holder.id = mList.get(position).getId();
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
        return super.getItemViewType(position);
    }
}
