package it.mangusto.barca;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * Questo metodo � deprecato ma solo perch� non � utile utilizzando i
		 * fragment. Dato che non li stiamo usando, addPreferenceFromResource va
		 * pi� che bene
		 */
		addPreferencesFromResource(R.xml.preferences);
	}

}