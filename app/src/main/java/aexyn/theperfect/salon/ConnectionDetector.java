package aexyn.theperfect.salon;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ConnectionDetector {

    private Context _context;

    public ConnectionDetector(Context context) {
        this._context = context;
    }

    public boolean isConnectingToInternet() {
        boolean status = false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) _context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiNetwork = connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifiNetwork != null && wifiNetwork.isConnected()) {
                Log.e("Netowrk Status", Boolean.toString(status));
                status = true;
                Log.e("Netowrk Status", Boolean.toString(status));
            }

            NetworkInfo mobileNetwork = connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mobileNetwork != null && mobileNetwork.isConnected()) {
                Log.e("Netowrk Status", Boolean.toString(status));
                status = true;
                Log.e("Netowrk Status", Boolean.toString(status));
            }
        } catch (Exception e) {
            Log.e("Netowrk error", e.getMessage());
        }
        return status;
    }
    /*
     * public boolean isConnectingToInternet(){ ConnectivityManager connectivity
	 * = (ConnectivityManager)
	 * _context.getSystemService(Context.CONNECTIVITY_SERVICE); if (connectivity
	 * != null) { NetworkInfo[] info = connectivity.getAllNetworkInfo(); if
	 * (info != null) for (int i = 0; i < info.length; i++) if
	 * (info[i].getState() == NetworkInfo.State.CONNECTED) { return true; }
	 * 
	 * } return false; }
	 */
}
