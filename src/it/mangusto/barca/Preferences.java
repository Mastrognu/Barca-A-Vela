package it.mangusto.barca;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class Preferences extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * Questo metodo è deprecato ma solo perchè non è utile utilizzando i
		 * fragment. Dato che non li stiamo usando, addPreferenceFromResource va
		 * più che bene
		 */
		addPreferencesFromResource(R.xml.preferences);
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			getActionBar().setHomeButtonEnabled(true);
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
			
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}