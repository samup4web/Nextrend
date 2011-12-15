package com.nextrend.reader;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.TextView;
import android.widget.Toast;

public class MusicRelayActivity extends Activity {

	private MyExpandableListAdapter mAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.relay);

		Bundle extras = getIntent().getExtras();
		String tracks_json_string = "";
		
		if (extras != null) {
			
			try {
				tracks_json_string = extras.getString("music_relay");
				Log.w("NEW ACTIVITY", tracks_json_string);

				// Set up our adapter

				mAdapter = new MyExpandableListAdapter(tracks_json_string);
				ExpandableListView epView = (ExpandableListView) findViewById(R.id.tracksExpandableList);
				epView.setAdapter(mAdapter);
			} catch (Exception e) {

			}
			// epView.setListAdapter(mAdapter);
			// registerForContextMenu(getExpandableListView());

		} else {
			Log.w("NEXTREND", "HERE-2!");

		}

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Sample menu");
		menu.add(0, 0, 0, R.string.hello);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item
				.getMenuInfo();

		String title = ((TextView) info.targetView).getText().toString();

		int type = ExpandableListView
				.getPackedPositionType(info.packedPosition);
		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
			int groupPos = ExpandableListView
					.getPackedPositionGroup(info.packedPosition);
			int childPos = ExpandableListView
					.getPackedPositionChild(info.packedPosition);
			Toast.makeText(
					this,
					title + ": Child " + childPos + " clicked in group "
							+ groupPos, Toast.LENGTH_SHORT).show();
			return true;
		} else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
			int groupPos = ExpandableListView
					.getPackedPositionGroup(info.packedPosition);
			Toast.makeText(this, title + ": Group " + groupPos + " clicked",
					Toast.LENGTH_SHORT).show();
			return true;
		}

		return false;
	}

	public class MyExpandableListAdapter extends BaseExpandableListAdapter {

		JSONObject tracks_JsonObj;
		Vector<String> v_groups = new Vector<String>();
		Vector<String[]> v_sub_info = new Vector<String[]>();

		private String[] groups;
		private String[][] children;

		public MyExpandableListAdapter(String json_string) {

			try {
				this.tracks_JsonObj = new JSONObject(json_string);

				int count = this.tracks_JsonObj.getInt("count");
				Log.w("JSON", String.valueOf(count));

				JSONArray tracks_jArray = tracks_JsonObj.getJSONArray("tracks");

				for (int i = 0; i < count; i++) {

					JSONObject per_track = tracks_jArray.getJSONObject(i);
					String timestamp = per_track.getString("timestamp");
					String artist_name = per_track.getString("artist_name");
					String track = per_track.getString("track_name");
					String album = per_track.getString("album_name");

					String[] tempInfo = { artist_name, album, timestamp };

					v_groups.add(track);
					v_sub_info.add(tempInfo);

					// Log.w("JSON", timestamp + artist_name + track + album);

				}

				groups = new String[v_groups.size()];

				int x = 0;
				for (String str : v_groups) {
					groups[x++] = str;
				}

				children = new String[v_sub_info.size()][3];

				x = 0;
				for (String[] str : v_sub_info) {
					children[x++] = str;
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		public void parseJson() {

		}

		// private String[][] children = {
		// { "Arnold", "Barry", "Chuck", "David" },
		// { "Ace", "Bandit", "Cha-Cha", "Deuce" },
		// { "Fluffy", "Snuggles" }, { "Goldy", "Bubbles" } };

		public Object getChild(int groupPosition, int childPosition) {
			return children[groupPosition][childPosition];
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public int getChildrenCount(int groupPosition) {
			int i = 0;
			try {
				i = children[groupPosition].length;

			} catch (Exception e) {
			}

			return i;
		}

		public TextView getGenericView() {
			// Layout parameters for the ExpandableListView
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, 64);

			TextView textView = new TextView(MusicRelayActivity.this);
			textView.setLayoutParams(lp);
			// Center the text vertically
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			// textView.setTextColor(R.color.marcyred);
			// Set the text starting position
			textView.setPadding(56, 0, 0, 0);
			return textView;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView textView = getGenericView();
			textView.setText(getChild(groupPosition, childPosition).toString());
			return textView;
		}

		public Object getGroup(int groupPosition) {
			return groups[groupPosition];
		}

		public int getGroupCount() {
			return groups.length;
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView textView = getGenericView();
			textView.setText(getGroup(groupPosition).toString());
			return textView;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		public boolean hasStableIds() {
			return true;
		}

	}
}
