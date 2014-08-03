package com.soveryvaried.homemage.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class HomeMageProvider extends ContentProvider {
	private HomeMageDatabase mDB;
	private static final String DEBUG_TAG = "HomeMageProvider";
			
	private static final String AUTHORITY = "com.soveryvaried.homemage.db.HomeMageProvider";
	public static final int NODES = 100;
	public static final int NODE_ID = 110;
	public static final int NODE_ADDRESS = 120;
	private static final String NODES_BASE_PATH = "nodes";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + NODES_BASE_PATH);
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/mt-node";         
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/mt-node";
	
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase sqlDB = mDB.getWritableDatabase();
		long newID;
		int uriType = sURIMatcher.match(uri);
		if (uriType != NODES) {
			throw new IllegalArgumentException("Invalid URI for insert");
		}
		
		//Query if address already exists in table
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		String[] projection = {HomeMageDatabase.ID};
		queryBuilder.setTables(HomeMageDatabase.TABLE_NODES);
		queryBuilder.appendWhere(HomeMageDatabase.COL_ADDRESS + "='" + values.getAsString(HomeMageDatabase.COL_ADDRESS) + "'");
		Cursor cursor = queryBuilder.query(mDB.getReadableDatabase(), projection, null, null, null, null, null);
		
		//Do update or insert based on number of results returned
		if (cursor.getCount() > 0) {	
			cursor.moveToFirst();
			newID = cursor.getInt(cursor.getColumnIndex(HomeMageDatabase.ID));
			String selection = HomeMageDatabase.COL_ADDRESS + "=?";
			String[] selectionArgs = {values.getAsString(HomeMageDatabase.COL_ADDRESS)};

			//Try to do an update first in case
			int affected = sqlDB.update(HomeMageDatabase.TABLE_NODES, values, selection, selectionArgs);
			Log.v(DEBUG_TAG, "updated affected " + affected);
		} else {
			//Now call insert.  If row is inserted, will get new ID.  If row exists, will get existing ID.
			newID = sqlDB.insert(HomeMageDatabase.TABLE_NODES, null, values);
			Log.v(DEBUG_TAG, "insert id is " + newID);
			if (newID <= 0) {
				Log.v(DEBUG_TAG, "insert is throwing exception");
				throw new SQLException("Failed to insert row into " + uri);
			}
		}
		
		Uri newUri = ContentUris.withAppendedId(uri, newID);
		getContext().getContentResolver().notifyChange(uri, null);
		return newUri;
	}

	@Override
	public boolean onCreate() {
		mDB = new HomeMageDatabase(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(HomeMageDatabase.TABLE_NODES);
		if (selection != null && selectionArgs != null) {
            String selectionString = "";
            for (int i =0; i<selectionArgs.length; i++) {
                selectionString = selectionString + selectionArgs[i] + ",";
            }
			Log.v(DEBUG_TAG, "query with '" + selection + "'" + selectionString);
		}
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case NODE_ID:
			Log.v(DEBUG_TAG, "URI type is NODE_ID");
			queryBuilder.appendWhere(HomeMageDatabase.ID + "=" + uri.getLastPathSegment());
			break;
		case NODES:
			//no filter
			Log.v(DEBUG_TAG, "URI type is NODES");
			break;
		default: throw new IllegalArgumentException("Unknown URI");
		}
		
		Cursor cursor = queryBuilder.query(mDB.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
		SQLiteDatabase sqlDB = mDB.getWritableDatabase();
		int rowUpdated = 0;
		Log.v(DEBUG_TAG, uri.toString());
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case NODES:
			rowUpdated = sqlDB.update(HomeMageDatabase.TABLE_NODES, contentValues, selection, selectionArgs);
			break;
		default: throw new IllegalArgumentException("Unknown URI");
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return rowUpdated;
	}

	private static final UriMatcher sURIMatcher = new UriMatcher(
	        UriMatcher.NO_MATCH);
	static {
	    sURIMatcher.addURI(AUTHORITY, NODES_BASE_PATH, NODES);
	    sURIMatcher.addURI(AUTHORITY, NODES_BASE_PATH + "/#", NODE_ID);
	    sURIMatcher.addURI(AUTHORITY, NODES_BASE_PATH + "/update/#", NODE_ADDRESS);
	}
	
}
