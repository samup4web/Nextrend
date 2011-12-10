package com.nextrend.reader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.view.View.OnClickListener;

public class StartActivity extends Activity {

	private Button hide_frame_btn;
	private ToggleButton service_btn;
	private ToggleButton send_track_btn;
	private EditText duration_txt;

	boolean service_state = true;
	boolean send_track_state = true;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		
		final AppState appState = ((AppState)getApplicationContext());		
		

		hide_frame_btn = (Button) findViewById(R.id.hide_btn);	
		service_btn = (ToggleButton) findViewById(R.id.service_state_btn);
		send_track_btn = (ToggleButton) findViewById(R.id.send_track_btn);
		duration_txt = (EditText ) findViewById(R.id.duration);

		
		duration_txt.setText(appState.getDuration());
		service_btn.setChecked(appState.getService());
		send_track_btn.setChecked(appState.getSend_track());
		
		
		hide_frame_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String time_str = duration_txt.getText().toString();
				Log.d("TEXT",time_str + "-" +service_state+ "-"+send_track_state);
				
//				Intent i = new Intent(getApplicationContext(), MainActivity.class);
//				i.putExtra("duration", time_str);
//				i.putExtra("service_state", service_state);
//				i.putExtra("send_track", send_track_state);
				//startActivity(i);
											
				finish();
			}
		});

		
		duration_txt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				appState.setDuration(duration_txt.getText().toString());
			}
		});
		
		service_btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (service_btn.isChecked()) {
					// service is on
					Log.d("TEST", "ON");
					service_state = true;
					appState.setService_state(true);
				} else {
					// service is off
					Log.d("TEST", "OFF");
					service_state = false;
					appState.setService_state(false);
				}

			}
		});

		
		
		send_track_btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (send_track_btn.isChecked()) {
					// send_track-info is on
					Log.d("TEST", "ON");
					send_track_state = true;
					appState.setSend_track(true);
				} else {
					// send_track_info is off
					Log.d("TEST", "OFF");
					send_track_state = false;
					appState.setSend_track(false);
				}

			}
		});

	}

}
