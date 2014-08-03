package com.soveryvaried.homemage;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.soveryvaried.homemage.db.HomeMageDatabase;

public class NodeListAdapter extends CursorAdapter {
	private static final String DEBUG_TAG = "NodeListAdapter";
    private NodeListFragment host;

	public NodeListAdapter(Context context, Cursor c, NodeListFragment fragment) {

        super(context, c);
        host = fragment;
	}

	@Override
	public void bindView(View nodeView, Context context, Cursor cursor) {
		final int status = cursor.getInt(cursor.getColumnIndex(HomeMageDatabase.COL_STATUS));
		final String name = cursor.getString(cursor.getColumnIndex(HomeMageDatabase.COL_NAME));
		final String address = cursor.getString(cursor.getColumnIndex(HomeMageDatabase.COL_ADDRESS));
		
		nodeView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
                //NodeListFragment host = (NodeListFragment) v.getRootView();
				host.showNodeDetail(address, name, status);
			}			
		});
		TextView nameView = (TextView) nodeView.findViewById(R.id.nameTxt);
		ToggleButton button = (ToggleButton) nodeView.findViewById(R.id.onOffButton);
		
		
		nameView.setText(name);

		if (status > 0) {
			button.setChecked(true);
		} else {
			button.setChecked(false);
		}
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
					//MainActivity host = (MainActivity) v.getContext();
					host.toggleNode(address, ((ToggleButton) v).isChecked());
			}
		});

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View nodeView = inflater.inflate(R.layout.node_row, parent, false);
		return nodeView;
	}

}
