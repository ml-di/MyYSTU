package ru.ystu.myystu.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.Activitys.JobReaderActivity;
import ru.ystu.myystu.R;
import ru.ystu.myystu.AdaptersData.JobItemsData;
import ru.ystu.myystu.Utils.NetworkInformation;

public class JobItemsAdapter extends RecyclerView.Adapter<JobItemsAdapter.JobItemsViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static List<JobItemsData> mList;
    private Context mContext;

    static class JobItemsViewHolder extends RecyclerView.ViewHolder{

        private int id;
        final private AppCompatTextView organization;
        final private AppCompatTextView fileType;
        final private AppCompatImageView icon;
        final private AppCompatImageView menu;

        JobItemsViewHolder(View itemView, final List<JobItemsData> mList, final Context mContext) {
            super(itemView);

            organization = itemView.findViewById(R.id.itemJob_organization);
            fileType = itemView.findViewById(R.id.itemJob_fileType);
            icon  = itemView.findViewById(R.id.itemJob_icon);
            menu = itemView.findViewById(R.id.itemJob_menu);

            menu.setOnClickListener(view -> {
                new MenuItem().showMenu(view, mContext, getAdapterPosition());
            });
        }
    }

    public JobItemsAdapter(List<JobItemsData> mList, Context mContext) {
        JobItemsAdapter.mList = mList;
        this.mContext = mContext;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mContext = recyclerView.getContext();
    }

    @NonNull
    @Override
    public JobItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_job_item, parent, false);
        return new JobItemsViewHolder(mView, mList, mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull JobItemsViewHolder holder, int position) {
        holder.organization.setText(mList.get(position).getOrganization());

        if(mList.get(position).getFileType().equals("FILE")){
            holder.icon.setImageResource(R.drawable.ic_document);
            holder.fileType.setText(mList.get(position).getUrl().substring(mList.get(position).getUrl().lastIndexOf(".") + 1));
        } else {
            holder.icon.setImageResource(R.drawable.ic_document_text);
            holder.fileType.setText("");
        }

        holder.id = mList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
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
        private void showMenu (View mView, Context mContext, int id){

            final PopupMenu itemMenu = new PopupMenu(mView.getContext(), mView);
            itemMenu.inflate(R.menu.menu_job_item);

            final Menu mMenu = itemMenu.getMenu();
            if(mList.get(id).getFileType().equals("FILE")){
                mMenu.getItem(0).setTitle(R.string.alert_job_download);
                mMenu.getItem(1).setVisible(true);
            } else {
                mMenu.getItem(0).setTitle(R.string.alert_job_read);
                mMenu.getItem(1).setVisible(false);
            }

            itemMenu.setOnMenuItemClickListener(item -> {

                switch (item.getItemId()){
                    // Скачать / Читать
                    case R.id.menu_job_item_openLink:

                        if(mList.get(id).getFileType().equals("FILE")){
                            // Скачать
                            if(NetworkInformation.hasConnection(mContext)){

                                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                                } else {

                                    new Thread(() -> {

                                        String fileName = null;
                                        try {
                                            fileName = URLDecoder.decode(mList.get(id).getPost(), "UTF-8");
                                            fileName = fileName.replaceAll(" ", "_");
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }

                                        if(fileName != null){
                                            final DownloadManager.Request mRequest = new DownloadManager.Request(Uri.parse(mList.get(id).getUrl()));
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
                            readIntent.putExtra("content", mList.get(id).getPost());
                            readIntent.putExtra("title", mList.get(id).getOrganization());
                            mContext.startActivity(readIntent);

                        }

                        return true;
                    // Открыть в бразуере, если файл
                    case R.id.menu_job_item_openLinkInBrowser:

                        final Intent openLink = new Intent(Intent.ACTION_VIEW, Uri.parse(mList.get(id).getUrl()));
                        mContext.startActivity(openLink);

                        return true;
                    // Поделиться
                    case R.id.menu_job_item_share:

                        final String shareText;
                        if(mList.get(id).getFileType().equals("FILE")){
                            shareText = mList.get(id).getUrl();
                        } else {
                            shareText = Html.fromHtml(mList.get(id).getPost()).toString();
                        }

                        final Intent sharePost = new Intent();
                        sharePost
                                .setAction(Intent.ACTION_SEND)
                                .putExtra(Intent.EXTRA_TEXT, mList.get(id).getOrganization() + "\n\n" + shareText)
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
