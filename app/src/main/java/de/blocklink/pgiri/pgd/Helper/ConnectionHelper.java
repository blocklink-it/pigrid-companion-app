package de.blocklink.pgiri.pgd.Helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class ConnectionHelper {

    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_NOT_CONNECTED = 0;

    public static final int NETWORK_STATUS_NOT_CONNECTED = 0;
    public static final int NETWORK_STATUS_WIFI = 1;
    public static final int NETWORK_STATUS_MOBILE = 2;

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

    public static int getConnectivityStatus(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static int getConnectivityStatusString(Context mContext) {
        int conn = ConnectionHelper.getConnectivityStatus(mContext);
        int status = 0;
        if (conn == ConnectionHelper.TYPE_WIFI) {
            status = NETWORK_STATUS_WIFI;
        } else if (conn == ConnectionHelper.TYPE_MOBILE) {
            status = NETWORK_STATUS_MOBILE;
        } else if (conn == ConnectionHelper.TYPE_NOT_CONNECTED) {
            status = NETWORK_STATUS_NOT_CONNECTED;
        }
        return status;
    }
}
