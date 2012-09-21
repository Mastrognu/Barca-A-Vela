package it.makersf.barca;

import it.mangusto.barca.BaVCostants;
import android.util.Log;

class BlueToothThread extends Thread {

	private boolean stopped;
	private boolean paused;
	
	BlueToothThread() {
		super("BlueToothThread");
	}
	
	@Override
	public synchronized void run() {
		while(!stopped) {
			while(!paused) {
				//TODO
			}
			try {
				sleep(BaVCostants.MILLISECONDS_PER_SECOND);
			} catch (InterruptedException e) {
				Log.e(BaVCostants.DEGUB_TAG, e.getMessage());
			}
		}
	}
	
	public synchronized void pause() {
		paused = true;
	}
	
	public synchronized void resumeFromPause() {
		paused = false;
	}
	
	public synchronized void terminate() {
		stopped = true;
	}
}
