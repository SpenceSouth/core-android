package net.simplyadvanced.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/** A simple helper class to help simplify checking for Internet connection on device.
 * 
 * This class uses the following permissions in AndroidManifest.xml:
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    
 *  */
public class HelperConnectivity {
//	private static final String LOG_TAG = "DEBUG: net.simplyadvanced.helper.HelperConnectivity";
//	private static final boolean IS_DEBUG = true;
//	private static final void log(String message) {
//		if (BuildConfig.DEBUG && IS_DEBUG) {
//			Log.d(LOG_TAG, message);
//		}
//	}
	
	/** Prevent instantiation of this class. */
	private HelperConnectivity() {}

	
	/** Returns true if device is connected to Internet, otherwise false. */
	public static boolean hasInternetConnection(final Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager.getActiveNetworkInfo() != null
				&& connectivityManager.getActiveNetworkInfo().isAvailable()
				&& connectivityManager.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			return false;
		}
	}
	
	/** Returns true if device is connected to Wi-Fi, otherwise false. */
	public static boolean hasWifiConnection(final Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
		return networkInfo.isConnected();
	}

	/** Returns true if device is connected to a mobile network, otherwise false. */
	public static boolean hasMobileNetworkConnection(final Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return networkInfo.isConnected();
	}
	
}
