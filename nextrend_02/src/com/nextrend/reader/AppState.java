package com.nextrend.reader;

import android.app.Application;

public class AppState extends Application {

	private String duration = "30";
	private boolean service = true;
	private boolean send_track = true;

	
	
	public String getDuration() {
		return duration;
	}
	public boolean getService(){
		return service;
	}
	public boolean getSend_track(){
		return send_track;
	}

	
	
	public void setDuration(String time) {
		duration = time;
	}
	public void setService_state(boolean state){
		service = state;
	}
	public void setSend_track(boolean state){
		send_track = state;
	}
}