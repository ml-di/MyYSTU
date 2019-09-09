package ru.ystu.myystu.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

import ru.ystu.myystu.Activitys.StoActivity;
import ru.ystu.myystu.AdaptersData.StoItemsData_Doc;
import ru.ystu.myystu.AdaptersData.StoItemsData_Subtitle;
import ru.ystu.myystu.AdaptersData.StoItemsData_Title;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.BottomSheetMenu.BottomSheetMenu;
import ru.ystu.myystu.Utils.IntentHelper;
import ru.ystu.myystu.Utils.NetworkInformation;
import ru.ystu.myystu.Utils.SettingsController;

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
        AppCompatImageView downloadIcon;

        DocViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.itemStoDoc_item);
            fileName = itemView.findViewById(R.id.itemStoDoc_name);
            fileExt = itemView.findViewById(R.id.itemStoDoc_fileType);
            summary = itemView.findViewById(R.id.itemStoDoc_summary);
            downloadIcon = itemView.findViewById(R.id.itemStoDoc_download_icon);
        }

        void setDoc (StoItemsData_Doc docItem, Context mContext) {

            final String fileNamePath = docItem.getFileName().replaceAll(" ", "_") + "." + docItem.getFileExt();
            final File file = new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS, fileNamePath);

            if (file.exists()) {
                downloadIcon.setVisibility(View.VISIBLE);
            } else {
                downloadIcon.setVisibility(View.GONE);
            }

            fileName.setText(docItem.getFileName());
            fileExt.setText(docItem.getFileExt());
            if (docItem.getSummary() != null) {
                summary.setVisibility(View.VISIBLE);
                summary.setText(docItem.getSummary());
            } else {
                summary.setVisibility(View.GONE);
            }

            item.setOnClickListener(v -> showMenu(mContext, docItem, fileNamePath, file, getAdapterPosition()));
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

    private static void showMenu (Context mContext, StoItemsData_Doc docData, String fileName, File file, int pos) {

        final Menu mMenu = new MenuBuilder(mContext);
        new MenuInflater(mContext).inflate(R.menu.menu_sto_item, mMenu);
        if(file.exists()){
            mMenu.findItem(R.id.menu_sto_item_open).setVisible(true);
            mMenu.findItem(R.id.menu_sto_item_download).setTitle(R.string.menu_delete);
            mMenu.findItem(R.id.menu_sto_item_download).setIcon(R.drawable.ic_delete);
        } else {
            mMenu.findItem(R.id.menu_sto_item_open).setVisible(false);
            mMenu.findItem(R.id.menu_sto_item_download).setTitle(R.string.menu_download);
            mMenu.findItem(R.id.menu_sto_item_download).setIcon(R.drawable.ic_download);
        }

        final BottomSheetMenu bottomSheetMenu = new BottomSheetMenu(mContext, mMenu);
        bottomSheetMenu.setTitle(docData.getFileName());
        bottomSheetMenu.setAnimation(SettingsController.isEnabledAnim(mContext));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            bottomSheetMenu.setLightNavigationBar(!SettingsController.isDarkTheme(mContext));
            bottomSheetMenu.setColorNavigationBar(R.color.colorBackground);
        }
        bottomSheetMenu.setOnItemClickListener(itemId -> {
            switch (itemId) {
                // Открыть
                case R.id.menu_sto_item_open:
                    IntentHelper.openFile(mContext, file);
                    break;
                // Скачать / Удалить
                case R.id.menu_sto_item_download:

                    if (file.exists()) {
                        // Удалить
                        if (file.delete()) {
                            ((StoActivity) mContext).updateItem(pos);
                        } else {
                            Toast.makeText(mContext, mContext
                                    .getString(R.string.toast_errorDeleteFile), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } else {
                        // Скачать
                        if(NetworkInformation.hasConnection()) {
                            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                            } else {
                                downloadFile(mContext, fileName, docData.getUrl(), pos, false, file, docData);
                            }
                        } else {
                            Toast.makeText(mContext,
                                    mContext.getResources()
                                            .getString(R.string.error_message_internet_error),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                // Открыть в браузере
                case R.id.menu_sto_item_openLinkInBrowser:
                    IntentHelper.openInBrowser(mContext, docData.getUrl());
                    break;
                // Поделиться ссылкой
                case R.id.menu_sto_item_shareLink:
                    IntentHelper.shareText(mContext, docData.getFileName() + "\n\n" + docData.getUrl());
                    break;
                // Поделиться файлом
                case R.id.menu_sto_item_shareFile:
                    if (file.exists()) {
                        final String titleIntent = mContext.getString(R.string.intent_schedule_share_doc);
                        IntentHelper.shareFile(mContext, file, titleIntent, docData.getFileName(), docData.getSummary());
                    } else {
                        if(NetworkInformation.hasConnection()) {
                            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                            } else {
                                downloadFile(mContext, fileName, docData.getUrl(), pos, true, file, docData);
                            }
                        } else {
                            Toast.makeText(mContext,
                                    mContext.getResources()
                                            .getString(R.string.error_message_internet_error),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        });
    }

    private static void downloadFile(Context mContext, String fileName, String url, int pos, boolean isShare, File file, StoItemsData_Doc docData) {

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            new Thread(() -> {
                if(url != null){

                    BroadcastReceiver onComplete = null;
                    try {
                        final DownloadManager.Request mRequest = new DownloadManager.Request(Uri.parse(url));
                        mRequest
                                .setTitle(fileName)
                                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                                .allowScanningByMediaScanner();

                        final DownloadManager mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                        if (mDownloadManager != null) {
                            mDownloadManager.enqueue(mRequest);
                        }

                        // Завершение загрузки
                        onComplete = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                ((StoActivity) mContext).updateItem(pos);
                                if (isShare) {
                                    final String titleIntent = mContext.getString(R.string.intent_schedule_share_doc);
                                    IntentHelper.shareFile(mContext, file, titleIntent, docData.getFileName(), docData.getSummary());
                                }
                                context.unregisterReceiver(this);
                            }
                        };
                        mContext.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    } catch (Exception e) {
                        if(onComplete != null)
                            mContext.unregisterReceiver(onComplete);
                    }
                }
            }).start();
        }
    }
}
