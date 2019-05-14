package ru.ystu.myystu.Fragments;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.ContentFrameLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import ru.ystu.myystu.R;

public class ErrorMessageFragment extends Fragment {

    private AppCompatTextView messageTextView;
    private AppCompatImageView iconImageView;
    private ContentFrameLayout refreshBtn;
    private ContentFrameLayout iconBackground;

    private ObjectAnimator scaleDown;

    private String msg;
    private int code;

    private int layout_id;
    private String fragment_tag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            msg = getArguments().getString("error_msg");
            code = getArguments().getInt("error_code");
            layout_id = getArguments().getInt("view_id");
            fragment_tag = getArguments().getString("fragment_tag");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // Анимация пульсации фона
        if(scaleDown == null) {
            scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                    iconBackground,
                    PropertyValuesHolder.ofFloat("scaleX", 0.85f),
                    PropertyValuesHolder.ofFloat("scaleY", 0.85f),
                    PropertyValuesHolder.ofFloat("alpha", 0.02f));
            scaleDown.setDuration(2000);
            scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
            scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
            scaleDown.setInterpolator(new FastOutSlowInInterpolator());
            scaleDown.start();
        } else {
            if (scaleDown.isPaused())
                scaleDown.start();
        }

        // Лютый костыль :)
        refreshBtn.setOnClickListener(view -> {
            if(getActivity().getSupportFragmentManager() != null){
                    getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                    // Для фрагментов
                    if(fragment_tag != null){
                        Fragment mFragment = null;

                        if(fragment_tag.equals("NEWS_FRAGMENT"))
                            mFragment = new NewsFragment();

                        if (mFragment != null) {
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(layout_id, mFragment, fragment_tag)
                                    .commit();
                        }
                    } else {
                        // Для активити
                        getActivity().finish();
                        getActivity().overridePendingTransition(0, 0);
                        startActivity(getActivity().getIntent());
                        getActivity().overridePendingTransition(0, 0);
                    }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if(scaleDown.isRunning()) {
            scaleDown.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (scaleDown.isRunning()) {
            scaleDown.cancel();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        switch (code){
            // Отсутсвует подключение к интернету
            case 0:
                if(msg == null)
                    messageTextView.setText(getResources().getString(R.string.error_message_internet_error));
                else
                    messageTextView.setText(msg);

                iconImageView.setImageResource(R.drawable.ic_connect_error);
                break;
            // Файл не найден
            case 1:
                if(msg == null)
                    messageTextView.setText(getResources().getString(R.string.error_message_file_not_found));
                else
                    messageTextView.setText(msg);

                iconImageView.setImageResource(R.drawable.ic_file_not_found);
                break;
            // Остальные
            default:
                if(msg == null){
                    messageTextView.setVisibility(View.GONE);
                } else {
                    if(messageTextView.getVisibility() == View.GONE)
                        messageTextView.setVisibility(View.VISIBLE);

                    messageTextView.setText(msg);
                }

                iconImageView.setImageResource(R.drawable.ic_error);
                break;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View mView = inflater.inflate(R.layout.fragment_error_message, container, false);
        messageTextView = mView.findViewById(R.id.error_message_msg);
        iconImageView = mView.findViewById(R.id.error_message_icon);
        refreshBtn = mView.findViewById(R.id.error_message_refresh_btn);
        iconBackground = mView.findViewById(R.id.error_message_icon_background);

        return mView;
    }
}
