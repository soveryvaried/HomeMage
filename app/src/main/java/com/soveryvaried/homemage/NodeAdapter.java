package com.soveryvaried.homemage;

import com.soveryvaried.homemage.db.Node;
import com.soveryvaried.homemage.isy.ISYService;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class NodeAdapter extends ArrayAdapter<Node> {
	private final Context context;
	private final Node[] values;
	
	public NodeAdapter (Context context, Node[] values) {
		super(context, R.layout.node_row, values);
		this.context = context;
		this.values = values;
	}
	
	public View getView (int position, View convertView, ViewGroup parent) {
		final Node node = values[position];
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View nodeView = inflater.inflate(R.layout.node_row, parent, false);
		nodeView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast toast = Toast.makeText(v.getContext(), "Row clicked", Toast.LENGTH_LONG);
				toast.show();
				
				Intent intent = new Intent(context, NodeDetailActivity.class);
				intent.putExtra(NodeDetailActivity.INTENT_NODE_ADDRESS, node.getAddress());
				intent.putExtra(NodeDetailActivity.INTENT_NODE_NAME, node.getName());
				intent.putExtra(NodeDetailActivity.INTENT_NODE_STATUS, node.getStatus());
				v.getContext().startActivity(intent);
			}			
		});
		TextView nameView = (TextView) nodeView.findViewById(R.id.nameTxt);
		ToggleButton button = (ToggleButton) nodeView.findViewById(R.id.onOffButton);
		
		
		nameView.setText(node.getName());
		if (node.getStatus() > 0) {
			button.setChecked(true);
		} else {
			button.setChecked(false);
		}
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				boolean on = ((ToggleButton) v).isChecked();
				
				
				if (on) {
					Toast toast = Toast.makeText(v.getContext(), "Button turned on", Toast.LENGTH_SHORT);
					toast.show();
					Intent intent = new Intent(v.getContext(), ISYService.class);
					intent.putExtra(ISYService.INTENT_EXTRA_METHOD, ISYService.METHOD_TURN_NODE_ON);
					intent.putExtra(ISYService.INTENT_EXTRA_NODE_ADDRESS, node.getAddress());
			    	v.getContext().startService(intent);
					
				} else {
					Toast toast = Toast.makeText(v.getContext(), "Button turned off", Toast.LENGTH_SHORT);
					toast.show();
					Intent intent = new Intent(v.getContext(), ISYService.class);
					intent.putExtra(ISYService.INTENT_EXTRA_METHOD, ISYService.METHOD_TURN_NODE_OFF);
					intent.putExtra(ISYService.INTENT_EXTRA_NODE_ADDRESS, node.getAddress());
			    	v.getContext().startService(intent);
				}
				
				
			}
		});
		
		return nodeView;
	}
}
