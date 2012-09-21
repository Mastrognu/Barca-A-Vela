package it.mangusto.barca;

import android.os.Bundle;
import android.preference.PreferenceActivity;

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
	}

}