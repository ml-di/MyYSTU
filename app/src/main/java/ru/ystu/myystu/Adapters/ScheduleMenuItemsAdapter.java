package ru.ystu.myystu.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.Activitys.ScheduleListActivity;
import ru.ystu.myystu.AdaptersData.ScheduleMenuItemsData;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.SettingsController;

public class ScheduleMenuItemsAdapter extends RecyclerView.Adapter<ScheduleMenuItemsAdapter.ScheduleMenuItemsViewHolder> {

    private ArrayList<ScheduleMenuItemsData> mList;
    private Context mContext;

    static class ScheduleMenuItemsViewHolder extends RecyclerView.ViewHolder {

        private int id;
        private AppCompatImageView icon;
        private AppCompatTextView text;
        private AppCompatImageView fab;
        private ConstraintLayout item;
        private ConstraintLayout background;

        ScheduleMenuItemsViewHolder(@NonNull View itemView, final Context mContext) {
            super(itemView);

            icon = itemView.findViewById(R.id.schedule_itemMenu_icon);
            text = itemView.findViewById(R.id.schedule_itemMenu_text);
            fab = itemView.findViewById(R.id.schedule_itemMenu_fab);
            item = itemView.findViewById(R.id.schedule_itemMenu);
            background = itemView.findViewById(R.id.schedule_itemMenu_background);

            item.setOnClickListener(view -> {
                final Intent mIntent = new Intent(mContext, ScheduleListActivity.class);
                mIntent.putExtra("ID", id);
                mContext.startActivity(mIntent);
                if (SettingsController.isEnabledAnim(mContext)) {
                    ((Activity)mContext).overridePendingTransition(R.anim.activity_slide_right_show, R.anim.activity_slide_left_out);
                }else {
                    ((Activity)mContext).overridePendingTransition(0, 0);
                }

            });
        }
    }

    public ScheduleMenuItemsAdapter(ArrayList<ScheduleMenuItemsData> mList, Context mContext) {
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
    public ScheduleMenuItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_schedule_item_menu, parent, false);
        return new ScheduleMenuItemsViewHolder(v, mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleMenuItemsViewHolder holder, int position) {

        holder.id = mList.get(position).getId();
        holder.icon.setImageResource(mList.get(position).getIcon());
        holder.text.setText(mList.get(position).getText());

        final int color = mContext.getResources().getColor(mList.get(position).getColor());

        holder.fab.setColorFilter(color);
        holder.icon.setColorFilter(color);
        holder.text.setTextColor(color);
        holder.background.setBackgroundTintList(ContextCompat.getColorStateList(mContext, mList.get(position).getColor()));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).hashCode();
    }
}
