package ru.ystu.myystu.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import ru.ystu.myystu.Activitys.SettingsActivity;
import ru.ystu.myystu.R;

public class AboutFragment extends Fragment {

    private AppCompatImageView icon;
    private AppCompatTextView aboutUniversity;
    private AppCompatTextView aboutLibrary;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        icon.setImageTintList(null);

        if (savedInstanceState != null) {
            ((SettingsActivity) Objects.requireNonNull(getActivity()))
                    .setTitleToolBar(getResources()
                            .getString(R.string.settings_category_other_about));
        }

        aboutLibrary.setOnClickListener(v -> {
            AboutLicensesFragment aboutLicensesFragment = new AboutLicensesFragment();
            ((SettingsActivity) Objects.requireNonNull(getActivity())).startFragment(aboutLicensesFragment, "aboutLibrary", getString(R.string.settings_category_other_library_about));
        });

        aboutUniversity.setOnClickListener(v -> {
            AboutUniversityFragment aboutUniversityFragment = new AboutUniversityFragment();
            ((SettingsActivity) Objects.requireNonNull(getActivity())).startFragment(aboutUniversityFragment, "aboutUniversity", getString(R.string.about_university_title));
        });
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
            aboutUniversity = mView.findViewById(R.id.settings_item_about_university);
            aboutLibrary = mView.findViewById(R.id.settings_item_library_about);
        }
        return mView;
    }
}
