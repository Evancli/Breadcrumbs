package com.oopl.breadcrumbs;

import com.oopl.breadcrumbs.R;
import com.oopl.breadcrumbs.SettingsActivity;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	private LocationManager locationManager;
	private LocationProvider provider;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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
	public void onStart() {

	}

	@Override
	public void onResume() {

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
		Intent settingIntent = new Intent(this, SettingsActivity.class);
		startActivity(settingIntent);

	}

	public void startLocationSearch() {
		if (checkLocationAvailibility()) {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 10000, // 10-second interval.
					10, // 10 meters.
					listener);
		}
	}

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
			// alertDialog.setIcon(R.drawable.report_dark);

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
