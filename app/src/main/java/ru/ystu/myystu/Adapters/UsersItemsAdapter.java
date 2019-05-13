package ru.ystu.myystu.Adapters;

import android.content.Context;
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
import ru.ystu.myystu.AdaptersData.EventItemsData_Header;
import ru.ystu.myystu.AdaptersData.ToolbarPlaceholderData;
import ru.ystu.myystu.AdaptersData.UsersItemsData;
import ru.ystu.myystu.R;

public class UsersItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_TOOLBAR_PLACEHOLDER = 0;
    private static final int ITEM_HEADER = 1;
    private static final int ITEM_USER = 2;

    private List<Parcelable> mList;
    private Context mContext;

    static class PlaceholderViewHolder extends RecyclerView.ViewHolder {
        PlaceholderViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void setPlaceholder (ToolbarPlaceholderData placeholderItem) {

        }
    }

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

            image.setImageURI(user.getImage());
            name.setText(user.getName());
            information.setText(user.getInformation());

            mainLayout.setOnClickListener(view -> {

            });

        }
    }

    public UsersItemsAdapter(List<Parcelable> mList, Context mContext) {
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

            case ITEM_TOOLBAR_PLACEHOLDER:
                final View viewPlaceholder = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_toolbar_placeholder, parent, false);
                mViewHolder = new PlaceholderViewHolder(viewPlaceholder);
                break;

            case ITEM_HEADER:
                final View viewHeader = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_event_item_header, parent, false);
                mViewHolder = new HeaderViewHolder(viewHeader);
                break;

            case ITEM_USER:
                final View viewUser = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_users_item, parent, false);
                mViewHolder = new UserViewHolder(viewUser);
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

            case ITEM_TOOLBAR_PLACEHOLDER:
                final ToolbarPlaceholderData placeholder = (ToolbarPlaceholderData) mList.get(position);
                ((PlaceholderViewHolder) holder).setPlaceholder(placeholder);
                break;
            case ITEM_HEADER:
                final EventItemsData_Header header = (EventItemsData_Header)mList.get(position);
                ((HeaderViewHolder) holder).setHeader(header, mContext);
                break;
            case ITEM_USER:
                final UsersItemsData user = (UsersItemsData) mList.get(position);
                ((UserViewHolder) holder).setUser(user, mContext);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {

        int viewType;

        if (mList.get(position) instanceof ToolbarPlaceholderData) {
            viewType = ITEM_TOOLBAR_PLACEHOLDER;
        } else if (mList.get(position) instanceof EventItemsData_Header) {
            viewType = ITEM_HEADER;
        } else if (mList.get(position) instanceof UsersItemsData) {
            viewType = ITEM_USER;
        }  else {
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
