package it.makersf.barca;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

class ServiceIncomingHandler extends Handler {

	private final BlueToothService mService;
	ServiceIncomingHandler(BlueToothService pService) {
		mService = pService;
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case BlueToothService.MSG_REGISTER_CLIENT:
		{
			BlueToothThread thread = new BlueToothThread(mService);
			mService.mClients.put(msg.getData().getString(BlueToothService.BDL_CLASS_IDENTIFIER), thread);
			//Note: il thread non � ancora stato fatto partire.
			//Sar� comunicato con un messaggio a quale MAC collegarsi, solo allora partir�
			break;
		}

		case BlueToothService.MSG_UNREGISTER_CLIENT:
		{
			BlueToothThread thread = mService.mClients.remove(msg.getData().getString(BlueToothService.BDL_CLASS_IDENTIFIER));
			thread.terminate();
			break;
		}

		case BlueToothService.MSG_DEVICE_TO_CONNECT:
		{
			Bundle passedBundle = msg.getData();
			BlueToothThread thread = mService.mClients.get(passedBundle.getString(BlueToothService.BDL_CLASS_IDENTIFIER));
			thread.setDeviceToConnect( (BluetoothDevice) passedBundle.getParcelable(BlueToothService.BDL_DEVICE));
			break;
		}

		default:
			super.handleMessage(msg);
		}
	}
}