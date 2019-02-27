package ru.ystu.myystu.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.Activitys.JobActivity;
import ru.ystu.myystu.Activitys.OlympActivity;
import ru.ystu.myystu.R;
import ru.ystu.myystu.AdaptersData.MenuItemsData;
import ru.ystu.myystu.Utils.NetworkInformation;

public class MenuItemsAdapter extends RecyclerView.Adapter<MenuItemsAdapter.MenuItemsViewHolder> {

    private List<MenuItemsData> mList;
    private Context context;

    static class MenuItemsViewHolder extends RecyclerView.ViewHolder{

        private final AppCompatTextView text;
        private final AppCompatImageView icon;
        private final ConstraintLayout itemMenu;
        private int id;


        MenuItemsViewHolder(View itemView, final List<MenuItemsData> mList, final Context context) {
            super(itemView);


            text = itemView.findViewById(R.id.itemMenu_text);
            icon = itemView.findViewById(R.id.itemMenu_icon);
            itemMenu = itemView.findViewById(R.id.itemMenu);

            // Обработчик нажатий пунктов меню
            itemMenu.setOnClickListener(view -> {

                switch (id){

                    // Расписание
                    case 0:

                        break;
                    // Карта корпусов
                    case 1:

                        break;
                    // Олимпиады
                    case 2:

                        if(NetworkInformation.hasConnection(context))
                            context.startActivity(new Intent(context, OlympActivity.class));
                        else
                            Toast.makeText(context, context.getResources().getString(R.string.toast_dont_network), Toast.LENGTH_LONG).show();

                        break;
                    // Трудоустройство
                    case 3:

                        if(NetworkInformation.hasConnection(context))
                            context.startActivity(new Intent(context, JobActivity.class));
                        else
                            Toast.makeText(context, context.getResources().getString(R.string.toast_dont_network), Toast.LENGTH_LONG).show();

                        break;
                    // Обратная связь
                    case 4:

                        break;
                }
            });
        }
    }

    public MenuItemsAdapter(List<MenuItemsData> mList, Context context) {
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
    public MenuItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_menu_item, parent, false);
        return new MenuItemsViewHolder(v, mList, context);
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
