package com.oopl.breadcrumbs;

import android.location.Location;

public class MessageData {
	public double latitude;
	public double longitude;
	public String message;
	public int position;
	
	public MessageData(double lat, double lon, String mess, int pos) {
		latitude = lat;
		longitude = lon;
		message = mess; 
		position = pos;
	}
}
