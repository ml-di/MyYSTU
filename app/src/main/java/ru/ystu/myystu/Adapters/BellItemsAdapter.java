package ru.ystu.myystu.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.AdaptersData.BellItemsData;
import ru.ystu.myystu.R;

public class BellItemsAdapter extends RecyclerView.Adapter<BellItemsAdapter.BellItemsViewHolder> {

    private List<BellItemsData> mList;
    private Context mContext;

    static class BellItemsViewHolder extends RecyclerView.ViewHolder {

        private int id;
        final private AppCompatTextView date;
        final private AppCompatTextView title;
        final private AppCompatTextView subTitle;

        public BellItemsViewHolder(@NonNull View itemView, final List<BellItemsData> mList, final Context mContext) {
            super(itemView);

            date = itemView.findViewById(R.id.itemBell_date);
            title = itemView.findViewById(R.id.itemBell_title);
            subTitle = itemView.findViewById(R.id.itemBell_subTitle);
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

        holder.id = mList.get(position).getId();
        holder.title.setText(mList.get(position).getTitle());
        holder.subTitle.setText(mList.get(position).getSubTitle());

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
