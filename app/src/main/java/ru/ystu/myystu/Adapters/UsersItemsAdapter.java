package ru.ystu.myystu.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.Activitys.UserFullActivity;
import ru.ystu.myystu.AdaptersData.StringData;
import ru.ystu.myystu.AdaptersData.EventItemsData_Header;
import ru.ystu.myystu.AdaptersData.UsersItemsData;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.FrescoHelper;
import ru.ystu.myystu.Utils.SettingsController;

public class UsersItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private static final int ITEM_HEADER = 1;
    private static final int ITEM_USER = 2;
    private static final int ITEM_DIVIDER = 3;

    private List<Parcelable> mList;
    private List<Parcelable> mListFiltered;
    private Context mContext;

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void setHeader (EventItemsData_Header header, Context mContext) {

        }
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout mainLayout;
        private SimpleDraweeView image;
        private AppCompatTextView name;
        private AppCompatTextView information;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);

            mainLayout = itemView.findViewById(R.id.itemUser);
            image = itemView.findViewById(R.id.itemUser_image);
            name = itemView.findViewById(R.id.itemUser_name);
            information = itemView.findViewById(R.id.itemUser_information);
        }

        void setUser (UsersItemsData user, Context mContext) {

            if (SettingsController.isImageDownload(mContext)) {
                image.setImageRequest(FrescoHelper.getImageRequest(mContext, user.getImage()));
            }

            name.setText(user.getName());
            information.setText(user.getInformation());

            mainLayout.setOnClickListener(view -> {
                final Intent mIntent = new Intent(mContext, UserFullActivity.class);
                mIntent
                        .putExtra("id", user.getId())
                        .putExtra("link", user.getLink())
                        .putExtra("name", user.getName())
                        .putExtra("image", user.getImage())
                        .putExtra("information", user.getInformation());

                mContext.startActivity(mIntent);
                if (SettingsController.isEnabledAnim(mContext)) {
                    ((Activity)mContext).overridePendingTransition(R.anim.activity_slide_right_show, R.anim.activity_slide_left_out);
                } else {
                    ((Activity)mContext).overridePendingTransition(0, 0);
                }
            });

        }
    }

    static class DividerViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView title;

        DividerViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.itemUser_divider);
        }

        void setDivider (StringData divider) {
            title.setText(divider.getTitle());
        }
    }

    public UsersItemsAdapter(List<Parcelable> mList, Context mContext) {
        this.mList = mList;
        this.mListFiltered = mList;
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

            case ITEM_USER:
                final View viewUser = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_users_item, parent, false);
                mViewHolder = new UserViewHolder(viewUser);
                break;

            case ITEM_DIVIDER:
                final View viewDivider = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_users_divider, parent, false);
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

        int viewType = holder.getItemViewType();
        switch (viewType) {

            case ITEM_HEADER:
                final EventItemsData_Header header = (EventItemsData_Header)mListFiltered.get(position);
                ((HeaderViewHolder) holder).setHeader(header, mContext);
                break;
            case ITEM_USER:
                final UsersItemsData user = (UsersItemsData) mListFiltered.get(position);
                ((UserViewHolder) holder).setUser(user, mContext);
                break;
            case ITEM_DIVIDER:
                final StringData divider = (StringData) mListFiltered.get(position);
                ((DividerViewHolder) holder).setDivider(divider);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {

        int viewType;

        if (mListFiltered.get(position) instanceof EventItemsData_Header) {
            viewType = ITEM_HEADER;
        } else if (mListFiltered.get(position) instanceof UsersItemsData) {
            viewType = ITEM_USER;
        } else if (mListFiltered.get(position) instanceof StringData) {
            viewType = ITEM_DIVIDER;
        } else {
            viewType = -1;
        }

        return viewType;
    }

    @Override
    public int getItemCount() {
        return mListFiltered.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                final FilterResults mFilterResults = new FilterResults();
                final ArrayList<Parcelable> resultList = new ArrayList<>();

                if (constraint.equals("")) {
                    mListFiltered = mList;
                } else {
                    final String filter = constraint.toString().toLowerCase().trim();
                    for (Parcelable p : mList) {
                        if (p instanceof UsersItemsData) {
                            final String nameTemp = ((UsersItemsData) p).getName().toLowerCase();
                            final String informationTemp = ((UsersItemsData) p).getInformation().toLowerCase();

                            if (nameTemp.contains(filter) || informationTemp.contains(filter)) {
                                final int id = ((UsersItemsData) p).getId();
                                final String link = ((UsersItemsData) p).getLink();
                                final String image = ((UsersItemsData) p).getImage();
                                final String name = ((UsersItemsData) p).getName();
                                final String information = ((UsersItemsData) p).getInformation();

                                resultList.add(new UsersItemsData(id, link, image, name, information));
                            }
                        }
                    }
                    mListFiltered = resultList;
                    mListFiltered.add(1, new StringData(-1, mContext.getResources().getString(R.string.other_search_results)));
                }

                mFilterResults.values = mListFiltered;
                mFilterResults.count = mListFiltered.size();

                return mFilterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mListFiltered = (ArrayList<Parcelable>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
