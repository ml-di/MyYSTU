package ru.ystu.myystu.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import java.io.File;

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

        final Uri fileUri = FileProvider.getUriForFile(mContext, "ru.ystu.myystu.provider", file);
        final String fileExtension = MimeTypeMap.getFileExtensionFromUrl(fileUri.toString());
        final String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);

        final Intent openIntent = new Intent();
        openIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        openIntent.addFlags (Intent.FLAG_GRANT_READ_URI_PERMISSION);
        openIntent.setAction(Intent.ACTION_VIEW);
        openIntent.setDataAndType(Uri.fromFile(file), mimeType);
        mContext.startActivity(openIntent);
    }
}
