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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.AdaptersData.EventAdditionalData_Additional;
import ru.ystu.myystu.AdaptersData.EventAdditionalData_Documents;
import ru.ystu.myystu.AdaptersData.StringData;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.NetworkInformation;

public class EventAdditionalItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static final int ITEM_ADDITIONAL = 0;
    private static final int ITEM_DOCUMENT = 1;
    private static final int ITEM_TITLE = 2;

    private List<Parcelable> mList;
    private Context mContext;

    static class AdditionalViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView title;
        private AppCompatTextView description;

        AdditionalViewHolder (View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.itemEventAdditional_title);
            description = itemView.findViewById(R.id.itemEventAdditional_description);


        }

        void setAdditional (EventAdditionalData_Additional additionalItem, Context mContext) {
            title.setText(additionalItem.getTitle());
            description.setText(additionalItem.getDescription());
        }
    }

    static class DocumentViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView name;
        private AppCompatTextView ext;
        private AppCompatImageView menu;

        DocumentViewHolder (View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.itemEventDocument_name);
            ext = itemView.findViewById(R.id.itemEventDocument_fileType);
            menu = itemView.findViewById(R.id.itemEventDocument_menu);
        }

        void setDocument (EventAdditionalData_Documents documentItem, Context mContext) {
            name.setText(documentItem.getTitle());
            ext.setText(documentItem.getExt());

            menu.setOnClickListener(View -> {
                new MenuItem().showMenu(View, mContext, documentItem.getLink(), documentItem.getTitle(), documentItem.getExt());
            });
        }
    }

    static class TitleViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView title;

        TitleViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.itemEventDivider);
        }

        void setTitle (StringData dividerItem) {
            title.setText(dividerItem.getTitle());
        }
    }

    public EventAdditionalItemsAdapter (List<Parcelable> mList, Context mContext) {
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
            case ITEM_ADDITIONAL:
                final View viewAdditional= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_event_additional_item, parent, false);
                mViewHolder = new AdditionalViewHolder(viewAdditional);
                break;

            case ITEM_DOCUMENT:
                final View viewDocument = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_event_additional_item_document, parent, false);
                mViewHolder = new DocumentViewHolder(viewDocument);
                break;

            case ITEM_TITLE:
                final View viewTitle = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_event_additional_title, parent, false);
                mViewHolder = new TitleViewHolder(viewTitle);
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
            case ITEM_ADDITIONAL:
                final EventAdditionalData_Additional additional = (EventAdditionalData_Additional) mList.get(position);
                ((AdditionalViewHolder) holder).setAdditional(additional, mContext);
                break;
            case ITEM_DOCUMENT:
                final EventAdditionalData_Documents document = (EventAdditionalData_Documents) mList.get(position);
                ((DocumentViewHolder) holder).setDocument(document, mContext);
                break;
            case ITEM_TITLE:
                final StringData title = (StringData) mList.get(position);
                ((TitleViewHolder) holder).setTitle(title);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;

        if (mList.get(position) instanceof EventAdditionalData_Additional) {
            viewType = ITEM_ADDITIONAL;
        } else if (mList.get(position) instanceof EventAdditionalData_Documents) {
            viewType = ITEM_DOCUMENT;
        } else if (mList.get(position) instanceof StringData) {
            viewType = ITEM_TITLE;
        } else{
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
        private void showMenu (View mView, Context mContext, String url, String name, String ext){

            final PopupMenu itemMenu = new PopupMenu(mView.getContext(), mView);
            itemMenu.inflate(R.menu.menu_event_full_document);

            itemMenu.setOnMenuItemClickListener(item -> {

                switch (item.getItemId()){

                    // Скачать файл
                    case R.id.menu_event_full_download:

                        if(NetworkInformation.hasConnection(mContext)){

                            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                            } else {

                                new Thread(() -> {

                                    final String fileName = name + "." + ext;
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

                                }).start();
                            }

                        } else {
                            Toast.makeText(mContext,
                                    mContext.getResources()
                                            .getString(R.string.error_message_internet_error),
                                    Toast.LENGTH_SHORT).show();
                        }

                        return true;

                    // Открыть в браузере
                    case R.id.menu_event_full_openLinkInBrowser:

                        final Intent openLink = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        mContext.startActivity(openLink);

                        return true;

                    // Поделиться ссылкой
                    case R.id.menu_event_full_share:

                        final Intent shareLink = new Intent();
                        shareLink
                                .setAction(Intent.ACTION_SEND)
                                .putExtra(Intent.EXTRA_TEXT, name + "\n\n" + url)
                                .setType("text/plain");
                        mContext.startActivity(shareLink);

                        return true;
                }
                return false;
            });
            itemMenu.show();
        }
    }
}
