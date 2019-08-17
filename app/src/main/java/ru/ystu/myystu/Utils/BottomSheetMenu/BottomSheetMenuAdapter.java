package ru.ystu.myystu.Utils.BottomSheetMenu;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.BottomSheetMenu.Data.DividerData;
import ru.ystu.myystu.Utils.BottomSheetMenu.Data.IconsAndTextData;
import ru.ystu.myystu.Utils.BottomSheetMenu.Data.NullIconsAndTextData;
import ru.ystu.myystu.Utils.BottomSheetMenu.Data.OnlyTextData;
import ru.ystu.myystu.Utils.BottomSheetMenu.Interface.OnItemClickListener;
import ru.ystu.myystu.Utils.Converter;

public class BottomSheetMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_ONLY_TEXT = 0;
    private static final int ITEM_ICON_AND_TEXT = 1;
    private static final int ITEM_NULL_ICON_AND_TEXT = 2;
    private static final int ITEM_DIVIDER = 3;

    private Context mContext;
    private ArrayList<Parcelable> mList;
    private RecyclerView.Adapter adapter;
    private OnItemClickListener onItemClickListener;

    static class OnlyTextViewHolder extends RecyclerView.ViewHolder {

        private LinearLayoutCompat item;
        private AppCompatTextView title;

        OnlyTextViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.bottomSheetMenu_item);
            title = itemView.findViewById(R.id.bottomSheetMenu_item_title);
        }

        void setOnlyText (OnlyTextData onlyTextItem, RecyclerView.Adapter adapter) {

            ((BottomSheetMenuAdapter) adapter).setDefaultView(title, item, onlyTextItem.getTitle(), onlyTextItem.isEnabled(), onlyTextItem.getItemId());
        }
    }

    static class IconAndTextViewHolder extends RecyclerView.ViewHolder {

        private LinearLayoutCompat item;
        private AppCompatTextView title;
        private AppCompatImageView icon;

        IconAndTextViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.bottomSheetMenu_item);
            title = itemView.findViewById(R.id.bottomSheetMenu_item_title);
            icon = itemView.findViewById(R.id.bottomSheetMenu_item_icon);
        }

        void setIconAndText (IconsAndTextData imageAndTextItem, RecyclerView.Adapter adapter) {

            ((BottomSheetMenuAdapter) adapter).setDefaultView(title, item, imageAndTextItem.getTitle(), imageAndTextItem.isEnabled(), imageAndTextItem.getItemId());
            icon.setImageDrawable(imageAndTextItem.getIcon());
        }
    }

    static class NullIconAndTextViewHolder extends RecyclerView.ViewHolder {

        private LinearLayoutCompat item;
        private AppCompatTextView title;

        NullIconAndTextViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.bottomSheetMenu_item);
            title = itemView.findViewById(R.id.bottomSheetMenu_item_title);
        }

        void setNullIconAndText (NullIconsAndTextData nullIconAndText, RecyclerView.Adapter adapter) {

            ((BottomSheetMenuAdapter) adapter).setDefaultView(title, item, nullIconAndText.getTitle(), nullIconAndText.isEnabled(), nullIconAndText.getItemId());
        }
    }

    static class DividerViewHolder extends RecyclerView.ViewHolder {

        private LinearLayoutCompat item;

        DividerViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.bottomSheetMenu_item);
        }

        void setDivider (DividerData divider, Context mContext) {

            final int padding;

            if (divider.isIcon()) {
                padding = (int) Converter.convertDpToPixel(72, mContext);
            } else {
                padding = (int) Converter.convertDpToPixel(16, mContext);
            }
            item.setPadding(padding, 0, 0, 0);
        }
    }

    BottomSheetMenuAdapter(ArrayList<Parcelable> mList) {
        this.mList = mList;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        adapter = recyclerView.getAdapter();
        mContext = recyclerView.getContext();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        final RecyclerView.ViewHolder mViewHolder;

        switch (viewType) {
            case ITEM_ONLY_TEXT:
                final View viewOnlyText = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_bottomsheetmenu_item_onlytext, parent, false);
                mViewHolder = new OnlyTextViewHolder(viewOnlyText);
                break;

            case ITEM_ICON_AND_TEXT:
                final View viewIconAndText = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_bottomsheetmenu_item_iconandtext, parent, false);
                mViewHolder = new IconAndTextViewHolder(viewIconAndText);
                break;

            case ITEM_NULL_ICON_AND_TEXT:
                final View viewNullIconAndText = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_bottomsheetmenu_item_nulliconandtext, parent, false);
                mViewHolder = new NullIconAndTextViewHolder(viewNullIconAndText);
                break;

            case ITEM_DIVIDER:
                final View viewDivider = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_bottomsheetmenu_item_divider, parent, false);
                mViewHolder = new DividerViewHolder(viewDivider);
                break;

            default:
                mViewHolder = null;
                break;
        }

        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final int viewType = holder.getItemViewType();
        switch (viewType) {
            case ITEM_ONLY_TEXT:
                final OnlyTextData onlyText = (OnlyTextData) mList.get(position);
                ((OnlyTextViewHolder) holder).setOnlyText(onlyText, adapter);
                break;

            case ITEM_ICON_AND_TEXT:
                final IconsAndTextData iconsAndText = (IconsAndTextData) mList.get(position);
                ((IconAndTextViewHolder) holder).setIconAndText(iconsAndText, adapter);
                break;

            case ITEM_NULL_ICON_AND_TEXT:
                final NullIconsAndTextData nullIconsAndTextData = (NullIconsAndTextData) mList.get(position);
                ((NullIconAndTextViewHolder) holder).setNullIconAndText(nullIconsAndTextData, adapter);
                break;

            case ITEM_DIVIDER:
                final DividerData dividerData = (DividerData) mList.get(position);
                ((DividerViewHolder) holder).setDivider(dividerData, mContext);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {

        final int viewType;

        if (mList.get(position) instanceof OnlyTextData) {
            viewType = ITEM_ONLY_TEXT;
        } else if (mList.get(position) instanceof IconsAndTextData) {
            viewType = ITEM_ICON_AND_TEXT;
        } else if (mList.get(position) instanceof NullIconsAndTextData) {
            viewType = ITEM_NULL_ICON_AND_TEXT;
        } else if (mList.get(position) instanceof DividerData) {
            viewType = ITEM_DIVIDER;
        } else  {
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

    void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    private void setDefaultView(AppCompatTextView titleTextView,
                                LinearLayoutCompat item,
                                String title,
                                boolean isEnabled,
                                int itemId) {

        titleTextView.setText(title);

        item.setEnabled(isEnabled);
        if (isEnabled) {
            item.setAlpha(1);
        } else {
            item.setAlpha(0.5f);
        }

        item.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.OnItemClick(itemId);
            }
        });
    }
}
