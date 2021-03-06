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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.soveryvaried.homemage.db.HomeMageDatabase;
import com.soveryvaried.homemage.db.HomeMageProvider;
import com.soveryvaried.homemage.isy.ISYService;

public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

	private NodeListAdapter nodeListAdapter;
	//private static final String DEBUG_TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		nodeListAdapter = new NodeListAdapter(this, null);
		ListView nodeListVw = (ListView) findViewById(R.id.nodeListVw);
		nodeListVw.setAdapter(nodeListAdapter);
		nodeListVw.setOnItemClickListener(new NodeListOnItemClickListener());

		getLoaderManager().initLoader(0, null, this);

		Intent intent = new Intent(this, ISYService.class);
		intent.putExtra(ISYService.INTENT_EXTRA_METHOD, ISYService.METHOD_RETRIEVE_NODES);
		startService(intent);
	}

	public MainActivity getActivity() {
		return this;
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// This is called when a new Loader needs to be created. This
		// sample only has one Loader, so we don't care about the ID.

		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.
		String[] projection = { HomeMageDatabase.ID, HomeMageDatabase.COL_NAME, HomeMageDatabase.COL_ADDRESS,
				HomeMageDatabase.COL_STATUS };
        String sortOrder = HomeMageDatabase.COL_NAME;
		return new CursorLoader(getActivity(), HomeMageProvider.CONTENT_URI, projection, null, null, sortOrder);
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Swap the new cursor in. (The framework will take care of closing the
		// old cursor once we return.)
		nodeListAdapter.swapCursor(data);
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		// This is called when the last Cursor provided to onLoadFinished()
		// above is about to be closed. We need to make sure we are no
		// longer using it.
		nodeListAdapter.swapCursor(null);
	}

	private class NodeListOnItemClickListener implements AdapterView.OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.v("nodeListOnClickItem", "start");
			Toast.makeText(getApplicationContext(), "Click NodeItem #" + position, Toast.LENGTH_LONG).show();
			Log.v("nodeListOnClickItem", "end");
		}
	}
	
	public void showNodeDetail(String address, String name, int status) {
		//Call ISYService to update node details
		Intent isyIntent = new Intent(this, ISYService.class);
		isyIntent.putExtra(ISYService.INTENT_EXTRA_METHOD, ISYService.METHOD_UPDATE_NODE);
		isyIntent.putExtra(ISYService.INTENT_EXTRA_NODE_ADDRESS, address);
  	    startService(isyIntent);

		//Start NodeDetailActivity
		Intent activityIntent = new Intent(this, NodeDetailActivity.class);
		activityIntent.putExtra(NodeDetailActivity.INTENT_NODE_ADDRESS, address);
		startActivity(activityIntent);
	}
	
	public void toggleNode(String address, boolean isOn) {
		Toast toast = Toast.makeText(this, "Button turned " + (isOn?"on":"off"), Toast.LENGTH_SHORT);
		toast.show();

		Intent intent = new Intent(this, ISYService.class);
		if (isOn) {
			intent.putExtra(ISYService.INTENT_EXTRA_METHOD, ISYService.METHOD_TURN_NODE_ON);
		} else {
			intent.putExtra(ISYService.INTENT_EXTRA_METHOD, ISYService.METHOD_TURN_NODE_OFF);
		}
		intent.putExtra(ISYService.INTENT_EXTRA_NODE_ADDRESS, address);
  	startService(intent);
	}
}
