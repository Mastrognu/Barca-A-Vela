package it.makersf.barca;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

public class BlueToothService extends Service {

	public static final int MSG_REGISTER_CLIENT = 1;
	public static final int MSG_UNREGISTER_CLIENT = 2;
	private BlueToothThread mThread;

	NotificationManager mNotificationManager;
	ArrayList<Messenger> mClients = new ArrayList<Messenger>();
	final Messenger mMessenger = new Messenger(new IncomingHandler());

	@Override
	public void onCreate() {
		mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		mThread = new BlueToothThread();
	}

	@Override
	public void onDestroy() {
		mThread.terminate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}
	
	protected BlueToothThread getBlueToothThread() {
		return mThread;
	}

	private class IncomingHandler extends Handler {
		
		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_REGISTER_CLIENT:
				mClients.add(msg.replyTo);
				break;
				
			case MSG_UNREGISTER_CLIENT:
				mClients.remove(msg.replyTo);
				break;
				
			default:
				super.handleMessage(msg);
			}
		}
	}
}
