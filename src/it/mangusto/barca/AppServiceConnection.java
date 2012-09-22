package it.mangusto.barca;

import it.makersf.barca.BlueToothService;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

class AppServiceConnection implements ServiceConnection{

	private final ServiceCommunicator mCommunicator;
	public AppServiceConnection(ServiceCommunicator pCommunicator) {
		mCommunicator = pCommunicator;
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		mCommunicator.mService = new Messenger(service);
		try {
			Message msg = Message.obtain(null, BlueToothService.MSG_REGISTER_CLIENT);
			Bundle passedBundle = new Bundle();
			passedBundle.putString(BlueToothService.BDL_CLASS_IDENTIFIER, Main.CLASS_IDENTIFIER);
			msg.setData(passedBundle);
			mCommunicator.mService.send(msg);

		} catch (RemoteException e) {
			/* Il servizio è crashato prima che potessimo
			 * fare niente. Ci aspettiamo a breve di essere riconessi,
			 * per cui non c'è bisogno di fare niente.
			 */
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		mCommunicator.mService = null;
		mCommunicator.attemptServiceConnection();
	}
}
