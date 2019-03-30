package ru.ystu.myystu.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.Activitys.MainActivity;
import ru.ystu.myystu.Activitys.ScheduleListActivity;
import ru.ystu.myystu.AdaptersData.BellItemsData;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.StringFormatter;

public class BellItemsAdapter extends RecyclerView.Adapter<BellItemsAdapter.BellItemsViewHolder> {

    private List<BellItemsData> mList;
    private Context mContext;

    static class BellItemsViewHolder extends RecyclerView.ViewHolder {

        private int idType;
        private int idSubType;
        final private AppCompatTextView date;
        final private AppCompatTextView title;
        final private AppCompatTextView subTitle;
        final private ConstraintLayout item;

        BellItemsViewHolder(@NonNull View itemView, final List<BellItemsData> mList, final Context mContext) {
            super(itemView);

            date = itemView.findViewById(R.id.itemBell_date);
            title = itemView.findViewById(R.id.itemBell_title);
            subTitle = itemView.findViewById(R.id.itemBell_subTitle);
            item = itemView.findViewById(R.id.itemBell);

            item.setOnClickListener(view -> {

                switch (idType){
                    // Расписание
                    case 0:
                        final int position = getAdapterPosition();
                        final String[] prefix = new String[]{"asf", "ief", "af", "mf", "htf", "zf", "ozf"};
                        final Intent mIntent = new Intent(mContext, ScheduleListActivity.class);
                        mIntent.putExtra("ID", idSubType);
                        mContext.startActivity(mIntent);

                        // Удаление элемента из BellFragment
                        final String link = mList.get(position).getLink();
                        final int idSub = (mList.get(position).getIdSubType());

                        final SharedPreferences mSharedPreferences = mContext.getSharedPreferences("SCHEDULE_UPDATE", Context.MODE_PRIVATE);
                        final SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                        mEditor.putString(prefix[idSub].toUpperCase(), link);
                        mEditor.apply();

                        mList.remove(position);
                        ((MainActivity) mContext).removeItemUpdate(position);
                        ((MainActivity) mContext).badgeChange(mList.size());

                        break;

                }

            });
        }
    }

    public BellItemsAdapter(List<BellItemsData> mList, Context mContext) {
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
    public BellItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_bell_item, parent, false);
        return new BellItemsViewHolder(mView, mList, mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull BellItemsViewHolder holder, int position) {

        holder.idType = mList.get(position).getIdType();
        holder.idSubType = mList.get(position).getIdSubType();

        holder.title.setText(mList.get(position).getTitle());

        if(mList.get(position).getSubTitle() != null){
            holder.subTitle.setText(new StringFormatter().groupFormated(mList.get(position).getSubTitle()));
        }

        if(mList.get(position).getDate() != null){
            String date = mList.get(position).getDate().substring(0, mList.get(position).getDate().indexOf(" ") + 4);
            if(date.length() == 5){
                date = "0" + date;
            }
            final Spannable spanDate = new SpannableString(date);
            spanDate.setSpan(new TextAppearanceSpan(mContext, R.style.BellItemCountStyle), 0, date.indexOf(" "), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanDate.setSpan(new TextAppearanceSpan(mContext, R.style.BellItemTextStyle), date.indexOf(" ") + 1, date.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.date.setText(spanDate);
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
        return super.getItemViewType(position);
    }

    public void removeItem (int position) {
        mList.remove(position);
    }
    public String getLink(int position) {
        return mList.get(position).getLink();
    }
    public int getSubId(int position) {
        return mList.get(position).getIdSubType();
    }

}
