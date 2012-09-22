package it.mangusto.barca;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class Main extends SherlockActivity {

	static final String CLASS_IDENTIFIER = Main.class.toString();

	/*
	 * Usare questo per qualunque comunicazione con il server
	 */
	private ServiceCommunicator mCommunicator;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mCommunicator = new ServiceCommunicator(this);

		getSupportActionBar().setHomeButtonEnabled(false);

		mCommunicator.attemptServiceConnection();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == ServiceCommunicator.REQUEST_ENABLE_BT)
			mCommunicator.onResultFromBlueToothEnableRequest(resultCode, data);
	}

	void unusableBlueTooth(final ServiceCommunicator.BLUETOOTH_FAILED_CONNECTION pReason) {
		/* TODO
		 * questa classe viene chiamata quando il bluetooth non è supportato
		 * o non si riesce ad accendere il sensore.
		 *
		 * MANGUSTO, gestire questa cosa e mostrarla all'utente è roba da UI, la lascio a te ;)
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

	@Override
	public void onDestroy() {
		mCommunicator.onDestroy();
	}
}
