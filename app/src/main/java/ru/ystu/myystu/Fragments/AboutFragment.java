package ru.ystu.myystu.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import ru.ystu.myystu.Activitys.SettingsActivity;
import ru.ystu.myystu.R;

public class AboutFragment extends Fragment {

    private AppCompatImageView icon;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        icon.setImageTintList(null);

        if (savedInstanceState != null) {
            ((SettingsActivity) Objects.requireNonNull(getActivity()))
                    .setTitleToolBar(getResources()
                            .getString(R.string.settings_category_other_about));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((SettingsActivity) Objects.requireNonNull(getActivity()))
                .setTitleToolBar(getResources()
                        .getString(R.string.menu_text_settings));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View mView = inflater.inflate(R.layout.fragment_about, container, false);
        if (mView != null) {
            icon = mView.findViewById(R.id.about_icon);
        }
        return mView;
    }
}
