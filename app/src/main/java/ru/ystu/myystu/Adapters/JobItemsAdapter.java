package ru.ystu.myystu.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.Activitys.JobReaderActivity;
import ru.ystu.myystu.AdaptersData.ToolbarPlaceholderData;
import ru.ystu.myystu.R;
import ru.ystu.myystu.AdaptersData.JobItemsData;
import ru.ystu.myystu.Utils.NetworkInformation;
import ru.ystu.myystu.Utils.SettingsController;

public class JobItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static final int ITEM_TOOLBAR_PLACEHOLDER = 0;
    private static final int ITEM_JOB = 1;

    private List<Parcelable> mList;
    private Context mContext;

    static class PlaceholderViewHolder extends RecyclerView.ViewHolder {

        PlaceholderViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void setPlaceholder (ToolbarPlaceholderData placeholderItem) {

        }
    }

    static class JobItemViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView organization;
        private AppCompatTextView fileType;
        private AppCompatImageView icon;
        private AppCompatImageView menu;

        JobItemViewHolder(View itemView) {
            super(itemView);

            organization = itemView.findViewById(R.id.itemJob_organization);
            fileType = itemView.findViewById(R.id.itemJob_fileType);
            icon  = itemView.findViewById(R.id.itemJob_icon);
            menu = itemView.findViewById(R.id.itemJob_menu);

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

            menu.setOnClickListener(view -> {
                new MenuItem().showMenu(view, mContext, jobItem);
            });
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

        RecyclerView.ViewHolder mViewHolder;

        switch (viewType) {
            case ITEM_TOOLBAR_PLACEHOLDER:
                final View viewPlaceholder = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_toolbar_placeholder, parent, false);
                mViewHolder = new PlaceholderViewHolder(viewPlaceholder);
                break;

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
            case ITEM_TOOLBAR_PLACEHOLDER:
                final ToolbarPlaceholderData placeholder = (ToolbarPlaceholderData) mList.get(position);
                ((PlaceholderViewHolder) holder).setPlaceholder(placeholder);
                break;
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

        if (mList.get(position) instanceof ToolbarPlaceholderData) {
            viewType = ITEM_TOOLBAR_PLACEHOLDER;
        } else if (mList.get(position) instanceof JobItemsData) {
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
        switch (requestCode) {
            case 0:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(mContext, "Разрешение успешно получено, повторите действие", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }

    private static class MenuItem {
        private void showMenu (View mView, Context mContext, JobItemsData jobItem){

            final PopupMenu itemMenu = new PopupMenu(mView.getContext(), mView);
            itemMenu.inflate(R.menu.menu_job_item);

            final Menu mMenu = itemMenu.getMenu();
            if(jobItem.getFileType().equals("FILE")){
                mMenu.getItem(0).setTitle(R.string.menu_download);
                mMenu.getItem(1).setVisible(true);
            } else {
                mMenu.getItem(0).setTitle(R.string.menu_detail);
                mMenu.getItem(1).setVisible(false);
            }

            itemMenu.setOnMenuItemClickListener(item -> {

                switch (item.getItemId()){
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

                        return true;
                    // Открыть в бразуере, если файл
                    case R.id.menu_job_item_openLinkInBrowser:

                        final Intent openLink = new Intent(Intent.ACTION_VIEW, Uri.parse(jobItem.getUrl()));
                        mContext.startActivity(openLink);

                        return true;
                    // Поделиться
                    case R.id.menu_job_item_share:

                        final String shareText;
                        if(jobItem.getFileType().equals("FILE")){
                            shareText = jobItem.getUrl();
                        } else {
                            shareText = Html.fromHtml(jobItem.getPost()).toString();
                        }

                        final Intent sharePost = new Intent();
                        sharePost
                                .setAction(Intent.ACTION_SEND)
                                .putExtra(Intent.EXTRA_TEXT, jobItem.getOrganization() + "\n\n" + shareText)
                                .setType("text/plain");
                        mContext.startActivity(sharePost);

                        return true;
                }
                return false;
            });
            itemMenu.show();
        }
    }
}
