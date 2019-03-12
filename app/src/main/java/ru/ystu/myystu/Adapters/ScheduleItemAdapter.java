package ru.ystu.myystu.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.view.LayoutInflater;
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
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.AdaptersData.ScheduleListItemData;
import ru.ystu.myystu.R;

public class ScheduleItemAdapter extends RecyclerView.Adapter<ScheduleItemAdapter.ScheduleItemViewHolder> {

    private ArrayList<ScheduleListItemData> mList;
    private Context mContext;

    static class ScheduleItemViewHolder extends RecyclerView.ViewHolder {

        final String[] prefix = new String[]{"asf", "ief", "af", "mf", "htf", "zf", "ozf"};

        private int id;
        private int id_pref;
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

                final File dir = new File(Environment.getExternalStorageDirectory(),
                        "/.MyYSTU/" + prefix[id_pref]);

                final String fileName = mList.get(id).getName() + "." + mList.get(id).getType();
                File file = new File(dir, fileName);

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

                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags (Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setAction(Intent.ACTION_VIEW);
                        String type = "application/msword";
                        intent.setDataAndType(Uri.fromFile(file), type);
                        mContext.startActivity(intent);
                    } catch (Exception e){
                        if(e.getMessage().startsWith("No Activity found to handle")){
                            Toast.makeText(mContext, mContext.getResources()
                                            .getString(R.string.schedule_file_not_open),
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(mContext, mContext.getResources()
                            .getString(R.string.schedule_file_not_found), Toast.LENGTH_SHORT).show();

            });

            // Меню элемента
            menu.setOnClickListener(view -> {

                final File dir = new File(Environment.getExternalStorageDirectory(),
                        "/.MyYSTU/" + prefix[id_pref]);

                final String fileName = mList.get(id).getName() + "." + mList.get(id).getType();
                File file = new File(dir, fileName);

                if(file.exists()){
                    new MenuItem().showMenu(view, mContext, file);
                } else
                    Toast.makeText(mContext, mContext.getResources()
                            .getString(R.string.schedule_file_not_found), Toast.LENGTH_SHORT).show();

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

        holder.id = mList.get(position).getId();
        holder.id_pref = mList.get(position).getId_pref();
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

    private static class MenuItem {
        private void showMenu (View mView, Context mContext, File file){

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
