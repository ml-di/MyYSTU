package ru.ystu.myystu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import ru.ystu.myystu.Services.UpdateCheck;

public class BootIntentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent pushIntent = new Intent(context, UpdateCheck.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(pushIntent);
            } else {
                context.startService(pushIntent);
            }
        }
    }
}
