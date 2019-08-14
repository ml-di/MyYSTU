package ru.ystu.myystu.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import ru.ystu.myystu.Activitys.SettingsActivity;
import ru.ystu.myystu.Application;
import ru.ystu.myystu.Database.AppDatabase;
import ru.ystu.myystu.R;

public class SettingsFragment extends PreferenceFragmentCompat implements ActivityCompat.OnRequestPermissionsResultCallback {

    private String key;
    private AppDatabase db;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);
        this.key = rootKey;

        if (db == null || !db.isOpen()) {
            db = Application.getInstance().getDatabase();
        }

        // Размер загружаемых изображений
        final ListPreference photoSize = findPreference("preference_general_photoSize");
        if(photoSize != null) {
            photoSize.setSummary(photoSize.getEntry());
            photoSize.setOnPreferenceChangeListener((preference, newValue) -> {
                int index = 0;
                for(String s : getResources().getStringArray(R.array.photoSize_Values)) {
                    if(s.equals(newValue)) {
                        preference.setSummary(getResources().getStringArray(R.array.photoSize_Array)[index]);
                    }
                    index++;
                }
                return true;
            });
        }

        // Получение размера и очистка кеша изображений
        final Preference cacheImage = findPreference("preference_cache_photo");
        if (cacheImage != null) {
            final File imageCache = new File(getContext().getCacheDir() + "/image_cache");
            if (imageCache.list() != null && imageCache.list().length > 0) {
                final String sizeImageCache = getReadableSize(folderSize(imageCache));
                cacheImage.setSummary(getResources().getString(R.string.settings_category_additional_cache_summary) + " " + sizeImageCache);
            } else {
                cacheImage.setSummary(getResources().getString(R.string.settings_category_additional_cache_summary)
                        + " 0 B");
            }
            cacheImage.setOnPreferenceClickListener(view -> {
                final ImagePipeline imagePipeline = Fresco.getImagePipeline();
                imagePipeline.clearCaches();
                cacheImage.setSummary(getResources().getString(R.string.settings_category_additional_cache_summary)
                    + " 0 B");
                return true;
            });
        }

        // Получение размер и очистка кеша данных
        final Preference cacheApp = findPreference("preference_cache_app");
        if (cacheApp != null) {

            long sizeDb = 0;
            if (getContext().getDatabasePath("myystudb") != null) {
                sizeDb = getContext().getDatabasePath("myystudb").length();
            }

            cacheApp.setSummary(getResources().getString(R.string.settings_category_additional_cache_summary) + " " + getReadableSize(sizeDb));

            cacheApp.setOnPreferenceClickListener(view -> {
                new Thread(() -> {
                    db.clearAllTables();
                    db.close();
                    getActivity().runOnUiThread(() -> cacheApp.setSummary(getResources().getString(R.string.settings_category_additional_cache_summary)
                            + " " + getReadableSize(getContext().getDatabasePath("myystudb").length())));
                }).start();
                return true;
            });
        }

        // Получение размера и очистка кеша расписания
        final Preference cacheSchedule = findPreference("preference_cache_schedule");
        if (cacheSchedule != null) {
            final File scheduleCache = new File(Environment.getExternalStorageDirectory(), "/.MyYSTU/");
            if (scheduleCache.list() != null && scheduleCache.list().length > 0) {
                final String sizeScheduleCache = getReadableSize(folderSize(scheduleCache));
                cacheSchedule.setSummary(getResources().getString(R.string.settings_category_additional_cache_summary) + " " + sizeScheduleCache);
            } else {
                cacheSchedule.setSummary(getResources().getString(R.string.settings_category_additional_cache_summary)
                        + " 0 B");
            }

            cacheSchedule.setOnPreferenceClickListener(view -> {
                folderDelete(scheduleCache, getActivity());
                cacheSchedule.setSummary(getResources().getString(R.string.settings_category_additional_cache_summary)
                        + " 0 B");
                return true;
            });
        }

        // О ВУЗе
        final Preference aboutUniversity = findPreference("preference_other_about_university");
        if (aboutUniversity != null) {
            aboutUniversity.setOnPreferenceClickListener(view -> {
                AboutUniversityFragment aboutUniversityFragment = new AboutUniversityFragment();
                ((SettingsActivity) Objects.requireNonNull(getActivity())).startFragment(aboutUniversityFragment, view);
                return true;
            });
        }

        // О приложении
        final Preference about = findPreference("preference_other_about");
        if (about != null) {
            about.setOnPreferenceClickListener(view -> {
                AboutFragment aboutFragment = new AboutFragment();
                ((SettingsActivity) Objects.requireNonNull(getActivity())).startFragment(aboutFragment, view);
                return true;
            });
        }

        // Лицензии открытого ПО
        final Preference aboutLic = findPreference("preference_other_library_about");
        if (aboutLic != null) {
            aboutLic.setOnPreferenceClickListener(view -> {
                AboutLicensesFragment aboutLicensesFragment = new AboutLicensesFragment();
                ((SettingsActivity) Objects.requireNonNull(getActivity())).startFragment(aboutLicensesFragment, view);
                return true;
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(key != null) {
            ((SettingsActivity) getActivity()).setTitleToolBar(getContext()
                            .getResources().getString(R.string.menu_text_settings));
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    // Определение размера каталога
    private static long folderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += folderSize(file);
        }
        return length;
    }
    // Удаление всех файлов из каталога
    private static void folderDelete(File directory, Context mContext) {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(mContext), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((SettingsActivity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            for (File file : directory.listFiles()) {
                if (file.isFile())
                    file.delete();
                else {
                    folderDelete(file, mContext);
                    file.delete();
                }
            }
        }
    }
    // Перевод байтов в нормальные велечины
    private static String getReadableSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups))
                + " " + units[digitGroups];
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(getActivity(), "Разрешение успешно получено, повторите действие", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
