package it.mangusto.barca;

import it.makersf.barca.BlueToothService;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NavUtils;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class Main extends SherlockActivity {
	
	private Messenger mService;
	private boolean mIsBound;
	final Messenger mMessenger = new Messenger(new Handler() {

		@Override
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}

	});

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = new Messenger(service);
			try {
				Message msg = Message.obtain(null, BlueToothService.MSG_REGISTER_CLIENT);
				msg.replyTo = mMessenger;
				mService.send(msg);

			} catch (RemoteException e) {
				/* Il servizio è crashato prima che potessimo
				 * fare niente. Ci aspettiamo a breve di essere riconessi,
				 * per cui non c'è bisogno di fare niente.
				 */
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
			/* Il servizio è crashato (ma dopo che ci fossimo già connessi).*/
		}
	};

	private int REQUEST_ENABLE_BT = 1;
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		getSupportActionBar().setHomeButtonEnabled(false);

		// Attivo il Bluetooth se non è già attivo
		if (mBluetoothAdapter == null) {
			unusableBlueTooth();
		}

		if (mBluetoothAdapter.isEnabled()) {
			doBindService();
		} else {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			//doBindService() sarà chiamato da onActivityResult solo se il BT è stato attivato correttamente
		}
	}

	void doBindService() {
		bindService(new Intent(this,
				BlueToothService.class), mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	void doUnbindService() {
		if (mIsBound) {
			// If we have received the service, and hence registered with
			// it, then now is the time to unregister.
			if (mService != null) {
				try {
					Message msg = Message.obtain(null, BlueToothService.MSG_UNREGISTER_CLIENT);
					msg.replyTo = mMessenger;
					mService.send(msg);
				} catch (RemoteException e) {
					// There is nothing special we need to do if the service
					// has crashed.
				}
			}
		// Detach our existing connection.
		unbindService(mConnection);
		mIsBound = false;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			doBindService();
		} else {
			unusableBlueTooth();
		}
	}

	private void unusableBlueTooth() {
		/* TODO
		 * questa classe viene chiamata quando il bluetooth non è supportato
		 * o non si riesce ad accendere il sensore
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getSupportMenuInflater().inflate(R.menu.main, menu);
		/*
		menu.add(R.string.actionbar_bluetooth).setIcon(R.drawable.ic_action_bluetooth).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add(R.string.actionbar_trigger).setIcon(R.drawable.ic_action_trigger).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(R.string.actionbar_settings).setIcon(R.drawable.ic_action_settings).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		*/
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;

			case R.id.menu_bluetooth:
				Intent intentDiscoverDevices = new Intent(this, DiscoverDevices.class);
				startActivity(intentDiscoverDevices);
				return true;

			case R.id.menu_trigger:
				Toast.makeText(getApplicationContext(),"Trigger",Toast.LENGTH_SHORT).show();
				return true;

			case R.id.menu_settings:   	
            	Intent intentPreferences = new Intent(this, Preferences.class);
        		startActivity(intentPreferences);
            	return true;
            	
        }
        return super.onOptionsItemSelected(item);
    }

}
