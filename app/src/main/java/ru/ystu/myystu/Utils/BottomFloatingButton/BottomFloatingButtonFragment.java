package ru.ystu.myystu.Utils.BottomFloatingButton;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.BottomFloatingButton.Interface.OnClickListener;

public class BottomFloatingButtonFragment extends Fragment {

    private OnClickListener onClickListener;

    private ConstraintLayout content;
    private CardView item;
    private AppCompatImageView iconView;
    private AppCompatTextView textView;

    private Drawable icon = null;
    private String text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            text = getArguments().getString("BFB_TEXT");
            if (getArguments().getInt("BFB_ICON") != -1) {
                icon = getContext().getResources().getDrawable(getArguments().getInt("BFB_ICON"));
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        textView.setText(text);
        if (icon == null) {
            iconView.setVisibility(View.GONE);
        } else {
            iconView.setVisibility(View.VISIBLE);
            iconView.setImageDrawable(icon);
        }

        content.requestApplyInsets();
        content.setOnApplyWindowInsetsListener((v, insets) -> {
            content.setPadding(0, 0, 0, insets.getSystemWindowInsetBottom());
            return insets;
        });

        int test = 0;
        item.setOnClickListener(v -> onClickListener.OnClick());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View mView = inflater.inflate(R.layout.fragment_bottom_floating_button, container, false);
        if (mView != null) {
            content = mView.findViewById(R.id.FBF_content);
            item = mView.findViewById(R.id.FBF_item);
            textView = mView.findViewById(R.id.FBF_text);
            iconView = mView.findViewById(R.id.FBF_icon);
        }
        return mView;
    }

    public void setOnClickListener (OnClickListener listener) {
        onClickListener = listener;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onClickListener = BottomFloatingButton.SaveListener.onRequestListener();
        int test = 0;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BottomFloatingButton.SaveListener.onSaveListener(onClickListener);
        int test = 0;
    }
}
