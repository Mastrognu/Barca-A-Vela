package it.makersf.barca;

import it.mangusto.barca.BaVCostants;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

class BlueToothThread extends Thread {

	private boolean stopped;
	private boolean paused;

	private BluetoothDevice mDevice;
	private BluetoothDevice mDeviceToConnectTo;

	BlueToothThread() {
		super("BlueToothThread");
	}

	@Override
	public synchronized void run() {
		while(!stopped) {
			while(!paused) {
				//TODO
				/*
				 * Check if mDeviceToConnectTo is null.
				 * If so, connect to it, set mDevice equal it, and mDeviceToConnectTo to null,
				 * else check if still connectet to mDevice.
				 *
				 * Then read the steam
				 */
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

	public synchronized void setDeviceToConnect(BluetoothDevice pDevice) {
		mDeviceToConnectTo = pDevice;
	}
}
