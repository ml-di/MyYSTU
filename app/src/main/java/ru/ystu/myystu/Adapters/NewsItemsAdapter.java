package ru.ystu.myystu.Adapters;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import ru.ystu.myystu.R;
import ru.ystu.myystu.Activitys.ViewPhotoActivity;
import ru.ystu.myystu.AdaptersData.NewsItemsData;
import ru.ystu.myystu.AdaptersData.NewsItemsData_DontAttach;
import ru.ystu.myystu.AdaptersData.NewsItemsData_Header;
import ru.ystu.myystu.AdaptersData.NewsItemsPhotoData;
import ru.ystu.myystu.Utils.BottomSheetMenu.BottomSheetMenu;
import ru.ystu.myystu.Utils.IntentHelper;
import ru.ystu.myystu.Utils.SettingsController;
import ru.ystu.myystu.Utils.StringFormatter;
import ru.ystu.myystu.Utils.UnixToString;

import static android.content.Context.CLIPBOARD_SERVICE;

public class NewsItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_HEADER = 0;
    private static final int ITEM_DONT_ATTACH = 1;
    private static final int ITEM_1_PHOTO = 2;
    private static final int ITEM_2_PHOTO = 3;
    private static final int ITEM_MORE_PHOTO = 4;

    private ArrayList<Parcelable> mList;
    private Context mContext;
    private StringFormatter stringFormatter = new StringFormatter();

    static class HeaderViewHolder extends RecyclerView.ViewHolder{

        private int id;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);

        }

        void setHeader(NewsItemsData_Header header){

        }
    }

    static class DontAttachViewHolder extends RecyclerView.ViewHolder{

        private AppCompatTextView postDate;
        private AppCompatTextView postText;
        private ConstraintLayout postPin;
        private AppCompatImageView menuNewsItem;

        private UnixToString unixToString = new UnixToString();

        DontAttachViewHolder(@NonNull View itemView) {
            super(itemView);

            postDate = itemView.findViewById(R.id.post_date);
            postText = itemView.findViewById(R.id.post_text);
            postPin = itemView.findViewById(R.id.post_pin);
            menuNewsItem = itemView.findViewById(R.id.menu_news_item);
        }

        void setDontAttach(NewsItemsData_DontAttach dontAttach, StringFormatter stringFormatter, Context mContext){

            postText.setText(stringFormatter.getFormattedString(dontAttach.getText()));
            postText.setMovementMethod(LinkMovementMethod.getInstance());
            postDate.setText(unixToString.setUnixToString(dontAttach.getDate(), mContext));

            if(Objects.equals(dontAttach.getIsPinned(), 1))
                postPin.setVisibility(View.VISIBLE);
            else
                postPin.setVisibility(View.GONE);

            menuNewsItem.setOnClickListener(e -> showMenu(mContext, dontAttach.getSigner(), dontAttach.getUrlPost(), postText.getText().toString()));
        }
    }

    static class OnePhotoViewHodler extends RecyclerView.ViewHolder{

        private AppCompatTextView postDate;
        private AppCompatTextView postText;
        private SimpleDraweeView postPhoto;
        private ConstraintLayout postPin;
        private AppCompatImageView menuNewsItem;

        private UnixToString unixToString = new UnixToString();

        OnePhotoViewHodler(@NonNull View itemView) {
            super(itemView);

            postDate = itemView.findViewById(R.id.post_date);
            postText = itemView.findViewById(R.id.post_text);
            postPhoto = itemView.findViewById(R.id.post_photo_1);
            postPin = itemView.findViewById(R.id.post_pin);
            menuNewsItem = itemView.findViewById(R.id.menu_news_item);
        }

        void setOnePhoto(NewsItemsData onePhoto, StringFormatter stringFormatter, Context mContext){

            postText.setText(stringFormatter.getFormattedString(onePhoto.getText()));
            postText.setMovementMethod(LinkMovementMethod.getInstance());
            postDate.setText(unixToString.setUnixToString(onePhoto.getDate(), mContext));

            if(Objects.equals(onePhoto.getIsPinned(), 1))
                postPin.setVisibility(View.VISIBLE);
            else
                postPin.setVisibility(View.GONE);

            final float w = (float)onePhoto.getListPhoto().get(0).getWidth();
            final float h = (float)onePhoto.getListPhoto().get(0).getHeight();
            final float aspectRatio = w / h;

            final ImageRequest mImageRequest =
                    ImageRequestBuilder.newBuilderWithSource(Uri.parse(onePhoto.getListPhoto().get(0).getUrlPreview()))
                            .disableMemoryCache()
                            .setProgressiveRenderingEnabled(true)
                            .build();

            postPhoto.setAspectRatio(aspectRatio);
            postPhoto.setImageRequest(mImageRequest);

            menuNewsItem.setOnClickListener(e -> showMenu(mContext, onePhoto.getSigner(), onePhoto.getUrlPost(), postText.getText().toString()));
            postPhoto.setOnClickListener(e -> {
                new PhotoViewSetter().setPhoto(mContext, onePhoto.getListPhoto(), 0);
            });

        }

    }

    static class TwoPhotoViewHodler extends RecyclerView.ViewHolder{

        private AppCompatTextView postDate;
        private AppCompatTextView postText;
        private SimpleDraweeView postPhoto_1;
        private SimpleDraweeView postPhoto_2;
        private ConstraintLayout postPin;
        private AppCompatImageView menuNewsItem;

        private UnixToString unixToString = new UnixToString();

        TwoPhotoViewHodler(@NonNull View itemView) {
            super(itemView);

            postDate = itemView.findViewById(R.id.post_date);
            postText = itemView.findViewById(R.id.post_text);
            postPhoto_1 = itemView.findViewById(R.id.post_photo_1);
            postPhoto_2 = itemView.findViewById(R.id.post_photo_2);
            postPin = itemView.findViewById(R.id.post_pin);
            menuNewsItem = itemView.findViewById(R.id.menu_news_item);
        }

        void setTwoPhoto(NewsItemsData twoPhoto, StringFormatter stringFormatter, Context mContext){

            final int margin = Math.round(TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 2,mContext.getResources().getDisplayMetrics()));

            postText.setText(stringFormatter.getFormattedString(twoPhoto.getText()));
            postText.setMovementMethod(LinkMovementMethod.getInstance());
            postDate.setText(unixToString.setUnixToString(twoPhoto.getDate(), mContext));

            if(Objects.equals(twoPhoto.getIsPinned(), 1))
                postPin.setVisibility(View.VISIBLE);
            else
                postPin.setVisibility(View.GONE);

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

                final ImageRequest mImageRequest =
                        ImageRequestBuilder.newBuilderWithSource(Uri.parse(twoPhoto.getListPhoto().get(i).getUrlPreview()))
                                .disableMemoryCache()
                                .setProgressiveRenderingEnabled(true)
                                .build();

                switch (i){
                    case 0:
                        params.setMarginEnd(margin);
                        postPhoto_1.setAspectRatio(aspectRatio);
                        postPhoto_1.setImageRequest(mImageRequest);
                        postPhoto_1.setLayoutParams(params);
                        break;
                    case 1:
                        params.setMarginStart(margin);
                        postPhoto_2.setAspectRatio(aspectRatio);
                        postPhoto_2.setImageRequest(mImageRequest);
                        postPhoto_2.setLayoutParams(params);
                        break;
                }
            }

            menuNewsItem.setOnClickListener(e -> showMenu(mContext, twoPhoto.getSigner(), twoPhoto.getUrlPost(), postText.getText().toString()));
            postPhoto_1.setOnClickListener(e -> {
                new PhotoViewSetter().setPhoto(mContext, twoPhoto.getListPhoto(), 0);
            });
            postPhoto_2.setOnClickListener(e -> {
                new PhotoViewSetter().setPhoto(mContext, twoPhoto.getListPhoto(), 1);
            });
        }
    }

    static class MorePhotoViewHodler extends RecyclerView.ViewHolder{

        private AppCompatTextView postDate;
        private AppCompatTextView postText;
        private SimpleDraweeView postPhoto_1;
        private SimpleDraweeView postPhoto_2;
        private SimpleDraweeView postPhoto_3;
        private LinearLayoutCompat photoCountFrame;
        private AppCompatTextView photoCountText;
        private ConstraintLayout postPin;
        private AppCompatImageView menuNewsItem;

        private UnixToString unixToString = new UnixToString();

        MorePhotoViewHodler(@NonNull View itemView) {
            super(itemView);

            postDate = itemView.findViewById(R.id.post_date);
            postText = itemView.findViewById(R.id.post_text);
            postPhoto_1 = itemView.findViewById(R.id.post_photo_1);
            postPhoto_2 = itemView.findViewById(R.id.post_photo_2);
            postPhoto_3 = itemView.findViewById(R.id.post_photo_3);
            photoCountFrame = itemView.findViewById(R.id.photoCountFrame);
            photoCountText = itemView.findViewById(R.id.photoCountText);
            postPin = itemView.findViewById(R.id.post_pin);
            menuNewsItem = itemView.findViewById(R.id.menu_news_item);

        }

        void setMorePhoto(NewsItemsData morePhoto, StringFormatter stringFormatter, Context mContext){

            postText.setText(stringFormatter.getFormattedString(morePhoto.getText()));
            postText.setMovementMethod(LinkMovementMethod.getInstance());
            postDate.setText(unixToString.setUnixToString(morePhoto.getDate(), mContext));

            if(Objects.equals(morePhoto.getIsPinned(), 1))
                postPin.setVisibility(View.VISIBLE);
            else
                postPin.setVisibility(View.GONE);

            for(int i = 0; i < 3; i++){

                final float w = (float)morePhoto.getListPhoto().get(i).getWidth();
                final float h = (float)morePhoto.getListPhoto().get(i).getHeight();
                final float aspectRatio = w / h;

                final ImageRequest mImageRequest =
                        ImageRequestBuilder.newBuilderWithSource(Uri.parse(morePhoto.getListPhoto().get(i).getUrlPreview()))
                                .disableMemoryCache()
                                .setProgressiveRenderingEnabled(true)
                                .build();

                switch (i){
                    case 0:
                        postPhoto_1.setAspectRatio(aspectRatio);
                        postPhoto_1.setImageRequest(mImageRequest);
                        break;
                    case 1:
                        postPhoto_2.setAspectRatio(aspectRatio);
                        postPhoto_2.setImageRequest(mImageRequest);
                        break;
                    case 2:
                        postPhoto_3.setAspectRatio(aspectRatio);
                        postPhoto_3.setImageRequest(mImageRequest);
                        break;
                }

                if(morePhoto.getListPhoto().size() > 3){

                    final String count = String.valueOf(morePhoto.getListPhoto().size() - 2);

                    postPhoto_3.getHierarchy().setOverlayImage(mContext.getResources().getDrawable(R.color.colorOverleyImage));
                    photoCountFrame.setVisibility(View.VISIBLE);
                    photoCountText.setText(count);
                } else {
                    postPhoto_3.getHierarchy().setOverlayImage(null);
                    photoCountFrame.setVisibility(View.GONE);
                    photoCountText.setText("");
                }
            }

            menuNewsItem.setOnClickListener(e -> showMenu(mContext, morePhoto.getSigner(), morePhoto.getUrlPost(), postText.getText().toString()));
            postPhoto_1.setOnClickListener(e -> {
                new PhotoViewSetter().setPhoto(mContext, morePhoto.getListPhoto(), 0);
            });
            postPhoto_2.setOnClickListener(e -> {
                new PhotoViewSetter().setPhoto(mContext, morePhoto.getListPhoto(), 1);
            });
            postPhoto_3.setOnClickListener(e -> {
                new PhotoViewSetter().setPhoto(mContext, morePhoto.getListPhoto(), 2);
            });
        }
    }

    public NewsItemsAdapter(ArrayList<Parcelable> mList, Context mContext) {
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
                final View viewHeader = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_news_item_header, parent, false);
                mViewHolder = new HeaderViewHolder(viewHeader);
            break;

            case ITEM_DONT_ATTACH:
                final View viewDontAttach = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_news_item_dont_attach, parent, false);
                mViewHolder = new DontAttachViewHolder(viewDontAttach);
            break;

            case ITEM_1_PHOTO:
                final View viewOnePhoto = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_news_item_one_photo, parent, false);
                mViewHolder = new OnePhotoViewHodler(viewOnePhoto);
            break;

            case ITEM_2_PHOTO:
                final View viewTwoPhoto = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_news_item_two_photo, parent, false);
                mViewHolder = new TwoPhotoViewHodler(viewTwoPhoto);
            break;

            case ITEM_MORE_PHOTO:
                final View viewMorePhoto = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_news_item_more_photo, parent, false);
                mViewHolder = new MorePhotoViewHodler(viewMorePhoto);
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
                final NewsItemsData_Header header = (NewsItemsData_Header)mList.get(position);
                ((HeaderViewHolder) holder).setHeader(header);
                break;
            case ITEM_DONT_ATTACH:
                final NewsItemsData_DontAttach dontAttach = (NewsItemsData_DontAttach) mList.get(position);
                ((DontAttachViewHolder) holder).setDontAttach(dontAttach, stringFormatter, mContext);
                break;
            case ITEM_1_PHOTO:
                final NewsItemsData onePhoto = (NewsItemsData) mList.get(position);
                ((OnePhotoViewHodler) holder).setOnePhoto(onePhoto, stringFormatter, mContext);
                break;
            case ITEM_2_PHOTO:
                final NewsItemsData twoPhoto = (NewsItemsData) mList.get(position);
                ((TwoPhotoViewHodler) holder).setTwoPhoto(twoPhoto, stringFormatter, mContext);
                break;
            case ITEM_MORE_PHOTO:
                final NewsItemsData morePhoto = (NewsItemsData) mList.get(position);
                ((MorePhotoViewHodler) holder).setMorePhoto(morePhoto, stringFormatter, mContext);
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
    public long getItemId(int position) {
        return mList.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    // Установить фото в ViewPager
    private static class PhotoViewSetter{

        void setPhoto(Context mContext, ArrayList<NewsItemsPhotoData> mList, int position){
            final Intent mIntent = new Intent(mContext, ViewPhotoActivity.class);

            if(mList.size() > 3 && position == 2)
                mIntent.putExtra("position", 0);
            else
                mIntent.putExtra("position", position);

            mIntent.putExtra("list", mList);
            mContext.startActivity(mIntent);
            if (!SettingsController.isEnabledAnim(mContext)) {
                ((Activity) mContext).overridePendingTransition(0, 0);
            }
        }
    }

    private static void showMenu (Context mContext,
                                  int signer,
                                  String urlPost,
                                  String postText) {

        final Menu mMenu = new MenuBuilder(mContext);
        new MenuInflater(mContext).inflate(R.menu.menu_news_item, mMenu);

        if(signer > 0)
            mMenu.findItem(R.id.menu_news_item_openAuthor).setEnabled(true);
        else
            mMenu.findItem(R.id.menu_news_item_openAuthor).setEnabled(false);

        final BottomSheetMenu bottomSheetMenu = new BottomSheetMenu(mContext, mMenu);
        bottomSheetMenu.setTitle(R.string.news_bottomsheetmenu_title);
        bottomSheetMenu.setAnimation(SettingsController.isEnabledAnim(mContext));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            bottomSheetMenu.setLightNavigationBar(true);
            bottomSheetMenu.setColorNavigationBar(R.color.colorBackground);
        }

        bottomSheetMenu.setOnItemClickListener(itemId -> {
            final ClipboardManager mClipboardManager = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);
            final ClipData mClipData;

            switch (itemId) {
                // Открыть оригинал
                case R.id.menu_news_item_openOriginal:
                    IntentHelper.openInBrowser(mContext, urlPost);
                    break;
                // Поделиться
                case R.id.menu_news_item_shareLink:
                    IntentHelper.shareText(mContext, urlPost);
                    break;
                // Скопировать ссылку
                case R.id.menu_news_item_copyLink:
                    mClipData = ClipData.newPlainText("post_link", urlPost);
                    mClipboardManager.setPrimaryClip(mClipData);
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.toast_isCopyLink), Toast.LENGTH_SHORT).show();
                    break;
                // Скопировать текст
                case R.id.menu_news_item_copyText:
                    mClipData = ClipData.newPlainText("post_text", postText);
                    mClipboardManager.setPrimaryClip(mClipData);
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.toast_isCopyText), Toast.LENGTH_SHORT).show();
                    break;
                // Открыть владельца
                case R.id.menu_news_item_openAuthor:
                    IntentHelper.openInBrowser(mContext, "https://vk.com/id" + signer);
                    break;
            }
        });
    }
}
