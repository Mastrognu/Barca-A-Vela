package it.makersf.barca;

import java.util.HashMap;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Messenger;

public class BlueToothService extends Service {

	public static final int MSG_REGISTER_CLIENT = 1;
	public static final int MSG_UNREGISTER_CLIENT = 2;
	public static final String BDL_CLASS_IDENTIFIER = "ClassIdentifier";

	/* usare il notification manager per segnalare lo stato del servizio*/
	NotificationManager mNotificationManager;
	HashMap<String, BlueToothThread> mClients = new HashMap<String, BlueToothThread>(1);

	final Messenger mMessenger = new Messenger(new ServiceIncomingHandler(this));

	@Override
	public void onCreate() {
		mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	}

	@Override
	public void onDestroy() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}
}
