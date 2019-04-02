package ru.ystu.myystu.Utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class ServiceUtils {

    public static boolean isServiceRunning(String serviceClassName, Context mContext){
        final ActivityManager activityManager = (ActivityManager) mContext.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)){
                return true;
            }
        }

        return false;
    }
}
