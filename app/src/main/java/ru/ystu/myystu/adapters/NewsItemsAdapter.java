package ru.ystu.myystu.adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Parcelable;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.R;
import ru.ystu.myystu.adaptersData.NewsItemsData;
import ru.ystu.myystu.adaptersData.NewsItemsData_DontAttach;
import ru.ystu.myystu.adaptersData.NewsItemsData_Header;
import ru.ystu.myystu.utils.StringFormatter;
import ru.ystu.myystu.utils.UnixToString;

public class NewsItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_HEADER = 0;
    private static final int ITEM_DONT_ATTACH = 1;
    private static final int ITEM_1_PHOTO = 2;

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

    static class OnePhotoViewHodler extends RecyclerView.ViewHolder{

        private int id;
        private AppCompatTextView postDate;
        private AppCompatTextView postText;
        private AppCompatTextView postPinned;
        private SimpleDraweeView postPhoto;

        private UnixToString unixToString = new UnixToString();

        OnePhotoViewHodler(@NonNull View itemView) {
            super(itemView);

            postDate = itemView.findViewById(R.id.post_date);
            postPinned = itemView.findViewById(R.id.post_pinned);
            postText = itemView.findViewById(R.id.post_text);
            postPhoto = itemView.findViewById(R.id.post_photo);
        }

        void setOnePhoto(NewsItemsData onePhoto, StringFormatter stringFormatter, Context context){

            postText.setText(stringFormatter.getFormattedString(onePhoto.getText()));
            postText.setMovementMethod(LinkMovementMethod.getInstance());
            postDate.setText(unixToString.setUnixToString(onePhoto.getDate()));

            if(Objects.equals(onePhoto.getIsPinned(), 1))
                postPinned.setText("Запись закреплена");
            else
                postPinned.setText("");

            float w = (float)onePhoto.getListPhoto().get(0).getWidth();
            float h = (float)onePhoto.getListPhoto().get(0).getHeight();
            float aspectRatio = w / h;

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setLowResImageRequest(ImageRequest.fromUri(onePhoto.getListPhoto().get(0).getUrlSmall()))
                    .setImageRequest(ImageRequest.fromUri(onePhoto.getListPhoto().get(0).getUrlPreview()))
                    .setOldController(postPhoto.getController()).build();

            postPhoto.setController (controller);
            postPhoto.setAspectRatio(aspectRatio);

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

            case ITEM_1_PHOTO:
                View viewOnePhoto = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_news_item_one_photo, parent, false);
                viewHolder = new OnePhotoViewHodler(viewOnePhoto);
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
            case ITEM_1_PHOTO:
                NewsItemsData onePhoto = (NewsItemsData) mList.get(position);
                ((OnePhotoViewHodler) holder).setOnePhoto(onePhoto, stringFormatter, context);

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
        } else if(mList.get(position) instanceof NewsItemsData){        //&& ((NewsItemsData) mList.get(position)).getListPhoto().size() == 1
            viewType = ITEM_1_PHOTO;
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
