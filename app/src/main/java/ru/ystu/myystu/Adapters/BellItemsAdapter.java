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
import ru.ystu.myystu.Activitys.MainActivity;
import ru.ystu.myystu.Activitys.ScheduleListActivity;
import ru.ystu.myystu.AdaptersData.UpdateData;
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

            /*item.setOnClickListener(view -> {

                switch (idType){
                    // Расписание
                    case 0:

                        final int position = getAdapterPosition();
                        final Intent mIntent = new Intent(mContext, ScheduleListActivity.class);
                        mIntent.putExtra("ID", idSubType);
                        mContext.startActivity(mIntent);

                        // TODO Удаление элемента из BellFragment
                        /*
                        *   Обновить БД
                        *

                        mList.remove(position);
                        ((MainActivity) mContext).badgeChange(mList.size());

                        break;
                }
            });*/
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

        // TODO заолнить итем обновления

        if (mList.get(position).getType().equals("EVENT")) {
            holder.type.setText("События");
            holder.text.setText("Обновлен раздел с событиями");
            holder.icon.setImageResource(R.drawable.ic_bell);
        } else if (mList.get(position).getType().equals("JOB")) {
            holder.type.setText("Вакансии");
            holder.text.setText("Добавлены новые вакансии");
            holder.icon.setImageResource(R.drawable.ic_bell);
        }

        // TODO int to String
        holder.bandage.setText(mList.get(position).getCount());
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
