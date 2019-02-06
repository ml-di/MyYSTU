package ru.ystu.myystu.function;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkInformation {

    public static boolean hasConnection(final Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = null;
        if (cm != null) {
            wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        }
        if (wifiInfo != null && wifiInfo.isConnected())
            return true;

        if (cm != null) {
            wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        }
        if (wifiInfo != null && wifiInfo.isConnected())
            return true;

        if (cm != null) {
            wifiInfo = cm.getActiveNetworkInfo();
        }
        if (wifiInfo != null && wifiInfo.isConnected())
            return true;

        return false;
    }

}
