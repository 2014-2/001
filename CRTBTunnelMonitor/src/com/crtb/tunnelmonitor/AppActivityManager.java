package com.crtb.tunnelmonitor;

import java.util.Stack;

import android.app.Activity;

public final class AppActivityManager {

	private static Stack<Activity> activityStack = new Stack<Activity>();

	public static void addActivity(Activity activity) {
		if (activity != null) {
			activityStack.add(activity);
		}
	}

	public static void removeActivity(Activity activity) {

		if (activity != null) {
			activityStack.remove(activity);
		}
	}

	public static void finishAllActivity() {

		for (Activity activity : activityStack) {

			if (activity != null && !activity.isFinishing()) {
				activity.finish();
			}
		}

		// clear stack
		clearAllStack();
	}

	public static void clearAllStack() {
		activityStack.clear();
	}
}
