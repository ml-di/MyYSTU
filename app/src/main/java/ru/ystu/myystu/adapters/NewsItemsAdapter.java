package ru.ystu.myystu.adapters;

import android.content.Context;
import android.os.Parcelable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.R;
import ru.ystu.myystu.adaptersData.NewsItemsData_DontAttach;
import ru.ystu.myystu.adaptersData.NewsItemsData_Header;
import ru.ystu.myystu.utils.StringFormatter;
import ru.ystu.myystu.utils.UnixToString;

public class NewsItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_HEADER = 0;
    private static final int ITEM_DONT_ATTACH = 1;

    private ArrayList<Parcelable> mList;
    private Context context;
    private StringFormatter stringFormatter = new StringFormatter();

    static class HeaderViewHolder extends RecyclerView.ViewHolder{

        private int id;
        private AppCompatTextView headerText;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);

            headerText = itemView.findViewById(R.id.news_item_header_text);
        }

        void setHeader(NewsItemsData_Header header){

            headerText.setText(header.getText());

        }
    }

    static class DontAttachViewHolder extends RecyclerView.ViewHolder{

        private int id;
        private int isPinned;
        private AppCompatTextView postDate;
        private AppCompatTextView postText;
        private AppCompatTextView postPinned;


        private UnixToString unixToString = new UnixToString();

        DontAttachViewHolder(@NonNull View itemView) {
            super(itemView);

            postDate = itemView.findViewById(R.id.post_date);
            postPinned = itemView.findViewById(R.id.post_pinned);
            postText = itemView.findViewById(R.id.post_text);
        }

        void setDontAttach(NewsItemsData_DontAttach dontAttach, StringFormatter stringFormatter){

            postText.setText(stringFormatter.getFormattedString(dontAttach.getText()));
            postText.setMovementMethod(LinkMovementMethod.getInstance());
            postDate.setText(unixToString.setUnixToString(dontAttach.getDate()));

            if(Objects.equals(dontAttach.getIsPinned(), 1))
                postPinned.setText("Запись закреплена");
            else
                postPinned.setText("");
        }
    }

    public NewsItemsAdapter(ArrayList<Parcelable> mList, Context context) {
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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case ITEM_HEADER:
                View viewHeader = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_news_item_header, parent, false);
                viewHolder = new HeaderViewHolder(viewHeader);
            break;

            case ITEM_DONT_ATTACH:
                View viewDontAttach = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_news_item_dont_attach, parent, false);
                viewHolder = new DontAttachViewHolder(viewDontAttach);
            break;

            default:
                viewHolder = null;
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        int viewType = holder.getItemViewType();
        switch (viewType) {
            case ITEM_HEADER:
                NewsItemsData_Header header = (NewsItemsData_Header)mList.get(position);
                ((HeaderViewHolder) holder).setHeader(header);
                break;
            case ITEM_DONT_ATTACH:
                NewsItemsData_DontAttach dontAttach = (NewsItemsData_DontAttach) mList.get(position);
                ((DontAttachViewHolder) holder).setDontAttach(dontAttach, stringFormatter);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {

        int viewType;

        if (mList.get(position) instanceof NewsItemsData_Header) {
            viewType = ITEM_HEADER;
        } else if (mList.get(position) instanceof NewsItemsData_DontAttach) {
            viewType = ITEM_DONT_ATTACH;
        } else{
            viewType = -1;
        }

        return viewType;
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
