package com.nextrend.reader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.ActivityManager;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;

public class MainActivity extends ListActivity {

	/** Called when the activity is first created. */
	private ArrayAdapter<String> adapter;
	public static final String SERVICECMD = "com.android.music.musicservicecommand";
	public static final String CMDNAME = "command";
	public static final String CMDTOGGLEPAUSE = "togglepause";
	public static final String CMDSTOP = "stop";
	public static final String CMDPAUSE = "pause";
	public static final String CMDPREVIOUS = "previous";
	public static final String CMDNEXT = "next";

	private String tag_ID;

	private boolean playingTrack_flag;

	private String serverAddress = "http://130.240.95.226";

	private int duration; // MINS default - 30mins

	public MainActivity() {
		//
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create the ArrayAdapter for the List Activity
		// this.adapter = new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1, android.R.id.text1);
		//
		// setListAdapter(adapter);

		// Process the intent that started this activity. It should be a Tag
		// Discovery related intent

		// ActivityManager am =
		// (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
		//
		// List<ActivityManager.RunningServiceInfo> rs =
		// am.getRunningServices(50);
		//
		// for (int i=0; i<rs.size(); i++) {
		// ActivityManager.RunningServiceInfo rsi = rs.get(i);
		// Log.i("Service", "Process " + rsi.process + " with component " +
		// rsi.service.getClassName());
		// }

		//

		// Bundle extras = getIntent().getExtras();
		// if (extras != null) {
		// String value = extras.getString("duration");
		// Log.w("TEST", value);
		// }

		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		playingTrack_flag = audioManager.isMusicActive();

		AppState appState = ((AppState) getApplicationContext());

		this.resolveIntent(this.getIntent());

		if (playingTrack_flag && appState.getSend_track()) {
			Log.w("OK", "here");

			IntentFilter iF = new IntentFilter();
			iF.addAction("com.android.music.metachanged");
			// iF.addAction("com.android.music.playstatechanged");
			// iF.addAction("com.android.music.playbackcomplete");
			// iF.addAction("com.android.music.queuechanged");

			registerReceiver(mReceiver, iF);

		} else {

		}

	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String cmd = intent.getStringExtra("command");
			// Log.i("mIntentReceiver.onReceive ", action + " / " + cmd);
			String artist = intent.getStringExtra("artist");
			String album = intent.getStringExtra("album");
			String track = intent.getStringExtra("track");
			Log.d("Music", artist + ":" + album + ":" + track);

			try {
				post_data(tag_ID, artist, album, track);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
			finish();

		}
	};

	@Override
	protected void onStart() {
		super.onStart();
		// The activity is about to become visible.

	}

	@Override
	protected void onResume() {
		super.onResume();
		// The activity has become visible (it is now "resumed").
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.unregisterReceiver(mReceiver);

		// Another activity is taking focus (this activity is about to be
		// "paused").
	}

	@Override
	protected void onStop() {
		super.onStop();
		// The activity is no longer visible (it is now "stopped")
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// The activity is about to be destroyed.
	}

	@Override
	public void onNewIntent(Intent intent) {
		setIntent(intent);
		resolveIntent(intent);
	}

	private void resolveIntent(Intent intent) {

		// set current duration
		AppState appState = ((AppState) getApplicationContext());
		try {
			duration = Integer.parseInt(appState.getDuration());
		} catch (Exception e) {
			duration = 30;
		}

		// Get the Action
		String action = intent.getAction();

		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

			// Tag tagFromIntent =
			// intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

			// MifareUltralight mfu = MifareUltralight.get(tagFromIntent);

			byte[] byte_id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
			tag_ID = getHexString(byte_id, byte_id.length);

			// Log.e(tag_ID, "TAG-ID");

			try {
				String response = get_data(tag_ID);
				Log.e(tag_ID, response);
				show_music_relay(response);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}

			finish();
			// this.unregisterReceiver(mReceiver);
		}

	}

	private void show_music_relay(String music_relay) {
		// TODO Auto-generated method stub
		Intent i = new Intent(getApplicationContext(), MusicRelayActivity.class);
		i.putExtra("music_relay", music_relay);		
		startActivity(i);
		finish();
	}

	private void post_data(String tag_ID, String artist, String album,
			String track) throws IOException {

		long epoch_time = System.currentTimeMillis();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(epoch_time);
		String timestamp = formatter.format(date);

		Log.d("Sending info", "sending info for " + tag_ID + ", " + artist
				+ ", " + album + ", " + timestamp);

		String address = serverAddress + "/nextrend/api/submit/track_info";

		RestClient client = new RestClient(address);
		client.AddParam("tag_id", tag_ID);
		client.AddParam("artist", artist);
		client.AddParam("album", album);
		client.AddParam("track", track);
		client.AddParam("timestamp", timestamp);
		client.AddHeader("Content-type", "application/x-www-form-urlencoded");

		try {
			client.Execute(RequestMethod.POST);
			String responseCode = String.valueOf(client.getResponseCode());
			String response = client.getResponse();
			Log.d("NeXtrend", response);
		} catch (Exception e) {
			// e.printStackTrace();
			Log.e("NEXTREND", "Connection Error!");
		}

		// Log.d("POST-response", entityContents);
	}

	private String get_data(String tag_ID) throws IOException {

		String response = "";

		long epoch_time = System.currentTimeMillis();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(epoch_time);
		String timestamp = formatter.format(date);

		Log.d("Get info", "getting info for " + tag_ID + ", dur-" + duration
				+ ", " + timestamp);

		String address = serverAddress + "/nextrend/api/request/get_track_info";

		RestClient client = new RestClient(address);
		client.AddParam("tag_id", tag_ID);
		client.AddParam("timestamp", timestamp);
		client.AddParam("duration", String.valueOf(duration));
		client.AddHeader("Content-type", "application/x-www-form-urlencoded");

		//Log.d("tetete",timestamp);
		try {
			client.Execute(RequestMethod.GET);
			String responseCode = String.valueOf(client.getResponseCode());
			response = client.getResponse();
			//Log.d("NeXtrend", response);

		} catch (Exception e) {
			// e.printStackTrace();
			Log.e("NEXTREND", "Connection Error!");
		}

		return response;

	}

	// Hex help
	private static final byte[] HEX_CHAR_TABLE = { (byte) '0', (byte) '1',
			(byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6',
			(byte) '7', (byte) '8', (byte) '9', (byte) 'A', (byte) 'B',
			(byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F' };

	public static String getHexString(byte[] raw, int len) {
		byte[] hex = new byte[2 * len];
		int index = 0;
		int pos = 0;

		for (byte b : raw) {
			if (pos >= len)
				break;

			pos++;
			int v = b & 0xFF;
			hex[index++] = HEX_CHAR_TABLE[v >>> 4];
			hex[index++] = HEX_CHAR_TABLE[v & 0xF];
		}

		return new String(hex);
	}

}