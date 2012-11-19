package com.oopl.breadcrumbs;

import com.oopl.breadcrumbs.R;
import com.oopl.breadcrumbs.SettingsActivity;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements OnSharedPreferenceChangeListener {
	private ActionBar bar;
	private View view;
	private SharedPreferences prefs;
	private LocationManager locationManager;
	private LocationProvider provider;
	
	private AnimationView animationView;
	private RelativeLayout containerView;
	private TextView messageView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		bar = getActionBar();
		bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
		
		view = this.getWindow().getDecorView();
		
		setContentView(R.layout.activity_main);
		
		containerView = (RelativeLayout) this.findViewById(R.id.containerView);
		messageView = (TextView) this.findViewById(R.id.messageView);
		messageView.setVisibility(View.INVISIBLE);
		animationView = new AnimationView(this);
		containerView.addView(animationView);
		
		//Get preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        // register preference change listener
        prefs.registerOnSharedPreferenceChangeListener(this);

		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		// Retrieve a list of location providers that have fine accuracy, no
		// monetary cost, etc
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
		criteria.setCostAllowed(false);

		String providerName = locationManager.getBestProvider(criteria, true);

		// If no suitable provider is found, null is returned.
		if (providerName != null) {
			provider = locationManager.getProvider(providerName);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			callSettings(this.getWindow().getDecorView());
			return true;
		default:
			return false;
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		
		setActivityBackgroundColor(prefs.getString("pref_bgcolor", "blue"));
	}

	/*
	 * Location settings.
	 */
	private void enableLocationSettings() {
		Intent settingsIntent = new Intent(
				Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(settingsIntent);
	}

	/*
	 * Switch to setting activity.
	 */
	public void callSettings(View view) {
		//Intent settingIntent = new Intent(this, SettingsActivity.class);
		//startActivity(settingIntent);
		animationView.onPlay();
	}

	/*
	 * Start Location Manager updates
	 */
	public void startLocationSearch() {
		if (checkLocationAvailibility()) {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 1000, // 1-second interval.
					10, // 10 meters.
					listener);
		}
	}

	/*
	 *  Check Location Services Active
	 */
	public boolean checkLocationAvailibility() {
		final boolean gpsEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (!gpsEnabled) {
			// Build an alert dialog here that requests that the user enable
			// the location services, then when the user clicks the "OK" button,
			// call enableLocationSettings()

			final AlertDialog alertDialog = new AlertDialog.Builder(this)
					.create();
			alertDialog.setTitle("Location Services");
			alertDialog
					.setMessage("Location services are required to use this app. Please enable location services.");
			alertDialog.setIcon(R.drawable.report_dark);

			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					alertDialog.dismiss();
					enableLocationSettings();
				}
			});

			alertDialog.show();
		}

		return gpsEnabled;
	}

	/*
	 * Sets background color based on preferences.
	 */
	public void setActivityBackgroundColor(String colorStr) {
		int color1;
		int color2;
		if (colorStr.contentEquals("blue")) {
			color1 = Color.parseColor("#33B5E5");
			color2 = Color.parseColor("#0099CC");
		} else if (colorStr.contentEquals("red")) {
			color1 = Color.parseColor("#FF4444");
			color2 = Color.parseColor("#CC0000");
		} else if (colorStr.contentEquals("green")) {
			color1 = Color.parseColor("#99CC00");
			color2 = Color.parseColor("#669900");
		} else if (colorStr.contentEquals("orange")) {
			color1 = Color.parseColor("#FFBB33");
			color2 = Color.parseColor("#FF8800");
		} else if (colorStr.contentEquals("purple")) {
			color1 = Color.parseColor("#AA66CC");
			color2 = Color.parseColor("#9933CC");
		} else {
			color1 = Color.parseColor("#33B5E5");
			color2 = Color.parseColor("#0099CC");
		}
	
		
	    bar.setBackgroundDrawable(new ColorDrawable(color1));

	    view.setBackgroundColor(color1);
	    
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener#onSharedPreferenceChanged(android.content.SharedPreferences, java.lang.String)
	 */
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
	
	
	/*
	 * Ping Animation Start
	 */
	public void startAnimation() {
		
	}
	
	/*
	 *  Ping Animation End
	 */
	public void stopAnimation() {
		
	}
	
	/*
	 * Location Listener
	 */

	private final LocationListener listener = new LocationListener() {

		public void onLocationChanged(Location location) {
			// A new location update is received. Do something useful with it.
			// In this case,
			// we're sending the update to a handler which then updates the UI
			// with the new
			// location.

			//locationManager.removeUpdates(this);
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}
	};

	
}
