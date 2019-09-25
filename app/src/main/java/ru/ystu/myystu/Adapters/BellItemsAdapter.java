package ru.ystu.myystu.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import ru.ystu.myystu.Activitys.EventActivity;
import ru.ystu.myystu.Activitys.JobActivity;
import ru.ystu.myystu.Activitys.MainActivity;
import ru.ystu.myystu.AdaptersData.UpdateData;
import ru.ystu.myystu.Fragments.BellFragment;
import ru.ystu.myystu.R;

public class BellItemsAdapter extends RecyclerView.Adapter<BellItemsAdapter.BellItemsViewHolder> {

    private List<UpdateData> mList;
    private Context mContext;

    static class BellItemsViewHolder extends RecyclerView.ViewHolder {

        final private AppCompatTextView type;
        final private AppCompatTextView text;
        final private AppCompatTextView bandage;
        final private AppCompatImageView icon;
        final private ConstraintLayout item;

        BellItemsViewHolder(@NonNull View itemView, final List<UpdateData> mList, final Context mContext) {
            super(itemView);

            type = itemView.findViewById(R.id.itemBell_type);
            text = itemView.findViewById(R.id.itemBell_text);
            bandage = itemView.findViewById(R.id.itemBell_bandage);
            icon = itemView.findViewById(R.id.itemBell_icon);
            item = itemView.findViewById(R.id.itemBell);

            item.setOnClickListener(view -> {
                final int position = getAdapterPosition();
                final String type = mList.get(position).getType();
                Intent mIntent = null;
                switch (type){
                    // Расписание
                    case "EVENT":
                        mIntent = new Intent(mContext, EventActivity.class);
                        break;

                    case "JOB":
                        mIntent = new Intent(mContext, JobActivity.class);
                        break;
                }
                if (mIntent != null) {
                    mIntent.putExtra("isUpdate", true);
                    mContext.startActivity(mIntent);
                    ((BellFragment) ((MainActivity) mContext).getmBellFragment()).removeItem(position);
                }
            });
        }
    }

    public BellItemsAdapter(List<UpdateData> mList, Context mContext) {
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

        if (mList.get(position).getType().equals("EVENT")) {
            holder.type.setText(mContext.getResources().getString(R.string.activity_event_title));
            holder.text.setText(mContext.getResources().getString(R.string.bell_update_event_text));
            holder.icon.setImageResource(R.drawable.ic_event);
        } else if (mList.get(position).getType().equals("JOB")) {
            holder.type.setText(mContext.getResources().getString(R.string.activity_job_title));
            holder.text.setText(mContext.getResources().getString(R.string.bell_update_job_text));
            holder.icon.setImageResource(R.drawable.ic_job);
        }

        holder.bandage.setText(String.valueOf(mList.get(position).getCount()));
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
}
