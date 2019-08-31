package ru.ystu.myystu.Adapters;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import ru.ystu.myystu.AdaptersData.StoItemsData_Doc;
import ru.ystu.myystu.AdaptersData.StoItemsData_Subtitle;
import ru.ystu.myystu.AdaptersData.StoItemsData_Title;
import ru.ystu.myystu.R;

public class StoItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int ITEM_TITLE = 0;
    private static final int ITEM_SUBTITLE = 1;
    private static final int ITEM_DOC = 2;

    private List<Parcelable> mList;
    private Context mContext;

    static class TitleViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView title;

        TitleViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.stoItem_title);
        }

        void setTitle (StoItemsData_Title titleItem) {
            title.setText(titleItem.getTitle());
        }
    }

    static class SubtitleViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView subtitle;

        SubtitleViewHolder(@NonNull View itemView) {
            super(itemView);

            subtitle = itemView.findViewById(R.id.stoItem_subTitle);
        }

        void setSubtitle (StoItemsData_Subtitle subtitleItem) {
            subtitle.setText(subtitleItem.getSubtitle());
        }
    }

    static class DocViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout item;
        AppCompatTextView fileName;
        AppCompatTextView fileExt;
        AppCompatTextView summary;

        DocViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.itemStoDoc_item);
            fileName = itemView.findViewById(R.id.itemStoDoc_name);
            fileExt = itemView.findViewById(R.id.itemStoDoc_fileType);
            summary = itemView.findViewById(R.id.itemStoDoc_summary);
        }

        void setDoc (StoItemsData_Doc docItem, Context mContext) {

            fileName.setText(docItem.getFileName());
            fileExt.setText(docItem.getFileExt());
            if (docItem.getSummary() != null) {
                summary.setVisibility(View.VISIBLE);
                summary.setText(docItem.getSummary());
            } else {
                summary.setVisibility(View.GONE);
            }

            item.setOnClickListener(v -> Toast.makeText(mContext, docItem.getUrl(), Toast.LENGTH_SHORT).show());
        }
    }

    public StoItemsAdapter(List<Parcelable> mList) {
        this.mList = mList;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mContext = recyclerView.getContext();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case ITEM_TITLE:
                final View viewTitle = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_sto_title, parent, false);
                return new TitleViewHolder(viewTitle);
            case ITEM_SUBTITLE:
                final View viewSubtitle = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_sto_subtitle, parent, false);
                return new SubtitleViewHolder(viewSubtitle);
            case ITEM_DOC:
                final View viewDoc = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_sto_doc, parent, false);
                return new DocViewHolder(viewDoc);
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final int viewType = holder.getItemViewType();
        switch (viewType) {
            case ITEM_TITLE:
                final StoItemsData_Title title = (StoItemsData_Title) mList.get(position);
                ((TitleViewHolder) holder).setTitle(title);
                break;

            case ITEM_SUBTITLE:
                final StoItemsData_Subtitle subtitle = (StoItemsData_Subtitle) mList.get(position);
                ((SubtitleViewHolder) holder).setSubtitle(subtitle);
                break;

            case ITEM_DOC:
                final StoItemsData_Doc doc = (StoItemsData_Doc) mList.get(position);
                ((DocViewHolder) holder).setDoc(doc, mContext);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (mList.get(position) instanceof StoItemsData_Title) {
            return ITEM_TITLE;
        } else if (mList.get(position) instanceof StoItemsData_Subtitle) {
            return ITEM_SUBTITLE;
        } else if (mList.get(position) instanceof StoItemsData_Doc) {
            return ITEM_DOC;
        } else {
            return -1;
        }
    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).hashCode();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(mContext, mContext.getString(R.string.toast_permission_ok), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
