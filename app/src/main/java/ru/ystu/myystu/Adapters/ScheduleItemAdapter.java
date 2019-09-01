package ru.ystu.myystu.Adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
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
import ru.ystu.myystu.Utils.BottomSheetMenu.BottomSheetMenu;
import ru.ystu.myystu.Utils.NetworkInformation;
import ru.ystu.myystu.Utils.IntentHelper;
import ru.ystu.myystu.Utils.SettingsController;

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

        final String[] prefix = new String[]{"asf_ist", "asf_ad", "ief", "af", "mf", "htf", "zf", "ozf"};

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
                    IntentHelper.openFile(mContext, file);
                }

            });

            // Меню элемента
            menu.setOnClickListener(view -> {

                final File dir_menu = new File(Environment.getExternalStorageDirectory(),
                        "/.MyYSTU/" + prefix[id]);

                final String ext_menu = link.substring(link.lastIndexOf("."));
                final File file_menu = new File(dir_menu, fileName + ext_menu);

                showMenu(fileName, getAdapterPosition(), mContext, file_menu, link);
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
        if (requestCode == 0) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(mContext, "Разрешение успешно получено, повторите действие", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static void downloadFile(File file, String link, Context mContext, int position, int id) {

        if(NetworkInformation.hasConnection()){

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
                                IntentHelper.openFile(mContext, file);
                                break;

                            case 1:
                                if(file.exists()){
                                    final String titleIntent = mContext.getResources().getString(R.string.intent_schedule_share_doc);
                                    final String subject = mContext.getResources().getString(R.string.menu_text_schedule);
                                    IntentHelper.shareFile(mContext, file, titleIntent, subject, file.getName());
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

    private static void showMenu (String title, int pos, Context mContext, File file, String link) {

        final Menu mMenu = new MenuBuilder(mContext);
        new MenuInflater(mContext).inflate(R.menu.menu_schedule_item, mMenu);

        if(file.exists()){
            mMenu.getItem(0).setTitle(R.string.menu_delete);
            mMenu.getItem(0).setIcon(R.drawable.ic_delete);
        } else {
            mMenu.getItem(0).setTitle(R.string.menu_download);
            mMenu.getItem(0).setIcon(R.drawable.ic_download);
        }

        final BottomSheetMenu bottomSheetMenu = new BottomSheetMenu(mContext, mMenu);
        bottomSheetMenu.setTitle(title);
        bottomSheetMenu.setAnimation(SettingsController.isEnabledAnim(mContext));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            bottomSheetMenu.setLightNavigationBar(true);
            bottomSheetMenu.setColorNavigationBar(R.color.colorBackground);
        }
        bottomSheetMenu.setOnItemClickListener(itemId -> {
            switch (itemId) {
                case R.id.menu_schedule_item_download:

                    if (file.exists()) {
                        // Удалить
                        if (file.delete()) {
                            ((ScheduleListActivity) mContext).updateItem(pos);
                        } else {
                            Toast.makeText(mContext, mContext
                                    .getResources()
                                    .getString(R.string.toast_errorDeleteFile), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } else {
                        // Скачать
                        downloadFile(file, link, mContext, pos, -1);
                    }

                    break;

                // Открыть в браузере
                case R.id.menu_schedule_item_openInBrowser:
                    IntentHelper.openInBrowser(mContext, link);
                    break;
                // Поделиться файлом
                case R.id.menu_schedule_item_shareFile:

                    if (file.exists()) {
                        final String titleIntent = mContext.getResources().getString(R.string.intent_schedule_share_doc);
                        final String subject = mContext.getResources().getString(R.string.menu_text_schedule);
                        IntentHelper.shareFile(mContext, file, titleIntent, subject, title);
                    } else {
                        downloadFile(file, link, mContext, pos, 1);
                    }
                    break;
                // Поделиться ссылкой
                case R.id.menu_schedule_item_shareLink:
                    IntentHelper.shareText(mContext, title + "\n\n" + link);
                    break;
            }
        });
    }
}
