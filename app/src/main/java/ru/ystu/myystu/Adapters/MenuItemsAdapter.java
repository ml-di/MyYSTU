package ru.ystu.myystu.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.Activitys.JobActivity;
import ru.ystu.myystu.Activitys.MapActivity;
import ru.ystu.myystu.Activitys.EventActivity;
import ru.ystu.myystu.Activitys.UsersActivity;
import ru.ystu.myystu.Activitys.ScheduleActivity;
import ru.ystu.myystu.R;
import ru.ystu.myystu.AdaptersData.MenuItemsData;
import ru.ystu.myystu.Utils.SettingsController;

public class MenuItemsAdapter extends RecyclerView.Adapter<MenuItemsAdapter.MenuItemsViewHolder> {

    private ArrayList<MenuItemsData> mList;
    private Context mContext;

    static class MenuItemsViewHolder extends RecyclerView.ViewHolder{

        private int id;
        private final AppCompatTextView text;
        private final AppCompatImageView icon;
        private final CardView itemMenu;

        MenuItemsViewHolder(View itemView, final Context mContext) {
            super(itemView);


            text = itemView.findViewById(R.id.itemMenu_text);
            icon = itemView.findViewById(R.id.itemMenu_icon);
            itemMenu = itemView.findViewById(R.id.itemMenu);

            // Обработчик нажатий пунктов меню
            itemMenu.setOnClickListener(view -> {

                switch (id){

                    // Расписание
                    case 0:
                        mContext.startActivity(new Intent(mContext, ScheduleActivity.class));
                        break;
                    // Карта корпусов
                    case 1:
                        mContext.startActivity(new Intent(mContext, MapActivity.class));
                        break;
                    // События
                    case 2:
                        mContext.startActivity(new Intent(mContext, EventActivity.class));
                        break;
                    // Трудоустройство
                    case 3:
                        mContext.startActivity(new Intent(mContext, JobActivity.class));
                        break;
                    // Сотрудники и преподаватели
                    case 4:
                        mContext.startActivity(new Intent(mContext, UsersActivity.class));
                        break;
                }
                if (!SettingsController.isEnabledAnim(mContext)) {
                    ((Activity)mContext).overridePendingTransition(0, 0);
                }
            });
        }
    }

    public MenuItemsAdapter(ArrayList<MenuItemsData> mList, Context mContext) {
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
    public MenuItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_menu_item, parent, false);
        return new MenuItemsViewHolder(mView, mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemsViewHolder holder, int position) {
        holder.text.setText(mList.get(position).getText());
        holder.icon.setImageResource(mList.get(position).getIcon());
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
        return super.getItemId(position);
    }
}
