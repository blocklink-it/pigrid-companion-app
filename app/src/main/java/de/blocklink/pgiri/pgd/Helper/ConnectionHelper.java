package de.blocklink.pgiri.pgd.Helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class ConnectionHelper {

    public static boolean isWiFiConnected(Context mContext){
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(isConnected){
            boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
            return isWiFi;
        }
        return isConnected;
    }

    public static void enableWifi(Context mContext){
        WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(true);
    }
}
