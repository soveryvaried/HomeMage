package com.soveryvaried.homemage;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.soveryvaried.homemage.db.HomeMageDatabase;
import com.soveryvaried.homemage.db.HomeMageProvider;
import com.soveryvaried.homemage.db.Node;
import com.soveryvaried.homemage.isy.ISYService;

public class NodeDetailActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
	public static final String INTENT_NODE_ADDRESS = "nodeAddr";
	public static final String INTENT_NODE_NAME = "nodeName";
	public static final String INTENT_NODE_STATUS = "nodeStatus";
	private String address = "";
	private static final String DEBUG_TAG = "NodeDetailActivity";
	
	public NodeDetailActivity() {
		// TODO Auto-generated constructor stub
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		
		Intent intent = getIntent();
		address = intent.getStringExtra(INTENT_NODE_ADDRESS);

		//Get node details from database
		getLoaderManager().initLoader(0, null, this);
	}
	
	private void populateScreen(Node node) {
		TextView nodeNameVw = (TextView) findViewById(R.id.nameTxt);
		nodeNameVw.setText(node.getName());
		
		TextView nodeAddrVw = (TextView) findViewById(R.id.addrTxt);
		nodeAddrVw.setText(address);

        TextView nodeGroupVw = (TextView) findViewById(R.id.groupTxt);
        nodeGroupVw.setText("Group " + node.getGroup());

        SeekBar dimBar = (SeekBar) findViewById(R.id.dimBar);
        if (node.isDimmable()) {
            dimBar.setProgress(node.getStatus());
            dimBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {}

                public void onStartTrackingTouch(SeekBar seekbar) {}

                public void onStopTrackingTouch(SeekBar seekbar) {
                    TextView nodeAddrVw = (TextView) findViewById(R.id.addrTxt);
                    int progress = seekbar.getProgress();
                    String address = nodeAddrVw.getText().toString();
                        Toast toast = Toast.makeText(seekbar.getContext(), "Changed level to "+progress , Toast.LENGTH_SHORT);
                        toast.show();
                        Intent intent = new Intent(seekbar.getContext(), ISYService.class);
                        intent.putExtra(ISYService.INTENT_EXTRA_METHOD, ISYService.METHOD_BRIGHT_DIM_NODE);
                        intent.putExtra(ISYService.INTENT_EXTRA_NODE_ADDRESS, address);
                        intent.putExtra(ISYService.INTENT_EXTRA_BRIGHT_DIM_LEVEL, progress);
                        seekbar.getContext().startService(intent);
                }
            });
        } else {
            dimBar.setEnabled(false);
            dimBar.setVisibility(View.INVISIBLE);
            dimBar.setFocusable(false);
        }
		ToggleButton onOffButton = (ToggleButton) findViewById(R.id.onOffButton);
		if (node.getStatus() > 0) {
			onOffButton.setChecked(true);
		} else {
			onOffButton.setChecked(false);
		}
		onOffButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TextView nodeAddrVw = (TextView) findViewById(R.id.addrTxt);
				String address = nodeAddrVw.getText().toString();
				boolean on = ((ToggleButton) v).isChecked();
				if (on) {
					Toast toast = Toast.makeText(v.getContext(), "Button turned on", Toast.LENGTH_SHORT);
					toast.show();
					Intent intent = new Intent(v.getContext(), ISYService.class);
					intent.putExtra(ISYService.INTENT_EXTRA_METHOD, ISYService.METHOD_TURN_NODE_ON);
					intent.putExtra(ISYService.INTENT_EXTRA_NODE_ADDRESS, address);
			    	v.getContext().startService(intent);
					
				} else {
					Toast toast = Toast.makeText(v.getContext(), "Button turned off", Toast.LENGTH_SHORT);
					toast.show();
					Intent intent = new Intent(v.getContext(), ISYService.class);
					intent.putExtra(ISYService.INTENT_EXTRA_METHOD, ISYService.METHOD_TURN_NODE_OFF);
					intent.putExtra(ISYService.INTENT_EXTRA_NODE_ADDRESS, address);
			    	v.getContext().startService(intent);
				}	
			}
		});

		Button fastOnButton = (Button) findViewById(R.id.fastOnButton);
		fastOnButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TextView nodeAddrVw = (TextView) findViewById(R.id.addrTxt);
				String address = nodeAddrVw.getText().toString();
					Toast toast = Toast.makeText(v.getContext(), "Button turned on", Toast.LENGTH_SHORT);
					toast.show();
					Intent intent = new Intent(v.getContext(), ISYService.class);
					intent.putExtra(ISYService.INTENT_EXTRA_METHOD, ISYService.METHOD_TURN_NODE_FAST_ON);
					intent.putExtra(ISYService.INTENT_EXTRA_NODE_ADDRESS, address);
			    	v.getContext().startService(intent);
			}
		});

		Button fastOffButton = (Button) findViewById(R.id.fastOffButton);
		fastOffButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TextView nodeAddrVw = (TextView) findViewById(R.id.addrTxt);
				String address = nodeAddrVw.getText().toString();
					Toast toast = Toast.makeText(v.getContext(), "Button turned on", Toast.LENGTH_SHORT);
					toast.show();
					Intent intent = new Intent(v.getContext(), ISYService.class);
					intent.putExtra(ISYService.INTENT_EXTRA_METHOD, ISYService.METHOD_TURN_NODE_FAST_OFF);
					intent.putExtra(ISYService.INTENT_EXTRA_NODE_ADDRESS, address);
			    	v.getContext().startService(intent);
			}
		});		
	}
	
	//public NodeDetailActivity getActivity() {
	//	return this;
	//}

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// This is called when a new Loader needs to be created. This
		// sample only has one Loader, so we don't care about the ID.

		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.
		String[] projection = { HomeMageDatabase.ID, HomeMageDatabase.COL_NAME, HomeMageDatabase.COL_ADDRESS,
				HomeMageDatabase.COL_STATUS, HomeMageDatabase.COL_TYPE, HomeMageDatabase.COL_GROUP_ID };
		String selection = HomeMageDatabase.COL_ADDRESS + " = ?";
		String[] selectionArgs = {address};
		Log.v(DEBUG_TAG, "creating loader with '" + selection + "'   " + address);
		return new CursorLoader(getApplicationContext(), HomeMageProvider.CONTENT_URI, projection, selection, selectionArgs, null);

	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Swap the new cursor in. (The framework will take care of closing the
		// old cursor once we return.)
		data.moveToFirst();
		String addr = data.getString(data.getColumnIndex(HomeMageDatabase.COL_ADDRESS));
		String nm = data.getString(data.getColumnIndex(HomeMageDatabase.COL_NAME));
		int st = data.getInt(data.getColumnIndex(HomeMageDatabase.COL_STATUS));
        String type = data.getString(data.getColumnIndex(HomeMageDatabase.COL_TYPE));
        int group = data.getInt(data.getColumnIndex(HomeMageDatabase.COL_GROUP_ID));
        Node node = new Node();
        node.setAddress(addr);
        node.setName(nm);
        node.setStatus(st);
        node.setType(type);
        node.setGroup(group);
		populateScreen(node);
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		// This is called when the last Cursor provided to onLoadFinished()
		// above is about to be closed. We need to make sure we are no
		// longer using it.
	}

}
