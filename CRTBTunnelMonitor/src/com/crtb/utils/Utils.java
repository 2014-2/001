package com.crtb.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

public class Utils {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static Date parseDate(String text) {
		if (TextUtils.isEmpty(text)) {
			throw new IllegalArgumentException("Date text is empty.");
		}
		Date result = null;
		try {
			result = DATE_FORMAT.parse(text);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String formatDate(Date date) {
		return DATE_FORMAT.format(date);
	}
	
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connManager == null) {
			return false;
		} else {
			NetworkInfo[] networkInfo = connManager.getAllNetworkInfo();
			if (networkInfo != null) {
				for (NetworkInfo net : networkInfo) {
					if (net.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
