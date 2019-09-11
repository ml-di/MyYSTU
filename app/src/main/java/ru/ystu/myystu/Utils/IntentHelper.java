package ru.ystu.myystu.Utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.lang.reflect.Method;

import ru.ystu.myystu.Activitys.MainActivity;
import ru.ystu.myystu.R;

public class IntentHelper {

    public static void shareText(Context mContext, String text) {
        final Intent shareLink = new Intent();
        shareLink
                .setAction(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, text)
                .setType("text/plain");
        mContext.startActivity(shareLink);
    }

    public static void shareFile(Context mContext, File file, String title, String subject, String text) {

        final Uri scheduleUri = FileProvider.getUriForFile(mContext, "ru.ystu.myystu.provider", file);
        final String fileExtension = MimeTypeMap.getFileExtensionFromUrl(scheduleUri.toString());
        final String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);

        if (text == null) {
            text = "";
        }

        final Intent shareIntent = ShareCompat.IntentBuilder.from((Activity) mContext)
                .setType(mimeType)
                .setStream(scheduleUri)
                .setSubject(subject)
                .setText(text)
                .setEmailTo(new String[]{"example@ystu.ru"})
                .getIntent();
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        mContext.startActivity(Intent.createChooser(
                shareIntent, title));
    }

    public static void openInBrowser(Context mContext, String link) {
        final Intent openLink = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        mContext.startActivity(openLink);
    }

    public static void openFile(Context mContext, File file) {
        if(file.exists()){
            try{
                if(Build.VERSION.SDK_INT >= 24){
                    try{
                        Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                        m.invoke(null);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

                final Uri fileUri = FileProvider.getUriForFile(mContext, "ru.ystu.myystu.provider", file);
                final String fileExtension = MimeTypeMap.getFileExtensionFromUrl(fileUri.toString());
                final String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);

                final Intent openIntent = new Intent();
                openIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                openIntent.addFlags (Intent.FLAG_GRANT_READ_URI_PERMISSION);
                openIntent.setAction(Intent.ACTION_VIEW);
                openIntent.setDataAndType(Uri.fromFile(file), mimeType);
                mContext.startActivity(openIntent);

            } catch (Exception e){
                if(e.getMessage().startsWith("No Activity found to handle")){
                    Toast.makeText(mContext, mContext.getResources()
                                    .getString(R.string.schedule_file_not_open),
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.error_message_file_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    public static void restartApp (Context mContext) {
        /*final Intent mStartActivity = new Intent(mContext, MainActivity.class);
        int mPendingIntentId = 123456;
        final PendingIntent mPendingIntent = PendingIntent.getActivity(mContext, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        final AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);*/
        System.exit(0);
    }
}
