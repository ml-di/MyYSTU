package ru.ystu.myystu.Adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.ystu.myystu.Activitys.ScheduleListActivity;
import ru.ystu.myystu.AdaptersData.ScheduleListItemData;
import ru.ystu.myystu.AdaptersData.ToolbarPlaceholderData;
import ru.ystu.myystu.Network.LoadScheduleFromURL;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.NetworkInformation;

public class ScheduleItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int ITEM_TOOLBAR_PLACEHOLDER = 0;
    private static final int ITEM_SCHEDULE = 1;

    private ArrayList<Parcelable> mList;
    private Context mContext;

    static class PlaceholderViewHolder extends RecyclerView.ViewHolder {

        PlaceholderViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void setPlaceholder (ToolbarPlaceholderData placeholderItem) {

        }
    }

    static class ScheduleItemViewHolder extends RecyclerView.ViewHolder {

        final String[] prefix = new String[]{"asf", "ief", "af", "mf", "htf", "zf", "ozf"};

        private int id;
        private String fileName;
        private String link;

        private AppCompatTextView text;
        private ConstraintLayout item;
        private AppCompatImageView menu;
        private AppCompatImageView downloadIcon;

        ScheduleItemViewHolder(@NonNull View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.schedule_item_text);
            item = itemView.findViewById(R.id.schedule_item);
            menu = itemView.findViewById(R.id.menu_schedule_item);
            downloadIcon = itemView.findViewById(R.id.schedule_item_download_icon);

        }

        void setSchedule (ScheduleListItemData scheduleItem, Context mContext) {

            text.setText(scheduleItem.getName());
            id = scheduleItem.getId();
            fileName = scheduleItem.getName();
            link = scheduleItem.getLink();

            File dir;
            File file;
            String ext;

            dir = new File(Environment.getExternalStorageDirectory(),
                    "/.MyYSTU/" + prefix[scheduleItem.getId()]);
            ext = scheduleItem.getLink().substring(scheduleItem.getLink().lastIndexOf("."));
            file = new File(dir, scheduleItem.getName() + ext);

            if(file.exists()){
                downloadIcon.setVisibility(View.VISIBLE);
            } else {
                downloadIcon.setVisibility(View.GONE);
            }

            // Открыть расписание
            item.setOnClickListener(view -> {

                final File dir_item = new File(Environment.getExternalStorageDirectory(),
                        "/.MyYSTU/" + prefix[id]);

                final String ext_item = link.substring(link.lastIndexOf("."));
                final File file_item = new File(dir_item, fileName + ext_item);

                if(!file_item.exists()) {
                    // Скачать файл
                    downloadFile(file_item, link, mContext, getAdapterPosition(), 0);
                } else {
                    // Открыть файл
                    openFile(file_item, mContext);
                }

            });

            // Меню элемента
            menu.setOnClickListener(view -> {

                final File dir_menu = new File(Environment.getExternalStorageDirectory(),
                        "/.MyYSTU/" + prefix[id]);

                final String ext_menu = link.substring(link.lastIndexOf("."));
                final File file_menu = new File(dir_menu, fileName + ext_menu);

                new MenuItem().showMenu(view, mContext, file_menu, link, fileName, getAdapterPosition());

            });
        }
    }

    public ScheduleItemAdapter(ArrayList<Parcelable> mList, Context mContext) {
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

            case ITEM_SCHEDULE:
                final View viewSchedule = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_schedule_item, parent, false);
                mViewHolder = new ScheduleItemViewHolder(viewSchedule);
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
            case ITEM_SCHEDULE:
                final ScheduleListItemData schedule = (ScheduleListItemData) mList.get(position);
                ((ScheduleItemViewHolder) holder).setSchedule(schedule, mContext);
                break;
        }
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
    public int getItemViewType(int position) {

        int viewType;

        if (mList.get(position) instanceof ToolbarPlaceholderData) {
            viewType = ITEM_TOOLBAR_PLACEHOLDER;
        } else if (mList.get(position) instanceof ScheduleListItemData) {
            viewType = ITEM_SCHEDULE;
        } else {
            viewType = -1;
        }

        return viewType;
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

    private static void downloadFile(File file, String link, Context mContext, int position, int id) {

        if(NetworkInformation.hasConnection(mContext)){

            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            } else {

                final LoadScheduleFromURL loadScheduleFromURL = new LoadScheduleFromURL();
                final Completable mCompletableLoadSchedule = loadScheduleFromURL.getCompletableSchedule(link, file, mContext);
                final CompletableObserver mObserver = new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                        /*
                        *   ID
                        *   -1 - Ничего не делать
                        *   0 - Открыть
                        *   1 - Поделиться
                        * */

                        switch (id) {

                            case 0:
                                openFile(file, mContext);
                                break;

                            case 1:
                                if(file.exists()){
                                    final Intent mIntent = new Intent(Intent.ACTION_SEND);
                                    mIntent.setType("application/msword");
                                    mIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file));
                                    mContext.startActivity(Intent.createChooser(mIntent,
                                            mContext.getResources()
                                                    .getString(R.string.intent_schedule_share_doc)));
                                }
                                break;

                            default:
                                break;
                        }

                        ((ScheduleListActivity) mContext).updateItem(position);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                };

                mCompletableLoadSchedule
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(mObserver);
            }

        } else {
            Toast.makeText(mContext,
                    mContext.getResources()
                            .getString(R.string.error_message_internet_error),
                    Toast.LENGTH_SHORT).show();
        }
    }
    private static void openFile(File file, Context mContext) {

        if(file.exists()){
            try{

                if(Build.VERSION.SDK_INT >= 24){
                    try{
                        Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                        m.invoke(null);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

                final Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags (Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setAction(Intent.ACTION_VIEW);
                final String type = "application/msword";
                intent.setDataAndType(Uri.fromFile(file), type);
                mContext.startActivity(intent);

            } catch (Exception e){
                if(e.getMessage().startsWith("No Activity found to handle")){
                    Toast.makeText(mContext, mContext.getResources()
                                    .getString(R.string.schedule_file_not_open),
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(mContext, mContext.getResources()
                    .getString(R.string.error_message_schedule_file_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    private static class MenuItem {
        private void showMenu (View mView, Context mContext, File file, String link, String name, int position){

            final PopupMenu itemMenu = new PopupMenu(mView.getContext(), mView);
            itemMenu.inflate(R.menu.menu_schedule_item);

            final Menu mMenu = itemMenu.getMenu();
            if(file.exists()){
                mMenu.getItem(0).setTitle(R.string.menu_delete);
            } else {
                mMenu.getItem(0).setTitle(R.string.menu_download);
            }

            itemMenu.setOnMenuItemClickListener(item -> {

                switch (item.getItemId()){

                    case R.id.menu_schedule_item_download:

                        if(file.exists()) {
                            // Удалить
                            if(file.delete()) {
                                ((ScheduleListActivity) mContext).updateItem(position);
                            } else {
                                Toast.makeText(mContext, mContext
                                                .getResources()
                                                .getString(R.string.toast_errorDeleteFile), Toast.LENGTH_SHORT)
                                        .show();
                            }

                        } else {
                            // Скачать
                            downloadFile(file, link, mContext, position, -1);
                        }

                        break;

                    // Открыть в браузере
                    case R.id.menu_schedule_item_openInBrowser:

                        final Intent openLink = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                        mContext.startActivity(openLink);

                        return true;

                    // Поделиться файлом
                    case R.id.menu_schedule_item_shareFile:

                        if(file.exists()){
                            final Intent mIntent = new Intent(Intent.ACTION_SEND);
                            mIntent.setType("application/msword");
                            mIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file));
                            mContext.startActivity(Intent.createChooser(mIntent,
                                    mContext.getResources()
                                            .getString(R.string.intent_schedule_share_doc)));
                        } else {
                            downloadFile(file, link, mContext, position, 1);
                        }
                        return true;

                    // Поделиться ссылкой
                    case R.id.menu_schedule_item_shareLink:

                        final Intent shareLink = new Intent();
                        shareLink
                                .setAction(Intent.ACTION_SEND)
                                .putExtra(Intent.EXTRA_TEXT, name + "\n\n" + link)
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
