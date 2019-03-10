package ru.ystu.myystu.Adapters;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.AdaptersData.ScheduleListItemData;
import ru.ystu.myystu.R;

public class ScheduleItemAdapter extends RecyclerView.Adapter<ScheduleItemAdapter.ScheduleItemViewHolder> {

    private ArrayList<ScheduleListItemData> mList;
    private Context mContext;

    static class ScheduleItemViewHolder extends RecyclerView.ViewHolder {

        final String[] prefix = new String[]{"asf", "ief", "af", "mf", "htf", "zf", "ozf"};

        private int id;
        private AppCompatTextView text;
        private AppCompatTextView size;
        private AppCompatTextView type;
        private ConstraintLayout item;
        private AppCompatImageView menu;

        ScheduleItemViewHolder(@NonNull View itemView, final ArrayList<ScheduleListItemData> mList, final Context mContext) {
            super(itemView);

            text = itemView.findViewById(R.id.schedule_item_text);
            size = itemView.findViewById(R.id.schedule_item_size);
            type = itemView.findViewById(R.id.schedule_item_type);
            item = itemView.findViewById(R.id.schedule_item);
            menu = itemView.findViewById(R.id.menu_schedule_item);

            // Открыть расписание
            item.setOnClickListener(view -> {

                //final File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                final File dir = new File(Environment.getRootDirectory() + ".MyYSTU");
                final File dirOut = Environment.getExternalStoragePublicDirectory(dir + "\\ystu_temp");
                final File file = new File(dir, prefix[id] + ".zip");
                final File fileOut = new File(dirOut, mList.get(id).getName() + "." + mList.get(id).getType());

                if(createTempDir(dir)){
                    try {
                        unzip(file, fileOut, id);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });

            // Меню элемента
            menu.setOnClickListener(view -> {
                new MenuItem().showMenu(view, mContext, id);
            });
        }
    }

    public ScheduleItemAdapter(ArrayList<ScheduleListItemData> mList, Context mContext) {
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
    public ScheduleItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_schedule_item, parent, false);
        return new ScheduleItemViewHolder(v, mList, mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleItemViewHolder holder, int position) {
        holder.text.setText(mList.get(position).getName());
        holder.size.setText(mList.get(position).getSize());
        holder.type.setText(mList.get(position).getType());
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
        return super.getItemViewType(position);
    }

    private static void unzip(File zipFile, File fileOut, int id) throws IOException {

        final ZipInputStream mZipInputStream;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            mZipInputStream = new ZipInputStream(
                    new BufferedInputStream(new FileInputStream(zipFile)), Charset.forName("CP866"));
        } else{
            mZipInputStream = new ZipInputStream(
                    new BufferedInputStream(new FileInputStream(zipFile)));
        }

        try {
            int read = 0;
            byte[] buffer = new byte[8192];

            FileOutputStream mFileOutputStream = new FileOutputStream(fileOut);
            try {
                while ((read = mZipInputStream.read(buffer)) != -1)
                    mFileOutputStream.write(buffer, 0, read);
            } finally {
                mFileOutputStream.close();
            }
        } finally {
            mZipInputStream.close();
        }
    }

    private static boolean createTempDir (File dir){

        if(dir.exists()){
            // Зачистить
            final String[] files = dir.list();
            for(String path:files){
                File mFile = new File(dir.getPath(), path);
                mFile.delete();
            }
            return true;
        } else
            return dir.mkdir();

    }

    private static class MenuItem {
        private void showMenu (View mView, Context mContext, int id){

            final PopupMenu itemMenu = new PopupMenu(mView.getContext(), mView);
            itemMenu.inflate(R.menu.menu_schedule_item);

            itemMenu.setOnMenuItemClickListener(item -> {

                switch (item.getItemId()){
                    case R.id.menu_schedule_item_shareLink:

                        return true;
                }

                return false;
            });

            itemMenu.show();
        }
    }
}
