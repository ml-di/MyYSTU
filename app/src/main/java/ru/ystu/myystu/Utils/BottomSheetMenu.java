package ru.ystu.myystu.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import androidx.annotation.MenuRes;
import androidx.appcompat.widget.AppCompatTextView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import ru.ystu.myystu.R;

public class BottomSheetMenu {

    private BottomSheetBehavior bottomSheetBehavior;
    private Context mContext;
    private int resMenuId;
    private Menu menu;
    private CharSequence title = "";
    private boolean closable = false;
    private boolean icons = true;

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
        titleTextView.setText(title);

        BottomSheetDialog dialog = new BottomSheetDialog(mContext);
        dialog.setContentView(view);
        dialog.show();
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
        this.closable = closable;
    }

    /*
    *       Отображать иконки
    */
    public void setIcons(boolean icons) {
        this.icons = icons;
    }
}
