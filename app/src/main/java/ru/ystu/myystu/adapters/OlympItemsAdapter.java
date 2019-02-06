package ru.ystu.myystu.adapters;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.R;
import ru.ystu.myystu.adaptersData.OlympItemsData;

public class OlympItemsAdapter extends RecyclerView.Adapter<OlympItemsAdapter.OlympItemsViewHolder> {

    private List<OlympItemsData> mList;
    private Context context;

    class  OlympItemsViewHolder extends RecyclerView.ViewHolder{

        private int id;
        private AppCompatTextView title;
        private AppCompatTextView text;

        OlympItemsViewHolder(View itemView, List<OlympItemsData> mList, Context context) {
            super(itemView);

            title = itemView.findViewById(R.id.itemOlymp_title);
            text = itemView.findViewById(R.id.itemOlymp_text);

        }
    }

    public OlympItemsAdapter(List<OlympItemsData> mList, Context context) {
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
    public OlympItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_olymp_item, parent, false);
        return new OlympItemsViewHolder(v, mList, context);
    }

    @Override
    public void onBindViewHolder(@NonNull OlympItemsViewHolder holder, int position) {
        holder.id = mList.get(position).getId();
        holder.title.setText(mList.get(position).getTitle());
        //holder.text.setText(mList.get(position).getText());

        holder.text.setText(Html.fromHtml(mList.get(position).getText()));
        holder.text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}
