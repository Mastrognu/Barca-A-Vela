package it.mangusto.barca;

import java.util.Set;

import it.makersf.barca.BlueToothService;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class ServiceCommunicator {
	public static final int BLUETOOTH_NOT_SUPPORTED = 0;
	public static final int UNABLE_TO_TURN_ON_BLUETOOTH = 1;
	public static final int REQUEST_ENABLE_BT = 1;

	private final Main mApp;
	private Messenger mService;
	private boolean mIsBound;
	private DiscoverDeviceCallBack mNewDeviceCallBack;

	private ServiceConnection mConnection;
	private BluetoothAdapter mBTAdapter;

	private int mLastValue;

	public ServiceCommunicator(Main pApp) {
		mApp = pApp;
		mConnection = new AppServiceConnection(this);
		mBTAdapter = BluetoothAdapter.getDefaultAdapter();

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		mApp.registerReceiver(mBluetoothMessageReceiver, filter);

		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		mApp.registerReceiver(mBluetoothMessageReceiver, filter);

		filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		mApp.registerReceiver(mBluetoothMessageReceiver, filter);

		filter = new IntentFilter(BlueToothService.BROADCAST_ACTION);
		mApp.registerReceiver(mServiceMessageReceiver, filter);
	}

	public int getLastValue() {
		return mLastValue;
	}

	public void attemptServiceConnection() {
		// Attivo il Bluetooth se non è già attivo
		if (mBTAdapter == null) {
			mApp.unusableBlueTooth(BLUETOOTH_NOT_SUPPORTED);
		}

		if (mBTAdapter.isEnabled()) {
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

	/**
	 * Ritorna i devices già trovati e inizia a cercarne di nuovi. Praticamente sempre tornerà un Set vuoto, quindi servirsi della DiscoveryDeviceCallBack
	 * per gestire i nuovi device trovati (come per esempio aggiungerli a una ArrayAdapter per visualizzarli).
	 *
	 * @param pNewDeviceDiscoveredCallBack Ogni volta che viene rilevato un nuovo device, viene chiamato il metodo di questa Callback
	 * @return Un Set contenente i devices già trovati. Può essere vuoto.
	 */
	public Set<BluetoothDevice> requestDeviceList(DiscoverDeviceCallBack pNewDeviceDiscoveredCallBack) {
		if(mBTAdapter == null)
			Log.e(BaVCostants.DEGUB_TAG, "Hai provato a richiedere i device, ma il bluetooth non è supportato.");

		mNewDeviceCallBack = pNewDeviceDiscoveredCallBack;

		if(mBTAdapter.isDiscovering())
			mBTAdapter.cancelDiscovery();

		mBTAdapter.startDiscovery();

		return mBTAdapter.getBondedDevices();
	}

	public void connectToDevice(BluetoothDevice pDevice) {
		if(mBTAdapter == null)
			Log.e(BaVCostants.DEGUB_TAG, "Hai provato a conneterti a un device, ma il bluetooth non è supportato.");
		if(pDevice == null)
			Log.e(BaVCostants.DEGUB_TAG, "Il pDevice passato è null.", new IllegalArgumentException());

		// Cancel discovery because it's costly and we're about to connect
		mBTAdapter.cancelDiscovery();

		try {
			Message msg = Message.obtain(null, BlueToothService.MSG_DEVICE_TO_CONNECT);
			Bundle passedBundle = new Bundle();
			passedBundle.putString(BlueToothService.BDL_CLASS_IDENTIFIER, Main.CLASS_IDENTIFIER);
			passedBundle.putParcelable(BlueToothService.BDL_DEVICE, pDevice);
			msg.setData(passedBundle);
			mService.send(msg);
		} catch (RemoteException e) {
			Log.e(BaVCostants.DEGUB_TAG, e.getMessage());
		}
	}

	public void onDestroy() {
		//clean everything up here!
		if(mBTAdapter != null)
			mBTAdapter.cancelDiscovery();

		mApp.unregisterReceiver(mBluetoothMessageReceiver);
		doUnbindService();
	}

	Messenger getMessanger() {
		return mService;
	}

	void setMessanger(Messenger pMessanger) {
		mService = pMessanger;
	}
	//===========================================================
	// Private Methods
	//===========================================================

	private void doBindService() {
		if(mBTAdapter.getState() != BluetoothAdapter.STATE_ON)
			Log.e(BaVCostants.DEGUB_TAG, "Non dovrebbe essere fatto partire il servizio se il bluetooth non è pronto!");

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
					Log.e(BaVCostants.DEGUB_TAG, e.getMessage()); //TODO just so that while testing we know. To remove on release
				}
			}
			// Detach our existing connection.
			mApp.unbindService(mConnection);
			mIsBound = false;
		}
	}

	private final BroadcastReceiver mBluetoothMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// If it's already paired, skip it, because it's been listed already
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					if(mNewDeviceCallBack != null)
						mNewDeviceCallBack.onNewDeviceDiscovered(device);
				}
				// When discovery is finished
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				mNewDeviceCallBack.onDiscoveryFinished();
				mNewDeviceCallBack = null;
			} else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				//TODO
				//il bluetooth è stato spento. Gestire la situazione!
				//NOTA: anche il servizio può registrare un broadcast receiver, per cui
				//si può decidere di gestire il tutto da servizio e notificare l'app con un intent
			}
		}
	};

	private final BroadcastReceiver mServiceMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			//String action = intent.getAction();
			mLastValue = intent.getBundleExtra(BlueToothService.BROADCAST_EXTRA_NAME).getInt("value");
		}
	};

	static interface DiscoverDeviceCallBack {
		void onNewDeviceDiscovered(BluetoothDevice pDevice);
		void onDiscoveryFinished();
	}
}
