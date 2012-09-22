package it.makersf.barca;

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
			BlueToothThread thread = new BlueToothThread();
			mService.mClients.put(msg.getData().getString(BlueToothService.BDL_CLASS_IDENTIFIER), thread);
			//Note: il thread non è ancora stato fatto partire.
			//Sarà comunicato con un messaggio a quale MAC collegarsi, solo allora partirà
			break;
		}
			
		case BlueToothService.MSG_UNREGISTER_CLIENT:
		{
			BlueToothThread thread = mService.mClients.remove(msg.getData().getString(BlueToothService.BDL_CLASS_IDENTIFIER));
			thread.terminate();
			break;
		}
			
		default:
			super.handleMessage(msg);
		}
	}
}