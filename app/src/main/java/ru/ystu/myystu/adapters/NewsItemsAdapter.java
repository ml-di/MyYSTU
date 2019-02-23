package ru.ystu.myystu.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcelable;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ImageDecodeOptionsBuilder;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.ContentFrameLayout;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
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
    private static final int ITEM_2_PHOTO = 3;
    private static final int ITEM_MORE_PHOTO = 4;

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
            postPhoto = itemView.findViewById(R.id.post_photo_1);
        }

        void setOnePhoto(NewsItemsData onePhoto, StringFormatter stringFormatter){

            postText.setText(stringFormatter.getFormattedString(onePhoto.getText()));
            postText.setMovementMethod(LinkMovementMethod.getInstance());
            postDate.setText(unixToString.setUnixToString(onePhoto.getDate()));

            if(Objects.equals(onePhoto.getIsPinned(), 1))
                postPinned.setText("Запись закреплена");
            else
                postPinned.setText("");

            final float w = (float)onePhoto.getListPhoto().get(0).getWidth();
            final float h = (float)onePhoto.getListPhoto().get(0).getHeight();
            final float aspectRatio = w / h;

            final ImageRequest imageRequest =
                    ImageRequestBuilder.newBuilderWithSource(Uri.parse(onePhoto.getListPhoto().get(0).getUrlPreview()))
                            .disableMemoryCache()
                            .setProgressiveRenderingEnabled(true)
                            .build();

            postPhoto.setAspectRatio(aspectRatio);
            postPhoto.setImageRequest(imageRequest);

        }

    }

    static class TwoPhotoViewHodler extends RecyclerView.ViewHolder{

        private int id;
        private AppCompatTextView postDate;
        private AppCompatTextView postText;
        private AppCompatTextView postPinned;
        private SimpleDraweeView postPhoto_1;
        private SimpleDraweeView postPhoto_2;

        private UnixToString unixToString = new UnixToString();

        TwoPhotoViewHodler(@NonNull View itemView) {
            super(itemView);

            postDate = itemView.findViewById(R.id.post_date);
            postPinned = itemView.findViewById(R.id.post_pinned);
            postText = itemView.findViewById(R.id.post_text);
            postPhoto_1 = itemView.findViewById(R.id.post_photo_1);
            postPhoto_2 = itemView.findViewById(R.id.post_photo_2);
        }

        void setTwoPhoto(NewsItemsData twoPhoto, StringFormatter stringFormatter, Context context){

            final int margin = Math.round(TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 2,context.getResources().getDisplayMetrics()));

            postText.setText(stringFormatter.getFormattedString(twoPhoto.getText()));
            postText.setMovementMethod(LinkMovementMethod.getInstance());
            postDate.setText(unixToString.setUnixToString(twoPhoto.getDate()));

            if(Objects.equals(twoPhoto.getIsPinned(), 1))
                postPinned.setText("Запись закреплена");
            else
                postPinned.setText("");

            float generalAspectRatio = 0;
            float[] aspectRatios = new float[2];

            for (int we = 0; we < 2; we++){
                final float w = (float)twoPhoto.getListPhoto().get(we).getWidth();
                final float h = (float)twoPhoto.getListPhoto().get(we).getHeight();
                final float aspectRatio = w / h;

                generalAspectRatio += aspectRatio;
                aspectRatios[we] = aspectRatio;
            }

            for(int i = 0; i < 2; i++){
                final float aspectRatio = aspectRatios[i];

                final float coef = 100 / generalAspectRatio;
                final float layout_weight = 1 - ((aspectRatio * coef) / 100);

                final LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);

                params.weight = layout_weight;

                final ImageRequest imageRequest =
                        ImageRequestBuilder.newBuilderWithSource(Uri.parse(twoPhoto.getListPhoto().get(i).getUrlPreview()))
                                .disableMemoryCache()
                                .setProgressiveRenderingEnabled(true)
                                .build();

                switch (i){
                    case 0:
                        params.setMarginEnd(margin);
                        postPhoto_1.setAspectRatio(aspectRatio);
                        postPhoto_1.setImageRequest(imageRequest);
                        postPhoto_1.setLayoutParams(params);
                        break;
                    case 1:
                        params.setMarginStart(margin);
                        postPhoto_2.setAspectRatio(aspectRatio);
                        postPhoto_2.setImageRequest(imageRequest);
                        postPhoto_2.setLayoutParams(params);
                        break;
                }
            }
        }
    }

    static class MorePhotoViewHodler extends RecyclerView.ViewHolder{

        private int id;
        private AppCompatTextView postDate;
        private AppCompatTextView postText;
        private AppCompatTextView postPinned;
        private SimpleDraweeView postPhoto_1;
        private SimpleDraweeView postPhoto_2;
        private SimpleDraweeView postPhoto_3;
        private LinearLayoutCompat photoCountFrame;
        private AppCompatTextView photoCountText;

        private UnixToString unixToString = new UnixToString();

        MorePhotoViewHodler(@NonNull View itemView) {
            super(itemView);

            postDate = itemView.findViewById(R.id.post_date);
            postPinned = itemView.findViewById(R.id.post_pinned);
            postText = itemView.findViewById(R.id.post_text);
            postPhoto_1 = itemView.findViewById(R.id.post_photo_1);
            postPhoto_2 = itemView.findViewById(R.id.post_photo_2);
            postPhoto_3 = itemView.findViewById(R.id.post_photo_3);
            photoCountFrame = itemView.findViewById(R.id.photoCountFrame);
            photoCountText = itemView.findViewById(R.id.photoCountText);
        }

        void setMorePhoto(NewsItemsData morePhoto, StringFormatter stringFormatter, Context context){

            postText.setText(stringFormatter.getFormattedString(morePhoto.getText()));
            postText.setMovementMethod(LinkMovementMethod.getInstance());
            postDate.setText(unixToString.setUnixToString(morePhoto.getDate()));

            if(Objects.equals(morePhoto.getIsPinned(), 1))
                postPinned.setText("Запись закреплена");
            else
                postPinned.setText("");

            for(int i = 0; i < 3; i++){

                final float w = (float)morePhoto.getListPhoto().get(i).getWidth();
                final float h = (float)morePhoto.getListPhoto().get(i).getHeight();
                final float aspectRatio = w / h;

                final ImageRequest imageRequest =
                        ImageRequestBuilder.newBuilderWithSource(Uri.parse(morePhoto.getListPhoto().get(i).getUrlPreview()))
                                .disableMemoryCache()
                                .setProgressiveRenderingEnabled(true)
                                .build();

                switch (i){
                    case 0:
                        postPhoto_1.setAspectRatio(aspectRatio);
                        postPhoto_1.setImageRequest(imageRequest);
                        break;
                    case 1:
                        postPhoto_2.setAspectRatio(aspectRatio);
                        postPhoto_2.setImageRequest(imageRequest);
                        break;
                    case 2:
                        postPhoto_3.setAspectRatio(aspectRatio);
                        postPhoto_3.setImageRequest(imageRequest);
                        break;
                }

                if(morePhoto.getListPhoto().size() > 3){

                    final String count = String.valueOf(morePhoto.getListPhoto().size() - 2);

                    postPhoto_3.getHierarchy().setOverlayImage(context.getResources().getDrawable(R.color.colorOverleyImage));
                    photoCountFrame.setVisibility(View.VISIBLE);
                    photoCountText.setText(count);
                } else {
                    postPhoto_3.getHierarchy().setOverlayImage(null);
                    photoCountFrame.setVisibility(View.GONE);
                    photoCountText.setText("");
                }
            }
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
                final View viewHeader = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_news_item_header, parent, false);
                viewHolder = new HeaderViewHolder(viewHeader);
            break;

            case ITEM_DONT_ATTACH:
                final View viewDontAttach = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_news_item_dont_attach, parent, false);
                viewHolder = new DontAttachViewHolder(viewDontAttach);
            break;

            case ITEM_1_PHOTO:
                final View viewOnePhoto = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_news_item_one_photo, parent, false);
                viewHolder = new OnePhotoViewHodler(viewOnePhoto);
            break;

            case ITEM_2_PHOTO:
                final View viewTwoPhoto = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_news_item_two_photo, parent, false);
                viewHolder = new TwoPhotoViewHodler(viewTwoPhoto);
            break;

            case ITEM_MORE_PHOTO:
                final View viewMorePhoto = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_news_item_more_photo, parent, false);
                viewHolder = new MorePhotoViewHodler(viewMorePhoto);
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
                final NewsItemsData_Header header = (NewsItemsData_Header)mList.get(position);
                ((HeaderViewHolder) holder).setHeader(header);
                break;
            case ITEM_DONT_ATTACH:
                final NewsItemsData_DontAttach dontAttach = (NewsItemsData_DontAttach) mList.get(position);
                ((DontAttachViewHolder) holder).setDontAttach(dontAttach, stringFormatter);
                break;
            case ITEM_1_PHOTO:
                final NewsItemsData onePhoto = (NewsItemsData) mList.get(position);
                ((OnePhotoViewHodler) holder).setOnePhoto(onePhoto, stringFormatter);
                break;
            case ITEM_2_PHOTO:
                final NewsItemsData twoPhoto = (NewsItemsData) mList.get(position);
                ((TwoPhotoViewHodler) holder).setTwoPhoto(twoPhoto, stringFormatter, context);
                break;
            case ITEM_MORE_PHOTO:
                final NewsItemsData morePhoto = (NewsItemsData) mList.get(position);
                ((MorePhotoViewHodler) holder).setMorePhoto(morePhoto, stringFormatter, context);
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
        } else if(mList.get(position) instanceof NewsItemsData
                && ((NewsItemsData) mList.get(position)).getListPhoto().size() == 1){
            viewType = ITEM_1_PHOTO;
        } else if(mList.get(position) instanceof NewsItemsData
                && ((NewsItemsData) mList.get(position)).getListPhoto().size() == 2){
            viewType = ITEM_2_PHOTO;
        } else if(mList.get(position) instanceof NewsItemsData
                && ((NewsItemsData) mList.get(position)).getListPhoto().size() > 2){
            viewType = ITEM_MORE_PHOTO;
        } else{
            viewType = -1;
        }

        return viewType;
    }

    @Override
    public long getItemId(int position) { //super.getItemId(position)
        return mList.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
