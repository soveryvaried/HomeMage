package com.soveryvaried.homemage.isy;

import android.app.Activity;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;

import com.soveryvaried.homemage.db.HomeMageDatabase;
import com.soveryvaried.homemage.db.HomeMageProvider;
import com.soveryvaried.homemage.db.Node;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class ISYService extends IntentService {
	private static final String DEBUG_TAG = "ISYService";
	public static final String INTENT_EXTRA_METHOD = "method";
	public static final String INTENT_EXTRA_MESSENGER = "messenger";
	public static final String INTENT_EXTRA_NODE_ADDRESS = "nodeAddress";
	public static final String INTENT_EXTRA_BRIGHT_DIM_LEVEL = "brtDimLevel";
	public static final int METHOD_RETRIEVE_NODES = 1;
	public static final int METHOD_TURN_NODE_ON = 2;
	public static final int METHOD_TURN_NODE_OFF = 3;
	public static final int METHOD_TURN_NODE_FAST_ON = 4;
	public static final int METHOD_TURN_NODE_FAST_OFF = 5;
	public static final int METHOD_BRIGHT_DIM_NODE = 6;
	public static final int METHOD_UPDATE_NODE = 7;

	public ISYService() {
		super("ISYService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.v(DEBUG_TAG, "started onHandleIntent");
		int method = intent.getIntExtra(INTENT_EXTRA_METHOD, 0);
		Bundle extras = intent.getExtras();

		switch (method) {
		case METHOD_RETRIEVE_NODES:
			Log.v(DEBUG_TAG, "case retrieve nodes");
			getNodeList(extras);
			break;
		case METHOD_TURN_NODE_ON:
			Log.v(DEBUG_TAG, "case turn on");
			toggleNode(intent.getStringExtra(INTENT_EXTRA_NODE_ADDRESS), "DON");
			break;
		case METHOD_TURN_NODE_OFF:
			Log.v(DEBUG_TAG, "case turn off");
			toggleNode(intent.getStringExtra(INTENT_EXTRA_NODE_ADDRESS), "DOF");
			break;
		case METHOD_TURN_NODE_FAST_ON:
			Log.v(DEBUG_TAG, "case fast on");
			toggleNode(intent.getStringExtra(INTENT_EXTRA_NODE_ADDRESS), "DFON");
			break;
		case METHOD_TURN_NODE_FAST_OFF:
			Log.v(DEBUG_TAG, "case fast off");
			toggleNode(intent.getStringExtra(INTENT_EXTRA_NODE_ADDRESS), "DFOF");
			break;
		case METHOD_BRIGHT_DIM_NODE:
			Log.v(DEBUG_TAG, "case bright/dim");
			brtDimNode(intent.getStringExtra(INTENT_EXTRA_NODE_ADDRESS),
					intent.getIntExtra(INTENT_EXTRA_BRIGHT_DIM_LEVEL, 0));
			break;
		case METHOD_UPDATE_NODE:
			Log.v(DEBUG_TAG, "case update");
			updateNode(intent.getStringExtra(INTENT_EXTRA_NODE_ADDRESS));
			break;
		default:
			Log.v("ISYService", "onHandleIntent called with unknown method");
			break;
		}
	}

	private HttpURLConnection getConnection(String urlCommand) throws Exception {
		String myURL = "http://soveryvaried.dnsalias.org/" + urlCommand;
		URL url = new URL(myURL);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		String basicAuth = "Basic " + new String(Base64.encode("admin:temp2".getBytes(), Base64.NO_WRAP));
		conn.setRequestProperty("Authorization", basicAuth);
		conn.connect();
		return conn;
	}

	private void getNodeList(Bundle extras) {
		Node[] nodeArray = null;
        ArrayList<Node> nodes;
        ArrayList<Node> filteredNodes = new ArrayList<Node>();
        XMLParser xmlParser;
		try {
			// Make HTTP call to get XML response
			HttpURLConnection conn = getConnection("/rest/nodes");
			int response = conn.getResponseCode();
			Log.v("getNodeList", "The response is: " + response);
			String responseMsg = conn.getResponseMessage();
			Log.v("getNodeList", "The response message is: " + responseMsg);
			InputStream is = conn.getInputStream();

			// Parse XML response into array of Node objects
			xmlParser = new XMLParser();
			nodes = xmlParser.parseNodesXML(is);
			Log.v("getNodeList", "size=" + nodes.size());
			is.close();

			// Insert nodes into database
			Node node = null;
			ContentValues nodeData = null;
            String mainTypeString;
            int mainType;
			for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
				node = i.next();

                mainTypeString = node.getType().substring(0, node.getType().indexOf("."));
                if (!mainTypeString.trim().isEmpty()) {
                    mainType = Integer.parseInt(mainTypeString);
                    if (mainType > 0 && mainType < 3) {
                        nodeData = new ContentValues();
                        nodeData.put(HomeMageDatabase.COL_ADDRESS, node.getAddress());
                        nodeData.put(HomeMageDatabase.COL_ENABLED, node.isEnabled());
                        nodeData.put(HomeMageDatabase.COL_NAME, node.getName());
                        nodeData.put(HomeMageDatabase.COL_STATUS, node.getStatus());
                        nodeData.put(HomeMageDatabase.COL_TYPE, node.getType());
                        //TO BE REMOVED WHEN GROUPS FULLY IMPLEMENTED
                        if (node.getName().indexOf("Family") >= 0) {
                            nodeData.put(HomeMageDatabase.COL_GROUP_ID, 1);
                        } else if (node.getName().indexOf("Dining") >= 0) {
                            nodeData.put(HomeMageDatabase.COL_GROUP_ID, 2);
                        } else if (node.getName().indexOf("Kitchen") >= 0) {
                            nodeData.put(HomeMageDatabase.COL_GROUP_ID, 3);
                        } else if (node.getName().indexOf("Master") >= 0) {
                            nodeData.put(HomeMageDatabase.COL_GROUP_ID, 4);
                        } else {
                            nodeData.put(HomeMageDatabase.COL_GROUP_ID, 5);
                        }

                        getContentResolver().insert(HomeMageProvider.CONTENT_URI, nodeData);
                        filteredNodes.add(node);
                    }
                }
			}

            nodeArray = filteredNodes.toArray(new Node[nodes.size()]);

			if (extras != null) {
				Messenger messenger = (Messenger) extras.get(INTENT_EXTRA_MESSENGER);
				if (messenger != null) {
					Message msg = Message.obtain();
					msg.arg1 = Activity.RESULT_OK;
					msg.obj = nodeArray;
					try {
						messenger.send(msg);
					} catch (RemoteException e) {
						Log.w(getClass().getName(), "Exception sending message", e);
					}
				}
			}
		} catch (Exception e) {
			Log.v("getNodeList", "Exception!");
			e.printStackTrace();
		}
	}

	private void updateNode(String nodeAddr) {
		Log.v("ISYClient", "updateNode " + nodeAddr);

		// Get latest node data from ISY
		try {
			HttpURLConnection conn = getConnection("/rest/nodes/" + nodeAddr.replace(" ", "%20"));
			int response = conn.getResponseCode();
			Log.v("toggleNode", "The response is: " + response);
			String responseMsg = conn.getResponseMessage();
			Log.v("toggleNode", "The response message is: " + responseMsg);
			InputStream is = conn.getInputStream();
			
			// Parse XML response into node object
			XMLParser xmlParser = new XMLParser();
			Node node = xmlParser.parseNodeDetailXML(is);			
			is.close();
			Log.v("updateNode", "status is " + node.getStatus());
			ContentValues nodeData = new ContentValues();
			nodeData.put(HomeMageDatabase.COL_ADDRESS, node.getAddress());
			nodeData.put(HomeMageDatabase.COL_ENABLED, node.isEnabled());
			nodeData.put(HomeMageDatabase.COL_NAME, node.getName());
			nodeData.put(HomeMageDatabase.COL_STATUS, node.getStatus());
			nodeData.put(HomeMageDatabase.COL_TYPE, node.getType());
			
			String selection = HomeMageDatabase.COL_ADDRESS + " = ?";
			String[] selectionArgs = {node.getAddress()};
			
			getContentResolver().update(HomeMageProvider.CONTENT_URI, nodeData, selection, selectionArgs);
			
		} catch (Exception e) {
			Log.v("toggleNode", "Exception!");
			e.printStackTrace();
		}
	}

	private void toggleNode(String nodeAddr, String cmd) {
		Log.v("ISYClient", "toggleNode " + nodeAddr);
		nodeAddr = nodeAddr.replace(" ", "%20");
		try {
			HttpURLConnection conn = getConnection("/rest/nodes/" + nodeAddr + "/cmd/" + cmd);
			int response = conn.getResponseCode();
			Log.v("toggleNode", "The response is: " + response);
			String responseMsg = conn.getResponseMessage();
			Log.v("toggleNode", "The response message is: " + responseMsg);
			Thread.sleep(300);
			updateNode(nodeAddr);
		} catch (Exception e) {
			Log.v("toggleNode", "Exception!");
			e.printStackTrace();
		}
	}

	// http://192.168.1.92/rest/nodes/19%2049%2047%201/cmd/DON/128
	private void brtDimNode(String nodeAddr, int level) {
		Log.v("ISYClient", "brtDim " + nodeAddr + "  to level " + level);
		nodeAddr = nodeAddr.replace(" ", "%20");
		try {
			HttpURLConnection conn = getConnection("/rest/nodes/" + nodeAddr + "/cmd/DON/" + level);
			int response = conn.getResponseCode();
			Log.v("brtDimNode", "The response is: " + response);
			String responseMsg = conn.getResponseMessage();
			Log.v("brtDimNode", "The response message is: " + responseMsg);
			Thread.sleep(300);
			updateNode(nodeAddr);
		} catch (Exception e) {
			Log.v("brtDimNode", "Exception!");
			e.printStackTrace();
		}
	}

}
