package ru.ystu.myystu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.R;
import ru.ystu.myystu.adaptersData.NewsItemsData;

public class NewsItemsAdapter extends RecyclerView.Adapter<NewsItemsAdapter.NewsItemsViewHolder> {

    private List<NewsItemsData> mList;
    private Context context;

    static class NewsItemsViewHolder extends RecyclerView.ViewHolder{

        private int id;
        private int isPinned;
        private AppCompatTextView postDate;
        private AppCompatTextView postText;
        private AppCompatTextView postPinned;

        public NewsItemsViewHolder(@NonNull View itemView, final List<NewsItemsData> mList, final Context context) {
            super(itemView);

            postDate = itemView.findViewById(R.id.post_date);
            postPinned = itemView.findViewById(R.id.post_pinned);
            postText = itemView.findViewById(R.id.post_text);
        }
    }

    public NewsItemsAdapter(List<NewsItemsData> mList, Context context) {
        this.mList = mList;
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        context = recyclerView.getContext();
    }

    @NonNull
    @Override
    public NewsItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_news_item, parent, false);
        return new NewsItemsViewHolder(v, mList, context);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsItemsViewHolder holder, int position) {
        holder.postText.setText(mList.get(position).getText());
        holder.postDate.setText(mList.get(position).getDate());

        if(Objects.equals(mList.get(position).getIsPinned(), 1))
            holder.postPinned.setText("Запись закреплена");
        else
            holder.postPinned.setText("");
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
