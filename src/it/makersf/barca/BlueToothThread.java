package it.makersf.barca;

import java.util.Random;

import it.mangusto.barca.BaVCostants;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

class BlueToothThread extends Thread {

	private boolean stopped;
	private boolean paused;
	private final BlueToothService mBTService;

	private BluetoothDevice mDevice;
	private BluetoothDevice mDeviceToConnectTo;

	BlueToothThread(BlueToothService pBTService) {
		super("BlueToothThread");
		mBTService = pBTService;
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
				Intent broadCast = new Intent(BlueToothService.BROADCAST_ACTION);
				Bundle content = new Bundle();
				content.putInt("value", new Random().nextInt());
				broadCast.putExtra(BlueToothService.BROADCAST_EXTRA_NAME, content);
				mBTService.sendBroadcast(broadCast);
				try {
					sleep(BaVCostants.MILLISECONDS_PER_SECOND);
				} catch (InterruptedException e) {
					Log.e(BaVCostants.DEGUB_TAG, e.getMessage());
				}
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
