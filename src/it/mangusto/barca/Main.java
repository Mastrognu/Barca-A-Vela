package it.mangusto.barca;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class Main extends Activity {
	
	private int REQUEST_ENABLE_BT = 1;
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        getActionBar().setHomeButtonEnabled(false);
        
        // Attivo il Bluetooth se non è già attivo
        enableBt();
        
    }
    
    public void enableBt() {
    	
		if (mBluetoothAdapter == null) {
		    //TODO Il dispositivo non supporta il Bluetooth
		}
		
		if (!mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
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
            	
            case R.id.menu_settings:            	
            	Toast.makeText(getApplicationContext(),"Settings",Toast.LENGTH_SHORT).show();
            	return true;
            	
        }
        return super.onOptionsItemSelected(item);
    }

}
