package net.simplyadvanced.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.common.io.CharStreams;
import com.squareup.okhttp.OkHttpClient;

/** A simple helper class to help simplify accessing Internet resources.
 * 
 * This class uses the following two libraries: OkHttp, Guava
 * More info: http://square.github.io/okhttp/
 * More info: https://code.google.com/p/guava-libraries/
 * 
 * This class uses the following permissions in AndroidManifest.xml:
    <uses-permission android:name="android.permission.INTERNET" />
 * 
 *  */
public class HelperHttp {
//	private static final String LOG_TAG = "DEBUG: net.simplyadvanced.helper.HelperHttp";
//	private static final boolean IS_DEBUG = true;
//	private static final void log(String message) {
//		if (BuildConfig.DEBUG && IS_DEBUG) {
//			Log.d(LOG_TAG, message);
//		}
//	}
	
	/** Prevent instantiation of this class. */
	private HelperHttp() {}
	
	
	private static OkHttpClient client;
	
	static {
		client = new OkHttpClient();
	}
	
	
	public static String get(URL url) throws IOException {
		HttpURLConnection connection = client.open(url);
		InputStream in = null;
		try {
			// Read the response.
			in = connection.getInputStream();
			return getStringFromInputStream(in);
		} finally {
			if (in != null)
				in.close();
		}
	}
	
	public static String post(URL url, byte[] body) throws IOException {
		HttpURLConnection connection = client.open(url);
		OutputStream out = null;
		InputStream in = null;
		try {
			// Write the request.
			connection.setRequestMethod("POST");
			out = connection.getOutputStream();
			out.write(body);
			out.close();

			// Read the response.
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new IOException("Unexpected HTTP response: "
						+ connection.getResponseCode() + " "
						+ connection.getResponseMessage());
			}
			in = connection.getInputStream();
			return getStringFromInputStream(in);
		} finally {
			// Clean up.
			if (out != null) { out.close(); }
			if (in != null) { in.close(); }
		}
	}
	
	
	private static String getStringFromInputStream(final InputStream inputStream) throws UnsupportedEncodingException, IOException {
		return CharStreams.toString(new InputStreamReader(inputStream, "UTF-8"));
	}

}
