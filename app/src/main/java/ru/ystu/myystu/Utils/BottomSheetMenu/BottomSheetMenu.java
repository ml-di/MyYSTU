package ru.ystu.myystu.Utils.BottomSheetMenu;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.AnimRes;
import androidx.annotation.ColorRes;
import androidx.annotation.MenuRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.ArrayList;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.BottomSheetMenu.Data.DividerData;
import ru.ystu.myystu.Utils.BottomSheetMenu.Data.IconsAndTextData;
import ru.ystu.myystu.Utils.BottomSheetMenu.Data.NullIconsAndTextData;
import ru.ystu.myystu.Utils.BottomSheetMenu.Data.OnlyTextData;
import ru.ystu.myystu.Utils.BottomSheetMenu.Interface.BottomSheetMenuInterface;
import ru.ystu.myystu.Utils.BottomSheetMenu.Interface.OnItemClickListener;
import ru.ystu.myystu.Utils.LightStatusBar;

public class BottomSheetMenu implements BottomSheetMenuInterface {

    private OnItemClickListener onItemClickListener;

    private BottomSheetBehavior bottomSheetBehavior;
    private Context mContext;
    private Menu menu;
    private CharSequence title;
    private @AnimRes int animationRes;
    private @ColorRes int navigationBarColorRes;

    private boolean isClosable = true;
    private boolean isIcons = true;
    private boolean isAnimation = false;
    private boolean isDividers = true;
    private boolean isLight = false;

    public BottomSheetMenu(Context mContext, @MenuRes int resId) {
        this.mContext = mContext;
        this.menu = new MenuBuilder(mContext);
        new MenuInflater(mContext).inflate(resId, menu);
    }

    public BottomSheetMenu(Context mContext, Menu menu) {
        this.mContext = mContext;
        this.menu = menu;
    }

    private void show() {
        final LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = layoutInflater.inflate(R.layout.bottomsheetmenu, null);
        final AppCompatTextView titleTextView = view.findViewById(R.id.bottomSheetMenu_title);
        final RecyclerView recyclerView = view.findViewById(R.id.bottomSheetMenu_recyclerView);
        final BottomSheetDialog dialog = new BottomSheetDialog(mContext, R.style.SheetDialog);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        final RecyclerView.Adapter mRecyclerViewAdapter = new BottomSheetMenuAdapter(parseMenuItem(menu));

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mRecyclerViewAdapter);
        setRecyclerViewAnim(recyclerView);

        if (title != null) {
            titleTextView.setVisibility(View.VISIBLE);
            titleTextView.setText(title);
        } else {
            titleTextView.setVisibility(View.GONE);
        }

        setTheme(dialog, view);

        dialog.setContentView(view);
        dialog.show();

        ((BottomSheetMenuAdapter) mRecyclerViewAdapter).setOnItemClickListener(itemId -> {
            if (onItemClickListener != null) {
                onItemClickListener.OnItemClick(itemId);
                if (isClosable) {
                    dialog.cancel();
                }
            }
        });
    }

    private ArrayList<Parcelable> parseMenuItem (Menu menu) {

        final ArrayList<Parcelable> itemList = new ArrayList<>();

        int groupId = 0;

        for (int i = 0; i < menu.size(); i++) {

            final MenuItem item = menu.getItem(i);

            if (item.isVisible()) {

                // Добавление разделителей
                if (isDividers) {
                    if (item.getGroupId() != 0 && groupId != item.getGroupId()) {
                        if (i > 0) {
                            itemList.add(new DividerData(isIcons));
                        }
                        groupId = item.getGroupId();
                    } else if (groupId > 0 && groupId != item.getGroupId()) {
                        groupId = item.getGroupId();
                        itemList.add(new DividerData(isIcons));
                    }
                }

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

    private void setRecyclerViewAnim (RecyclerView recyclerView) {
        if (isAnimation) {

            final LayoutAnimationController controller;

            if (animationRes != 0) {
                controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(), animationRes);
            } else {
                controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(), R.anim.layout_bottomsheetmenu_recyclerview_animation);
            }

            recyclerView.setLayoutAnimation(controller);
        } else {
            recyclerView.clearAnimation();
        }
    }

    private void setTheme (BottomSheetDialog dialog, View view) {
        final Window win = dialog.getWindow();
        if (isLight && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            view.setSystemUiVisibility(view.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            if (win != null) {
                win.setNavigationBarColor(ContextCompat.getColor(mContext, navigationBarColorRes));
            }
        } else {
            if (win != null) {
                win.setNavigationBarColor(ContextCompat.getColor(mContext, navigationBarColorRes));
            }
        }
    }

    @Override
    public void setOnItemClickListener (OnItemClickListener listener) {
        onItemClickListener = listener;
        show();
    }

    @Override
    public void setTheme(boolean light, int navigationBarColorRes) {
        this.isLight = light;
        this.navigationBarColorRes = navigationBarColorRes;
    }

    @Override
    public void setTitle(CharSequence title) {
        this.title = title;
    }

    @Override
    public void setTitle(int titleRes) {
        this.title = mContext.getResources().getString(titleRes);
    }

    @Override
    public void setClosable(boolean closable) {
        this.isClosable = closable;
    }

    @Override
    public void setIcons(boolean icons) {
        this.isIcons = icons;
    }

    @Override
    public void setAnimation(boolean animation) {
        this.isAnimation = animation;
    }

    @Override
    public void setAnimation(boolean animation, int animationRes) {
        this.isAnimation = animation;
        this.animationRes = animationRes;
    }

    @Override
    public void setDividers(boolean dividers) {
        this.isDividers = dividers;
    }
}
