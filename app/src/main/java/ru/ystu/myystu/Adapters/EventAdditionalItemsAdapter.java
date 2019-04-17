package ru.ystu.myystu.Adapters;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.AdaptersData.EventAdditionalData_Additional;
import ru.ystu.myystu.AdaptersData.EventAdditionalData_Documents;
import ru.ystu.myystu.R;

public class EventAdditionalItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_ADDITIONAL = 0;
    private static final int ITEM_DOCUMENT = 1;
    // TODO титл для доков как разделитетль

    private List<Parcelable> mList;
    private Context mContext;

    static class AdditionalViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView title;
        private AppCompatTextView description;

        AdditionalViewHolder (View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.itemEventAdditional_title);
            description = itemView.findViewById(R.id.itemEventAdditional_description);

        }

        void setAdditional (EventAdditionalData_Additional additionalItem, Context mContext) {
            title.setText(additionalItem.getTitle());
            description.setText(additionalItem.getDescription());
        }
    }

    static class DocumentViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView name;
        private AppCompatTextView ext;

        DocumentViewHolder (View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.itemEventDocument_name);
            ext = itemView.findViewById(R.id.itemEventDocument_fileType);

        }

        void setDocument (EventAdditionalData_Documents documentItem, Context mContext) {
            name.setText(documentItem.getTitle());
            ext.setText(documentItem.getExt());
        }
    }

    public EventAdditionalItemsAdapter (List<Parcelable> mList, Context mContext) {
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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder mViewHolder;

        switch (viewType) {
            case ITEM_ADDITIONAL:
                final View viewAdditional= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_event_additional_item, parent, false);
                mViewHolder = new AdditionalViewHolder(viewAdditional);
                break;

            case ITEM_DOCUMENT:
                final View viewDocument = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_event_additional_item_document, parent, false);
                mViewHolder = new DocumentViewHolder(viewDocument);
                break;

            default:
                mViewHolder = null;
                break;
        }

        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        int viewType = holder.getItemViewType();
        switch (viewType) {
            case ITEM_ADDITIONAL:
                final EventAdditionalData_Additional additional = (EventAdditionalData_Additional) mList.get(position);
                ((AdditionalViewHolder) holder).setAdditional(additional, mContext);
                break;
            case ITEM_DOCUMENT:
                final EventAdditionalData_Documents document = (EventAdditionalData_Documents) mList.get(position);
                ((DocumentViewHolder) holder).setDocument(document, mContext);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;

        if (mList.get(position) instanceof EventAdditionalData_Additional) {
            viewType = ITEM_ADDITIONAL;
        } else if (mList.get(position) instanceof EventAdditionalData_Documents) {
            viewType = ITEM_DOCUMENT;
        } else{
            viewType = -1;
        }

        return viewType;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).hashCode();
    }
}
