package ru.ystu.myystu;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.room.Room;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;

import okhttp3.OkHttpClient;
import ru.ystu.myystu.Database.AppDatabase;
import ru.ystu.myystu.Utils.SettingsController;

public class Application extends android.app.Application {

    public static Application instance;
    public static boolean isDarkSystemTheme;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        final Context context = Application.this;
        setSystemTheme(context);
        setTheme(context);

        final OkHttpClient okHttpClient = new OkHttpClient();
        final ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(context, okHttpClient)
                .build();

        Fresco.initialize(context, config);
    }

    public static Application getInstance() {
        return instance;
    }
    public AppDatabase getDatabase() {
        return Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "myystudb")
                .fallbackToDestructiveMigration()
                .build();
    }
    public boolean isDarkSystemTheme () {
        return this.isDarkSystemTheme();
    }
    public static void setTheme(Context mContext) {
        setTheme(SettingsController.isDarkTheme(mContext));
    }
    public static void setTheme(boolean isDark) {
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
    private static void setSystemTheme (Context mContext) {
        final int config = mContext.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        isDarkSystemTheme = config == Configuration.UI_MODE_NIGHT_YES && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
    }
}
