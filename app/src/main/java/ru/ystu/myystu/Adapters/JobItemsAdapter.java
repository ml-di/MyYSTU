package ru.ystu.myystu.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.ContentFrameLayout;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.R;
import ru.ystu.myystu.AdaptersData.JobItemsData;
import ru.ystu.myystu.Utils.FileInformation;
import ru.ystu.myystu.Utils.NetworkInformation;

public class JobItemsAdapter extends RecyclerView.Adapter<JobItemsAdapter.JobItemsViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback{

    private List<JobItemsData> mList;
    private Context context;
    private static AlertDialog.Builder alertDialog;

    static class JobItemsViewHolder extends RecyclerView.ViewHolder{

        private int id;
        final private AppCompatTextView organization;
        final private AppCompatTextView post;
        final private AppCompatTextView date;
        final private AppCompatTextView fileType;
        final private ConstraintLayout itemJob;
        final private LinearLayoutCompat itemJobDateLayout;
        final private String[] mAlertItems;


        JobItemsViewHolder(View itemView, final List<JobItemsData> mList, final Context context) {
            super(itemView);

            organization = itemView.findViewById(R.id.itemJob_organization);
            post = itemView.findViewById(R.id.itemJob_post);
            date = itemView.findViewById(R.id.itemJob_date);
            itemJob = itemView.findViewById(R.id.itemJob);
            itemJobDateLayout = itemView.findViewById(R.id.itemJob_date_layout);
            fileType = itemView.findViewById(R.id.itemJob_fileType);

            mAlertItems = new String[]{context.getResources().getString(R.string.alert_job_download), context.getResources().getString(R.string.alert_job_share)};

            itemJob.setOnClickListener(view -> {
                final String url = mList.get(id).getUrl();

                if(mList.get(id).getFileType().equals("FILE"))
                    mAlertItems[0] = context.getResources().getString(R.string.alert_job_download);
                else
                    mAlertItems[0] = context.getResources().getString(R.string.alert_job_openLink);

                // Диалоговое окно для вакансий
                alertDialog = new AlertDialog.Builder(context);
                alertDialog
                        .setTitle(mList.get(id).getOrganization())
                        .setItems(mAlertItems, (dialogInterface, i) -> {
                            switch (i){
                                // Скачать или открыть файл
                                case 0:

                                    if(NetworkInformation.hasConnection(context)){
                                        // Файл
                                        if(mList.get(id).getFileType().equals("FILE")){
                                            // Проверка на разрешение записи и чтения файлов
                                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                                            } else {

                                                new Thread(() -> {

                                                    final String fileType = FileInformation.getFileType(url);
                                                    String fileExtenstion = FileInformation.getExt(fileType);

                                                    if(!fileExtenstion.startsWith("."))
                                                        fileExtenstion = "." + fileExtenstion;

                                                    final String fileName = FileInformation.getFileName(fileType);

                                                    final DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                                                    request
                                                            .setTitle(fileName)
                                                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                                            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName + fileExtenstion)
                                                            .allowScanningByMediaScanner();

                                                    final DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                                                    if (manager != null) {
                                                        manager.enqueue(request);
                                                    }
                                                }).start();

                                            }
                                        }
                                        // Ссылка
                                        else {
                                            final Intent openLink = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                            context.startActivity(openLink);
                                        }
                                    } else
                                        Toast.makeText(context, context.getResources().getString(R.string.toast_dont_network), Toast.LENGTH_LONG).show();



                                    break;
                                // Поделиться файлом
                                case 1:

                                    final Intent sharePost = new Intent();
                                    sharePost
                                            .setAction(Intent.ACTION_SEND)
                                            .putExtra(Intent.EXTRA_TEXT, mList.get(id).getOrganization() + "\n" + url)
                                            .setType("text/plain");
                                    context.startActivity(sharePost);

                                    break;
                            }

                        })
                        .setNegativeButton(context.getResources().getString(R.string.alert_job_close), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .create()
                        .show();

            });
        }
    }

    public JobItemsAdapter(List<JobItemsData> mList, Context context) {
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
    public JobItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_job_item, parent, false);
        return new JobItemsViewHolder(v, mList, context);
    }

    @Override
    public void onBindViewHolder(@NonNull JobItemsViewHolder holder, int position) {
        holder.organization.setText(mList.get(position).getOrganization());
        holder.post.setText(mList.get(position).getPost());
        holder.fileType.setText(mList.get(position).getFileType());

        final String date = mList.get(position).getDate();
        if(Objects.equals(date, ""))
            holder.itemJobDateLayout.setVisibility(View.GONE);
        else{
            holder.itemJobDateLayout.setVisibility(View.VISIBLE);
            holder.date.setText(date);
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
                    Toast.makeText(context, "Разрешение успешно получено, повторите действие", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }
}