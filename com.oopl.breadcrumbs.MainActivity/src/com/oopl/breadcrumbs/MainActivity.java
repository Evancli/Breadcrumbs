package com.oopl.breadcrumbs;

import java.util.ArrayList;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements
		OnSharedPreferenceChangeListener {
	private ActionBar bar;
	private View view;
	private SharedPreferences prefs;
	private LocationManager locationManager;
	private LocationProvider provider;
	private ArrayList<MessageData> messages = new ArrayList<MessageData>();

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
		//messageView.setAlpha(0f);
		animationView = new AnimationView(this);
		containerView.addView(animationView);

		// Get preferences
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
		getMessagePositions();
		
		startLocationSearch();
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
		
		final String pass = prefs.getString("pref_password", null);
		// Log.d("MA", pass);
		final Intent settingIntent = new Intent(this, SettingsActivity.class);

		if (pass == null || pass.contentEquals("")) {

			startActivity(settingIntent);

		} else {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Password Protected");
			alert.setMessage("Enter Password:");

			final EditText input = new EditText(this);
			alert.setView(input);

			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							String value = input.getText().toString();
							// Log.d("MA", "Pin Value : " + value);
							if (value.contentEquals(pass)) {
								startActivity(settingIntent);
							}
							return;
						}
					});

			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							return;
						}
					});
			alert.show();
		}

		
		
		 
		//fadeInText("Test");
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
	 * Check Location Services Active
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
	 * get message data from settings
	 */
	public void getMessagePositions() {
		messages.clear();

		String lat1 = prefs.getString("pref_mess_1_lat", null);
		String long1 = prefs.getString("pref_mess_1_long", null);
		String mess1 = prefs.getString("pref_mess_1_mess", null);

		if (lat1 != null && long1 != null && mess1 != null) {
			messages.add(new MessageData(Double.parseDouble(lat1), Double
					.parseDouble(long1), mess1, 1));
		}

		String lat2 = prefs.getString("pref_mess_2_lat", null);
		String long2 = prefs.getString("pref_mess_2_long", null);
		String mess2 = prefs.getString("pref_mess_2_mess", null);

		if (lat2 != null && long2 != null && mess2 != null) {
			messages.add(new MessageData(Double.parseDouble(lat2), Double
					.parseDouble(long2), mess2, 2));
		}

		String lat3 = prefs.getString("pref_mess_3_lat", null);
		String long3 = prefs.getString("pref_mess_3_long", null);
		String mess3 = prefs.getString("pref_mess_3_mess", null);

		if (lat3 != null && long3 != null && mess3 != null) {
			messages.add(new MessageData(Double.parseDouble(lat3), Double
					.parseDouble(long3), mess3, 3));
		}

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
	 * 
	 * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener#
	 * onSharedPreferenceChanged(android.content.SharedPreferences,
	 * java.lang.String)
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
	 * Ping Animation End
	 */
	public void stopAnimation() {

	}

	/*
	 * Fade In text
	 */
	public void fadeInText(String text) {
		AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
		fadeIn.setDuration(1200);
		fadeIn.setFillAfter(true);
		messageView.setText(text);
		messageView.setVisibility(View.VISIBLE);
		messageView.startAnimation(fadeIn);
	}

	/*
	 * Fade Out Text
	 */
	public void fadeOutText() {
		AlphaAnimation fadeOut = new AlphaAnimation( 1.0f , 0.0f ) ; 
	     fadeOut.setDuration(1200);
	    fadeOut.setFillAfter(true);
	    messageView.setText("");
	    messageView.startAnimation(fadeOut);
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

			animationView.play();
			
			float range = Float.parseFloat(prefs.getString("pref_range", "10"));
			double lat = location.getLatitude();
			double lon = location.getLongitude();

			float[] results = new float[3];

			for (int i = 0; i < messages.size(); i++) {
				MessageData data = messages.get(i);
				
				
				

				Location.distanceBetween(lat, lon, data.latitude,
						data.longitude, results);
				
				Log.d("MA", "lat:"+lat + " long:"+ lon + " pos lat:"+ data.latitude + " pos long:" + data.longitude + " distance:"+results[0]+" range:"+range);
				
				if (results[0] <= range) {
					Log.d("MA", "MEssage fire");
					fadeInText(data.message);
				}
			}

			// locationManager.removeUpdates(this);
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
