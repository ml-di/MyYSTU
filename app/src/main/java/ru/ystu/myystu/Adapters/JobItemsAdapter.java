package ru.ystu.myystu.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.Activitys.JobReaderActivity;
import ru.ystu.myystu.R;
import ru.ystu.myystu.AdaptersData.JobItemsData;
import ru.ystu.myystu.Utils.BottomSheetMenu.BottomSheetMenu;
import ru.ystu.myystu.Utils.IntentHelper;
import ru.ystu.myystu.Utils.NetworkInformation;
import ru.ystu.myystu.Utils.SettingsController;

public class JobItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static final int ITEM_JOB = 1;

    private List<Parcelable> mList;
    private Context mContext;

    static class JobItemViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView organization;
        private AppCompatTextView fileType;
        private AppCompatImageView icon;
        private ConstraintLayout item;

        JobItemViewHolder(View itemView) {
            super(itemView);

            organization = itemView.findViewById(R.id.itemJob_organization);
            fileType = itemView.findViewById(R.id.itemJob_fileType);
            icon  = itemView.findViewById(R.id.itemJob_icon);
            item = itemView.findViewById(R.id.itemJob);

        }

        void setJob (JobItemsData jobItem, Context mContext) {

            organization.setText(jobItem.getOrganization());

            if(jobItem.getFileType().equals("FILE")){
                icon.setImageResource(R.drawable.ic_document);
                fileType.setText(jobItem.getUrl().substring(jobItem.getUrl().lastIndexOf(".") + 1));
            } else {
                icon.setImageResource(R.drawable.ic_document_text);
                fileType.setText("");
            }

            item.setOnClickListener(view -> showMenu(mContext, jobItem, organization.getText().toString()));
        }
    }

    public JobItemsAdapter(List<Parcelable> mList, Context mContext) {
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

        final RecyclerView.ViewHolder mViewHolder;

        switch (viewType) {

            case ITEM_JOB:
                final View viewJob = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_job_item, parent, false);
                mViewHolder = new JobItemViewHolder(viewJob);
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
            case ITEM_JOB:
                final JobItemsData job = (JobItemsData) mList.get(position);
                ((JobItemViewHolder) holder).setJob(job, mContext);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {

        int viewType;

        if (mList.get(position) instanceof JobItemsData) {
            viewType = ITEM_JOB;
        } else {
            viewType = -1;
        }

        return viewType;
    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).hashCode();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(mContext, "Разрешение успешно получено, повторите действие", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static void showMenu (Context mContext, JobItemsData jobItem, String title) {

        final Menu mMenu = new MenuBuilder(mContext);
        new MenuInflater(mContext).inflate(R.menu.menu_job_item, mMenu);

        if(jobItem.getFileType().equals("FILE")){
            mMenu.getItem(0).setTitle(R.string.menu_download);
            mMenu.getItem(1).setVisible(true);
            mMenu.getItem(0).setIcon(R.drawable.ic_download);
        } else {
            mMenu.getItem(0).setTitle(R.string.menu_detail);
            mMenu.getItem(1).setVisible(false);
            mMenu.getItem(0).setIcon(R.drawable.ic_detail);
        }

        final BottomSheetMenu bottomSheetMenu = new BottomSheetMenu(mContext, mMenu);
        bottomSheetMenu.setTitle(title);
        bottomSheetMenu.setAnimation(SettingsController.isEnabledAnim(mContext));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            bottomSheetMenu.setLightNavigationBar(!SettingsController.isDarkTheme(mContext));
            bottomSheetMenu.setColorNavigationBar(R.color.colorBackground);
        }
        bottomSheetMenu.setOnItemClickListener(itemId -> {
            switch (itemId) {
                // Скачать / Читать
                case R.id.menu_job_item_openLink:

                    if(jobItem.getFileType().equals("FILE")){
                        // Скачать
                        if(NetworkInformation.hasConnection()){

                            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                            } else {

                                new Thread(() -> {

                                    String fileName = null;
                                    try {
                                        fileName = URLDecoder.decode(jobItem.getPost(), "UTF-8");
                                        fileName = fileName.replaceAll(" ", "_");
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }

                                    if(fileName != null){
                                        final DownloadManager.Request mRequest = new DownloadManager.Request(Uri.parse(jobItem.getUrl()));
                                        mRequest
                                                .setTitle(fileName)
                                                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                                                .allowScanningByMediaScanner();

                                        final DownloadManager mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                                        if (mDownloadManager != null) {
                                            mDownloadManager.enqueue(mRequest);
                                        }
                                    }
                                }).start();
                            }

                        } else {
                            Toast.makeText(mContext,
                                    mContext.getResources()
                                            .getString(R.string.error_message_internet_error),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Прочитать

                        final Intent readIntent = new Intent(mContext, JobReaderActivity.class);
                        readIntent.putExtra("content", jobItem.getPost());
                        readIntent.putExtra("title", jobItem.getOrganization());
                        mContext.startActivity(readIntent);
                        if (SettingsController.isEnabledAnim(mContext)) {
                            ((Activity)mContext).overridePendingTransition(R.anim.activity_slide_right_show, R.anim.activity_slide_left_out);
                        } else {
                            ((Activity)mContext).overridePendingTransition(0, 0);
                        }
                    }

                    break;
                // Открыть в бразуере, если файл
                case R.id.menu_job_item_openLinkInBrowser:
                    IntentHelper.openInBrowser(mContext, jobItem.getUrl());
                    break;
                // Поделиться
                case R.id.menu_job_item_share:

                    final String shareText;
                    if(jobItem.getFileType().equals("FILE")){
                        shareText = jobItem.getUrl();
                    } else {
                        shareText = Html.fromHtml(jobItem.getPost()).toString();
                    }

                    IntentHelper.shareText(mContext, jobItem.getOrganization() + "\n\n" + shareText);
                    break;
            }
        });
    }
}
