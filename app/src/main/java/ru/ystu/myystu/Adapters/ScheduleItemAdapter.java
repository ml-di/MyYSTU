package ru.ystu.myystu.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.AdaptersData.ScheduleListItemData;
import ru.ystu.myystu.R;

public class ScheduleItemAdapter extends RecyclerView.Adapter<ScheduleItemAdapter.ScheduleItemViewHolder> {

    private ArrayList<ScheduleListItemData> mList;
    private Context mContext;

    static class ScheduleItemViewHolder extends RecyclerView.ViewHolder {

        private int id;
        private AppCompatTextView text;
        private ConstraintLayout item;

        ScheduleItemViewHolder(@NonNull View itemView, final ArrayList<ScheduleListItemData> mList, final Context mContext) {
            super(itemView);

            text = itemView.findViewById(R.id.schedule_item_text);
            item = itemView.findViewById(R.id.schedule_item);

            item.setOnClickListener(view -> {

            });
        }
    }

    public ScheduleItemAdapter(ArrayList<ScheduleListItemData> mList, Context mContext) {
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
    public ScheduleItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_schedule_item, parent, false);
        return new ScheduleItemViewHolder(v, mList, mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleItemViewHolder holder, int position) {
        holder.text.setText(mList.get(position).getName());
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
