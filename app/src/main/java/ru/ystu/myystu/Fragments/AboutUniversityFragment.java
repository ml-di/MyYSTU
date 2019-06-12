package ru.ystu.myystu.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import ru.ystu.myystu.Activitys.SettingsActivity;
import ru.ystu.myystu.R;

public class AboutUniversityFragment extends Fragment {

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((SettingsActivity) Objects.requireNonNull(getActivity()))
                .setTitleToolBar(getResources()
                        .getString(R.string.menu_text_settings));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            ((SettingsActivity) Objects.requireNonNull(getActivity()))
                    .setTitleToolBar(getResources()
                            .getString(R.string.settings_category_other_about_university));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about_university, container, false);
    }
}
