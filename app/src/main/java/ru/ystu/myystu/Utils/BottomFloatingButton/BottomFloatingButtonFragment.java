package ru.ystu.myystu.Utils.BottomFloatingButton;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.ystu.myystu.R;

public class BottomFloatingButtonFragment extends Fragment {

    private ConstraintLayout item;
    private AppCompatImageView iconView;
    private AppCompatTextView textView;

    private Drawable icon = null;
    private String text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            text = getArguments().getString("BFB_TEXT");
            if (getArguments().getParcelable("BFB_ICON") != null) {
                icon = getArguments().getParcelable("BFB_ICON");
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        textView.setText(text);
        item.setOnClickListener(v -> {});
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View mView = inflater.inflate(R.layout.fragment_bottom_floating_button, container, false);
        if (mView != null) {
            item = mView.findViewById(R.id.FBF_item);
            textView = mView.findViewById(R.id.FBF_text);
        }
        return mView;
    }
}
