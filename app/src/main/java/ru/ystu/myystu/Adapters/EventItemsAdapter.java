package ru.ystu.myystu.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.Activitys.EventActivity;
import ru.ystu.myystu.Activitys.EventFullActivity;
import ru.ystu.myystu.AdaptersData.StringData;
import ru.ystu.myystu.AdaptersData.EventItemsData_Event;
import ru.ystu.myystu.AdaptersData.EventItemsData_Header;
import ru.ystu.myystu.AdaptersData.UpdateItemsTitle;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.FrescoHelper;
import ru.ystu.myystu.Utils.SettingsController;

public class EventItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_HEADER = 0;
    private static final int ITEM_DIVIDER = 1;
    private static final int ITEM_EVENT = 2;
    private static final int ITEM_TITLE = 3;

    private List<Parcelable> mList;
    private Context mContext;

    static class  HeaderViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView chip1;
        private AppCompatTextView chip2;
        private AppCompatTextView chip3;

        HeaderViewHolder(View itemView) {
            super(itemView);

            chip1 = itemView.findViewById(R.id.eventItem_header_chip1);
            chip2 = itemView.findViewById(R.id.eventItem_header_chip2);
            chip3 = itemView.findViewById(R.id.eventItem_header_chip3);
        }

        void setHeader (EventItemsData_Header headerItem, Context mContext) {

            View[] chips = new View[]{chip1, chip2, chip3};

            for (int i = 0; i < chips.length; i++) {
                ((AppCompatTextView) chips[i]).setText(headerItem.getTitle()[i]);
                if (headerItem.getSelected_id() == i) {
                    selectedChip(chips[i], mContext);
                }

                int finalI = i;
                chips[i].setOnClickListener(v -> {
                    if(headerItem.getSelected_id() != finalI && !((EventActivity) mContext).isRefresh()){
                        ((EventActivity) mContext).getEvent(headerItem.getUrl()[finalI]);
                        ((EventActivity) mContext).setUrl(headerItem.getUrl()[finalI]);
                        resetChip(chips, mContext);
                        selectedChip(v, mContext);
                    }
                });
            }
        }

        private void resetChip (View[] chips, Context mContext) {

            for (View chip : chips) {
                chip.setAlpha(0.6f);
                chip.setBackgroundTintList(null);
                ((AppCompatTextView) chip).setTextColor(mContext.getResources().getColor(R.color.colorTextBlack));
            }
        }

        private void selectedChip (View chip, Context mContext) {
            chip.setAlpha(1);
            chip.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorPrimary)));
            ((AppCompatTextView) chip).setTextColor(mContext.getResources().getColor(R.color.colorBackground));
        }
    }

    static class  DividerViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView divider;

        DividerViewHolder(View itemView) {
            super(itemView);

            divider = itemView.findViewById(R.id.eventItem_divider_title);
        }

        void setDivider (StringData dividerItem) {
            divider.setText(dividerItem.getTitle());
        }
    }

    static class  EventItemViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout mainLayout;
        private SimpleDraweeView image;
        private AppCompatTextView date;
        private AppCompatTextView location;
        private AppCompatTextView title;
        private ConstraintLayout newView;

        EventItemViewHolder(View itemView) {
            super(itemView);

            mainLayout = itemView.findViewById(R.id.eventItem_mainLayout);
            image = itemView.findViewById(R.id.eventItem_image);
            date = itemView.findViewById(R.id.eventItem_date);
            location = itemView.findViewById(R.id.eventItem_location);
            title = itemView.findViewById(R.id.eventItem_title);
            newView = itemView.findViewById(R.id.eventItem_isNewTag);
        }

        void setViewItem(EventItemsData_Event eventItem, Context mContext){

            if (SettingsController.isImageDownload(mContext)) {
                image.setImageRequest(FrescoHelper.getImageRequest(mContext, eventItem.getPhotoUrl()));
            }

            date.setText(eventItem.getDate());
            location.setText(eventItem.getLocation());
            title.setText(eventItem.getTitle());

            if(eventItem.getLocation().length() < 1) {
                location.setVisibility(View.GONE);
            } else {
                location.setVisibility(View.VISIBLE);
            }

            if (eventItem.isNew()) {
                newView.setVisibility(View.VISIBLE);
            } else {
                newView.setVisibility(View.GONE);
            }

            mainLayout.setOnClickListener(view -> {

                final Intent mIntent = new Intent(mContext, EventFullActivity.class);
                mIntent
                        .putExtra("id", eventItem.getId())
                        .putExtra("url", eventItem.getLink())
                        .putExtra("urlPhoto", eventItem.getPhotoUrl())
                        .putExtra("title", eventItem.getTitle())
                        .putExtra("date", eventItem.getDate())
                        .putExtra("location", eventItem.getLocation());

                mContext.startActivity(mIntent);
                if (SettingsController.isEnabledAnim(mContext)) {
                    ((Activity)mContext).overridePendingTransition(R.anim.activity_slide_right_show, R.anim.activity_slide_left_out);
                } else {
                    ((Activity)mContext).overridePendingTransition(0 , 0);
                }

            });
        }
    }

    static class TitleViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView title;

        TitleViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.itemUser_divider);
        }

        void setTitle (UpdateItemsTitle updateItemsTitle) {
            title.setText(updateItemsTitle.getTitle());
        }
    }

    public EventItemsAdapter(List<Parcelable> mList, Context mContext) {
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
            case ITEM_HEADER:
                final View viewHeader = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_event_item_header, parent, false);
                mViewHolder = new HeaderViewHolder(viewHeader);
                break;

            case ITEM_DIVIDER:
                final View viewDivider = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_event_item_divider, parent, false);
                mViewHolder = new DividerViewHolder(viewDivider);
                break;

            case ITEM_EVENT:
                final View viewEvent = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_event_item_event, parent, false);
                mViewHolder = new EventItemViewHolder(viewEvent);
                break;

            case ITEM_TITLE:
                final View viewTitle = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_users_divider, parent, false);
                mViewHolder = new TitleViewHolder(viewTitle);
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
            case ITEM_HEADER:
                final EventItemsData_Header header = (EventItemsData_Header)mList.get(position);
                ((HeaderViewHolder) holder).setHeader(header, mContext);
                break;
            case ITEM_DIVIDER:
                final StringData divider = (StringData) mList.get(position);
                ((DividerViewHolder) holder).setDivider(divider);
                break;
            case ITEM_EVENT:
                final EventItemsData_Event viewItem = (EventItemsData_Event) mList.get(position);
                ((EventItemViewHolder) holder).setViewItem(viewItem, mContext);
                break;
            case ITEM_TITLE:
                final UpdateItemsTitle viewTitle = (UpdateItemsTitle) mList.get(position);
                ((TitleViewHolder) holder).setTitle(viewTitle);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;

        if (mList.get(position) instanceof EventItemsData_Header) {
            viewType = ITEM_HEADER;
        } else if (mList.get(position) instanceof StringData) {
            viewType = ITEM_DIVIDER;
        } else if (mList.get(position) instanceof EventItemsData_Event) {
            viewType = ITEM_EVENT;
        } else if (mList.get(position) instanceof UpdateItemsTitle) {
            viewType = ITEM_TITLE;
        } else {
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
