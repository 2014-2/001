package com.crtb.tunnelmonitor.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventDispatcher {
	
	private List<DatabaseListener> mDatabaseListeners = new CopyOnWriteArrayList<DatabaseListener>();
	
	private static EventDispatcher  sInstance;
	
	public static synchronized EventDispatcher getInstance() {
		if (sInstance == null) {
			sInstance = new EventDispatcher();
		}
		return sInstance;
	}
	
	public void registerDatabaseListener(DatabaseListener listener) {
		if (!mDatabaseListeners.contains(listener)) {
			mDatabaseListeners.add(listener);
		}
	}
	
	public void unregisterDatabaseListener(DatabaseListener listener) {
		mDatabaseListeners.remove(listener);
	}
	
	public void notifyDatabaseChanged() {
		for(DatabaseListener listener : mDatabaseListeners) {
			listener.onChanged();
		}
	}
	
	private EventDispatcher() {
		
	}

}
