package ru.ystu.myystu.Utils.BottomSheetMenu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.MenuRes;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.ArrayList;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.BottomSheetMenu.Data.IconsAndTextData;
import ru.ystu.myystu.Utils.BottomSheetMenu.Data.NullIconsAndTextData;
import ru.ystu.myystu.Utils.BottomSheetMenu.Data.OnlyTextData;

public class BottomSheetMenu {

    private BottomSheetBehavior bottomSheetBehavior;
    private Context mContext;
    private int resMenuId;
    private Menu menu;
    private CharSequence title;

    private boolean isClosable = false;
    private boolean isIcons = true;
    private boolean isAnimation = false;

    public BottomSheetMenu(Context mContext, @MenuRes int resId) {
        this.mContext = mContext;
        this.resMenuId = resId;
    }

    public BottomSheetMenu(Context mContext, Menu menu) {
        this.mContext = mContext;
        this.menu = menu;
    }

    public void show() {
        final LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = layoutInflater.inflate(R.layout.bottomsheetmenu, null);
        final AppCompatTextView titleTextView = view.findViewById(R.id.bottomSheetMenu_title);
        final RecyclerView recyclerView = view.findViewById(R.id.bottomSheetMenu_recyclerView);
        final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        final RecyclerView.Adapter mRecyclerViewAdapter = new BottomSheetMenuAdapter(parseMenuItem(menu));

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mRecyclerViewAdapter);

        if (title != null) {
            titleTextView.setVisibility(View.VISIBLE);
            titleTextView.setText(title);
        } else {
            titleTextView.setVisibility(View.GONE);
        }

        /*final MenuInflater menuInflater = new MenuInflater(mContext);
        Menu mm = new MenuBuilder(mContext);
        menuInflater.inflate(R.menu.menu_news_item, mm);*/

        dialog.setContentView(view);
        dialog.show();
    }

    private ArrayList<Parcelable> parseMenuItem (Menu menu) {

        final ArrayList<Parcelable> itemList = new ArrayList<>();

        for (int i = 0; i < menu.size(); i++) {

            final MenuItem item = menu.getItem(i);

            if (item.isVisible()) {

                final int itemId = item.getItemId();
                final String title = item.getTitle().toString();
                final Parcelable parcelable;

                if (isIcons) {

                    final Drawable icon = item.getIcon();

                    if (icon != null) {
                        parcelable = new IconsAndTextData(itemId, title, icon);
                        ((IconsAndTextData) parcelable).setEnabled(item.isEnabled());
                    } else {
                        parcelable = new NullIconsAndTextData(itemId, title);
                        ((NullIconsAndTextData)parcelable).setEnabled(item.isEnabled());
                    }

                } else {
                    parcelable = new OnlyTextData(itemId, title);
                    ((OnlyTextData) parcelable).setEnabled(item.isEnabled());
                }

                itemList.add(parcelable);
            }
        }

        return itemList;
    }

    /*
    *       Заголовок
    */
    public void setTitle(CharSequence title) {
        this.title = title;
    }

    /*
    *       Закрывать после клика по итему
    */
    public void setClosable(boolean closable) {
        this.isClosable = closable;
    }

    /*
    *       Отображать иконки
    */
    public void setIcons(boolean icons) {
        this.isIcons = icons;
    }

    /*
    *       Анимация списка
    */
    public void setAnimation(boolean animation) {
        this.isAnimation = animation;
    }
}
