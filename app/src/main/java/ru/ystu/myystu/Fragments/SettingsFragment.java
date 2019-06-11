package ru.ystu.myystu.Fragments;

import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Objects;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;
import ru.ystu.myystu.Activitys.SettingsActivity;
import ru.ystu.myystu.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    private String key;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);
        this.key = rootKey;

        // Включить / Отключить уведомления
        final SwitchPreference enableNotification = findPreference("preference_notification_enable");
        if (enableNotification != null) {
            notificationChange(enableNotification);
            enableNotification.setOnPreferenceClickListener(view -> {
                notificationChange(enableNotification);
                return true;
            });
        }

        // Включить / Отключить звук уведомлений
        final SwitchPreference songNotification = findPreference("preference_notification_ringtone_enable");
        if(songNotification != null) {
            notificationSongChange(songNotification);
            songNotification.setOnPreferenceClickListener(view -> {
                notificationSongChange(songNotification);
                return true;
            });
        }

        // Включить / Отключить обновления
        final SwitchPreference enableUpdate = findPreference("preference_additional_update_enable");
        if(enableUpdate != null) {
            notificationUpdateChange(enableUpdate);
            enableUpdate.setOnPreferenceClickListener(view -> {
                notificationUpdateChange(enableUpdate);
                Toast.makeText(getContext(), getResources().getString(R.string.toast_reloadApp), Toast.LENGTH_SHORT).show();
                return true;
            });
        }

        // Описание частоте обновления
        final ListPreference delayUpdate = findPreference("preference_additional_update_delay");
        if(delayUpdate != null) {
            delayUpdate.setSummary(delayUpdate.getEntry());
            delayUpdate.setOnPreferenceChangeListener((preference, newValue) -> {
                int index = 0;
                for(String s : getResources().getStringArray(R.array.delay_Values)) {
                    if(s.equals(newValue)) {
                        preference.setSummary(getResources().getStringArray(R.array.delay_Array)[index]);
                    }
                    index++;
                }

                Toast.makeText(getContext(), getResources().getString(R.string.toast_reloadApp), Toast.LENGTH_SHORT).show();

                return true;
            });
        }

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
                folderDelete(scheduleCache);
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(key != null) {
            ((SettingsActivity) getActivity()).setTitleToolBar(getContext()
                            .getResources().getString(R.string.menu_text_settings));
        }
    }

    // Включить / Отключить уведомления (Доступность view)
    private void notificationChange(SwitchPreference view){

        // TODO Временно
        findPreference("preference_notification_ringtone").setEnabled(false);


        if(view.isChecked()) {
            findPreference("preference_notification_type").setEnabled(true);
            findPreference("preference_notification_ringtone_enable").setEnabled(true);

            /*if(((SwitchPreference)findPreference("preference_notification_ringtone_enable")).isChecked())
                findPreference("preference_notification_ringtone").setEnabled(true);*/

            findPreference("preference_notification_vibration").setEnabled(true);
            findPreference("preference_notification_indicator").setEnabled(true);
            findPreference("preference_notification_push").setEnabled(true);
        } else {
            findPreference("preference_notification_type").setEnabled(false);
            findPreference("preference_notification_ringtone_enable").setEnabled(false);
            //findPreference("preference_notification_ringtone").setEnabled(false);
            findPreference("preference_notification_vibration").setEnabled(false);
            findPreference("preference_notification_indicator").setEnabled(false);
            findPreference("preference_notification_push").setEnabled(false);
        }
    }
    // Включить / Отключить звук уведомлений (Доступность view)
    private void notificationSongChange(SwitchPreference view) {
        /*if(view.isChecked()) {
            findPreference("preference_notification_ringtone").setEnabled(true);
        } else {
            findPreference("preference_notification_ringtone").setEnabled(false);
        }*/
    }
    // Включить / Отключить обновления (Доступность view)
    private void notificationUpdateChange(SwitchPreference view) {
        if(view.isChecked()) {
            findPreference("preference_additional_update_delay").setEnabled(true);
            findPreference("preference_additional_update_type").setEnabled(true);
        } else {
            findPreference("preference_additional_update_delay").setEnabled(false);
            findPreference("preference_additional_update_type").setEnabled(false);
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
    private static void folderDelete(File directory) {
        for (File file : directory.listFiles()) {
            if (file.isFile())
                file.delete();
            else {
                folderDelete(file);
                file.delete();
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
}
