package net.simplyadvanced.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/** A modular class for activating/deactivating Location providers (GPS/Network) and for getting
 * current location in general.
 * 
 *  TODO: Before using, take a look at `loadLocationManager()` and edit appropriately.
 *  */
public class HelperLocation {
	private static final String LOG_TAG = "DEBUG: net.simplyadvanced.helper.HelperLocation";
	private static final boolean IS_DEBUG = true;
	private static void log(final String message) {
		if (BuildConfig.DEBUG && IS_DEBUG) {
			Log.d(LOG_TAG, message);
		}
	}
	
	/** The minimum amount of time, in milliseconds, that the location listener should listen for location updates. Currently set for 5 minutes. */
	public static final long MIN_TIME = 5*60*1000; // In milliseconds. // Currently, set for 5 minutes.

	/** The minimum distance, in meters, that the location listener should listen for location updates. */
	public static final float MIN_DISTANCE = 100; // In meters.

	
	
	
	/** A reference to the system Location Manager. */
	private static LocationManager mLocationManager;
	
	/** A listener that responds to location updates. */
	private static LocationListener mLocationListener;

	private static Location mLocationGps;
	private static Location mLocationNetwork;
	
	/** Used to return a valid/parse-able value when there is a null/unknown location. */
	private static final double ZERO = 0.0; // If this didn't have the decimal, then it would be an `int`.
	

	
	/** Prevent instantiation of this class. */
	private HelperLocation() {}
	
	
	/***************************/
	/* The Important Functions */
	/***************************/
	
	/** Activates GPS, or if not available, then try to activate Network provider. */
	public static final void activateGps() {
		// This is called first to ensure that multiple instances of the listener aren't actually called and running.
		deactivateGps();

		loadLocationManager();
		
		// Load last known location coordinate, if available, so that user doesn't have to wait as long for a location.
        if (isGpsEnabled()) {
        	Location gpsLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	    	if (gpsLocation != null) {
				mLocationGps = gpsLocation;
			}
        } else if (isNetworkEnabled()) {
        	Location networkLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	    	if (networkLocation != null) {
				mLocationNetwork = networkLocation;
			}
        }
        
        mLocationListener = new LocationListener() {
    		@Override
    	    public void onLocationChanged(Location location) {
    			if (location.getProvider().equalsIgnoreCase(LocationManager.GPS_PROVIDER)) {
    				mLocationGps = location;
    			} else if (location.getProvider().equalsIgnoreCase(LocationManager.NETWORK_PROVIDER)) {
    				mLocationNetwork = location;
    			}
//        		while (latitudeNowString.length() < 10) { latitudeNowString = latitudeNowString + "0"; }
//        		while (longitudeNowString.length() < 11) { longitudeNowString = longitudeNowString + "0"; }
    	    }

    	    public void onStatusChanged(String provider, int status, Bundle extras) {}

    	    public void onProviderEnabled(String provider) {}

    	    public void onProviderDisabled(String provider) {}

    	};

		// Register the listener with the Location Manager to receive location updates.
		if (isGpsEnabled()) {
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener); // In milliseconds, meters.
		}
		if (isNetworkEnabled()) {
			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener); // In milliseconds, meters.
		}
	}

	/** Deactivates GPS. Stops listening for location updates. */
	public static void deactivateGps() {
    	try {
    		if (mLocationListener != null) {
				mLocationManager.removeUpdates(mLocationListener);
				mLocationListener = null;
    		}
		} catch (IllegalArgumentException e) {
			// ERROR: Intent is null.
			e.printStackTrace();
		}
	}
	
	
	
	/********************/
	/* Helper Functions */
	/********************/
	
	/** Returns true if GPS provider is enabled, otherwise false. */
	public static final boolean isGpsEnabled() {
		loadLocationManager();
        try {
        	return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception e) {
        	// If exception occurs, then there is no GPS provider available.
        	return false;
        }
	}

	/** Returns true if network provider is enabled, otherwise false. */
	public static final boolean isNetworkEnabled() {
		loadLocationManager();
        try {
        	return mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception e) {
        	// If exception occurs, then there is no network provider available.
        	return false;
        }
	}
	
	/** Returns the last known location latitude. Use this if you know that the location listener
	 * doesn't need to turn on. This first tries GPS, then network, and returns 0.0 if GPS nor Network is enabled. */
	public static final double getLastKnownLocationLatitude() {
		loadLocationManager();
		double latitude = 0.0;
		// Load last known location coordinate, if available, so that user doesn't have to wait as long for a location.
        if (isGpsEnabled()) {
        	Location gpsLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	    	if (gpsLocation != null) {
	    		latitude = gpsLocation.getLatitude();
			}
        } else if (isNetworkEnabled()) {
        	Location networkLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	    	if (networkLocation != null) {
	    		latitude = networkLocation.getLatitude();
			}
        }
        return latitude;
	}

	/** Returns the last known location longitude. Use this if you know that the location listener
	 * doesn't need to turn on. This first tries GPS, then network, and returns 0.0 if GPS nor Network is enabled. */
	public static final double getLastKnownLocationLongitude() {
		loadLocationManager();
		double longitude = 0.0;
		// Load last known location coordinate, if available, so that user doesn't have to wait as long for a location.
        if (isGpsEnabled()) {
        	Location gpsLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	    	if (gpsLocation != null) {
	    		longitude = gpsLocation.getLongitude();
			}
        } else if (isNetworkEnabled()) {
        	Location networkLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	    	if (networkLocation != null) {
	    		longitude = networkLocation.getLongitude();
			}
        }
        return longitude;
	}
	
	/** Returns a valid coordinate, if unknown that value will be `0.0`. */
	public static final double getCurrentLatitude() {
		if (getCurrentLatitudeGpsValid() != ZERO) {
			return getCurrentLatitudeGpsValid();
		} else {
			return getCurrentLatitudeNetworkValid();
		}
	}

	/** Returns a valid coordinate, if unknown that value will be `0.0`. */
	public static final double getCurrentLongitude() {
		if (getCurrentLongitudeGpsValid() != ZERO) {
			return getCurrentLongitudeGpsValid();
		} else {
			return getCurrentLongitudeNetworkValid();
		}
	}

	/** Returns a valid GPS coordinate. Basically, instead of returning null, it returns 0.0. Hopefully, nobody is really there. */
	private static final double getCurrentLatitudeGpsValid() {
		if (mLocationGps == null) {
			return ZERO;
		} else {
			return mLocationGps.getLatitude();
		}
	}

	/** Returns a valid GPS coordinate. Basically, instead of returning null, it returns 0.0. Hopefully, nobody is really there. */
	private static final double getCurrentLongitudeGpsValid() {
		if (mLocationGps == null) {
			return ZERO;
		} else {
			return mLocationGps.getLongitude();
		}
	}

	/** Returns a valid coordinate from network provider. Basically, instead of returning null, it returns 0.0. Hopefully, nobody is really there. */
	private static final double getCurrentLatitudeNetworkValid() {
		if (mLocationNetwork == null) {
			return ZERO;
		} else {
			return mLocationNetwork.getLatitude();
		}
	}

	/** Returns a valid coordinate from network provider. Basically, instead of returning null, it returns 0.0. Hopefully, nobody is really there. */
	private static final double getCurrentLongitudeNetworkValid() {
		if (mLocationNetwork == null) {
			return ZERO;
		} else {
			return mLocationNetwork.getLongitude();
		}
	}
	
	
	
	/*************************/
	/* User Helper Functions */
	/*************************/

	/** This opens the Location Settings.
	 * @param context must be an Activity context. */
	public static final void promptUserToEnableGps(final Context context) {
		new AlertDialog.Builder(context)
				.setTitle("Please enable GPS for proper functionality") // WAS: "Please enable GPS for best results".
//				.setMessage("")
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)); // Opens page in settings to enable GPS
					}
				})
				.setNegativeButton(android.R.string.cancel, null)
				.show();
	}
	
	

	/*********************/
	/* Secondary Helpers */
	/*********************/
	
	// TODOv2: Create a function to also return �E or �W // Use Alt + 0176
	// TODOv2: Create a function to also return �N or �S // Use Alt + 0176
	
//	while (latitudeNowString.length() < 10) { latitudeNowString = latitudeNowString + "0"; } // TODOv2: Change this to �N or �S // Use Alt + 0176
//	while (longitudeNowString.length() < 11) { longitudeNowString = longitudeNowString + "0"; } // TODOv2: Change this to �E or �W // Use Alt + 0176
	
	/** Returns a coordinate formatted to 8 decimal places, or "N/A" if bad String. Make sure this return value isn't parsed to any number. */
//	public static String formatCoordinateString(String s) {
//		if (s.equalsIgnoreCase(Const.PHRASE_NA)) {
//			return s;
//		} else {
//			int indexOfPeriod = s.indexOf(".");
//			while (s.substring(indexOfPeriod + 1).length() < 8) { s = s + "0"; }
//			if (s.substring(indexOfPeriod + 1).length() > 8) { // If too many decimals.
//				return s.substring(0, indexOfPeriod + 9);
//			}
//			return s;
//		}
//	}

	/** Returns a coordinate formatted to 8 decimal places.
	 * This turns a decimal to a String, then does the formatting, then makes it into a double again. */
//	public static double formatCoordinate(final double d) {
//		return Double.parseDouble(formatCoordinateString(String.valueOf(d)));
//	}
	
	
	
	/*******************/
	/* Private Helpers */
	/*******************/
	
	// This is needed at each of the points in order to make them easily static.
	/** Instantiates the mLocationManager variable if needed. */
	private static final void loadLocationManager() {
		// TODO: Don't get Application context this way. Either pass in a static way of getting app context like in second line, or create a constructor to accept the context.
		if (mLocationManager == null) { mLocationManager = (LocationManager) (new Activity().getApplicationContext()).getSystemService(Context.LOCATION_SERVICE); }
//		if (mLocationManager == null) { mLocationManager = (LocationManager) MyApp.getContext().getSystemService(Context.LOCATION_SERVICE); }
	}
	
}