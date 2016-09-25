package crac.sync;

public class SyncManager {

	private int interval;

	public SyncManager() {
		interval = 200;
	}

	public int getInterval() {
		return interval;
	}

	public void sync() {
		// TODO:
		System.out.println(
				"SyncManager::sync() called @ " + System.currentTimeMillis());
	}
}
