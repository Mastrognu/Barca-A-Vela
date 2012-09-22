package it.mangusto.barca;

import it.makersf.barca.BlueToothService;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class ServiceCommunicator {
	public static final int BLUETOOTH_NOT_SUPPORTED = 0;
	public static final int UNABLE_TO_TURN_ON_BLUETOOTH = 1;
	public static final int REQUEST_ENABLE_BT = 1;

	private final Main mApp;
	Messenger mService;
	private boolean mIsBound;

	private ServiceConnection mConnection;

	public ServiceCommunicator(Main pApp) {
		mApp = pApp;
		mConnection = new AppServiceConnection(this);
	}

	public void attemptServiceConnection() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// Attivo il Bluetooth se non è già attivo
		if (mBluetoothAdapter == null) {
			mApp.unusableBlueTooth(BLUETOOTH_NOT_SUPPORTED);
		}

		if (mBluetoothAdapter.isEnabled()) {
			doBindService();
		} else {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			mApp.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			//doBindService() sarà chiamato da onResultFromBlueToothInten solo se il BT è stato attivato correttamente
		}
	}

	public void requestServiceDisconnection() {
		doUnbindService();
	}

	public void onResultFromBlueToothEnableRequest(int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK) {
			doBindService();
		} else {
			mApp.unusableBlueTooth(UNABLE_TO_TURN_ON_BLUETOOTH);
		}
	}

	//===========================================================
	// Private Methods
	//===========================================================

	private void doBindService() {
		mApp.bindService(new Intent(mApp,
				BlueToothService.class), mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	private void doUnbindService() {
		if (mIsBound) {
			// If we have received the service, and hence registered with
			// it, then now is the time to unregister.
			if (mService != null) {
				try {
					Message msg = Message.obtain(null, BlueToothService.MSG_UNREGISTER_CLIENT);
					Bundle passedBundle = new Bundle();
					passedBundle.putString(BlueToothService.BDL_CLASS_IDENTIFIER, Main.CLASS_IDENTIFIER);
					msg.setData(passedBundle);
					mService.send(msg);
				} catch (RemoteException e) {
					// There is nothing special we need to do if the service
					// has crashed.
				}
			}
		// Detach our existing connection.
		mApp.unbindService(mConnection);
		mIsBound = false;
		}
	}
}
