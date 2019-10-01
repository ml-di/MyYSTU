package ru.ystu.myystu.Adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Parcelable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.Objects;
import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import ru.ystu.myystu.R;
import ru.ystu.myystu.AdaptersData.NewsItemsData;
import ru.ystu.myystu.AdaptersData.NewsItemsData_DontAttach;
import ru.ystu.myystu.Utils.BottomSheetMenu.BottomSheetMenu;
import ru.ystu.myystu.Utils.FrescoHelper;
import ru.ystu.myystu.Utils.IntentHelper;
import ru.ystu.myystu.Utils.NewsPhotoPagerTransformer;
import ru.ystu.myystu.Utils.PhotoViewSetter;
import ru.ystu.myystu.Utils.SettingsController;
import ru.ystu.myystu.Utils.StringFormatter;
import ru.ystu.myystu.Utils.UnixToString;

import static android.content.Context.CLIPBOARD_SERVICE;

public class NewsItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_DONT_ATTACH = 0;
    private static final int ITEM_ONE_PHOTO = 1;
    private static final int ITEM_MORE_PHOTO = 2;

    private ArrayList<Parcelable> mList;
    private Context mContext;
    private StringFormatter stringFormatter = new StringFormatter();

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

            postPhoto.setAspectRatio(aspectRatio);
            if (SettingsController.isImageDownload(mContext)) {
                postPhoto.setImageRequest(FrescoHelper.getImageRequest(mContext, onePhoto.getListPhoto().get(0).getUrlPreview()));
                postPhoto.setOnClickListener(e -> PhotoViewSetter.setPhoto(mContext, onePhoto.getListPhoto().get(0).getUrlFull()));
            }

            menuNewsItem.setOnClickListener(e -> showMenu(mContext, onePhoto.getSigner(), onePhoto.getUrlPost(), postText.getText().toString()));
        }

    }

    static class MorePhotoViewHodler extends RecyclerView.ViewHolder{

        private AppCompatTextView postDate;
        private AppCompatTextView postText;
        private ConstraintLayout postPin;
        private AppCompatImageView menuNewsItem;
        private ViewPager pager;
        private TabLayout pagerIndicator;

        private UnixToString unixToString = new UnixToString();

        MorePhotoViewHodler(@NonNull View itemView) {
            super(itemView);

            postDate = itemView.findViewById(R.id.post_date);
            postText = itemView.findViewById(R.id.post_text);
            postPin = itemView.findViewById(R.id.post_pin);
            menuNewsItem = itemView.findViewById(R.id.menu_news_item);
            pager = itemView.findViewById(R.id.post_viewPager);
            pagerIndicator = itemView.findViewById(R.id.post_viewPagerIndicator);

        }

        void setMorePhoto(NewsItemsData morePhoto, StringFormatter stringFormatter, Context mContext){

            postText.setText(stringFormatter.getFormattedString(morePhoto.getText()));
            postText.setMovementMethod(LinkMovementMethod.getInstance());
            postDate.setText(unixToString.setUnixToString(morePhoto.getDate(), mContext));

            if(Objects.equals(morePhoto.getIsPinned(), 1))
                postPin.setVisibility(View.VISIBLE);
            else
                postPin.setVisibility(View.GONE);


            final NewsItemPhotoPagerAdapter adapter = new NewsItemPhotoPagerAdapter(morePhoto.getListPhoto(), mContext);
            pager.setAdapter(adapter);
            pager.setPageTransformer(true, new NewsPhotoPagerTransformer());
            pagerIndicator.setupWithViewPager(pager);

            for (int i = 0; i < pagerIndicator.getTabCount(); i++) {
                pagerIndicator.getTabAt(i).setCustomView(adapter.getTabView(i ,pagerIndicator.getTabAt(i).isSelected()));
            }

            pagerIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    adapter.tabSelected(tab);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    adapter.tabUnselected(tab);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            menuNewsItem.setOnClickListener(e -> showMenu(mContext, morePhoto.getSigner(), morePhoto.getUrlPost(), postText.getText().toString()));
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

            case ITEM_DONT_ATTACH:
                final View viewDontAttach = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_news_item_dont_attach, parent, false);
                mViewHolder = new DontAttachViewHolder(viewDontAttach);
            break;

            case ITEM_ONE_PHOTO:
                final View viewOnePhoto = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_news_item_one_photo, parent, false);
                mViewHolder = new OnePhotoViewHodler(viewOnePhoto);
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
            case ITEM_DONT_ATTACH:
                final NewsItemsData_DontAttach dontAttach = (NewsItemsData_DontAttach) mList.get(position);
                ((DontAttachViewHolder) holder).setDontAttach(dontAttach, stringFormatter, mContext);
                break;
            case ITEM_ONE_PHOTO:
                final NewsItemsData onePhoto = (NewsItemsData) mList.get(position);
                ((OnePhotoViewHodler) holder).setOnePhoto(onePhoto, stringFormatter, mContext);
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

        if (mList.get(position) instanceof NewsItemsData_DontAttach) {
            viewType = ITEM_DONT_ATTACH;
        } else if(mList.get(position) instanceof NewsItemsData
                && ((NewsItemsData) mList.get(position)).getListPhoto().size() == 1){
            viewType = ITEM_ONE_PHOTO;
        } else if(mList.get(position) instanceof NewsItemsData
                && ((NewsItemsData) mList.get(position)).getListPhoto().size() > 1){
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
            bottomSheetMenu.setLightNavigationBar(!SettingsController.isDarkTheme(mContext));
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
