package com.soveryvaried.homemage.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class HomeMageDatabase extends SQLiteOpenHelper {
	private static final String DEBUG_TAG = "HomeMageDatabase";
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "homemage_data";
	
	public static final String TABLE_NODES = "nodes";
	public static final String ID = "_id";
	public static final String COL_ADDRESS = "address";
	public static final String COL_NAME = "name";
	public static final String COL_ENABLED = "enabled";
	public static final String COL_STATUS = "status";
	public static final String COL_TYPE = "type";
    public static final String COL_GROUP_ID = "group_id";
	private static final String CREATE_TABLE_NODES = "CREATE TABLE " + TABLE_NODES + " (" 
			+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COL_ADDRESS + " TEXT UNIQUE NOT NULL, " 
			+ COL_NAME + " TEXT NOT NULL, " 
			+ COL_ENABLED + " INTEGER, "
			+ COL_STATUS + " INTEGER, " 
			+ COL_TYPE + " TEXT, "
            + COL_GROUP_ID + " INTEGER);";
    public static final String TABLE_GROUPS = "groups";
    public static final String COL_SORT_ORDER = "sort_order";
    private static final String CREATE_TABLE_GROUPS = "CREATE TABLE " + TABLE_GROUPS + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_NAME + " TEXT NOT NULL, "
            + COL_SORT_ORDER + " INTEGER);";

    //TO BE REMOVED AFTER FULL IMPLEMENT GROUPS
    private static final String POPULATE_GROUPS = "INSERT INTO " + TABLE_GROUPS + " ("
            + COL_NAME + ", " + COL_SORT_ORDER + ")"
            + "VALUES ('FAMILY', 1);"
            + "INSERT INTO " + TABLE_GROUPS + " ("
            + COL_NAME + ", " + COL_SORT_ORDER + ")"
            + "VALUES ('DINING', 2);"
            + "INSERT INTO " + TABLE_GROUPS + " ("
            + COL_NAME + ", " + COL_SORT_ORDER + ")"
            + "VALUES ('KITCHEN', 3);"
            + "INSERT INTO " + TABLE_GROUPS + " ("
            + COL_NAME + ", " + COL_SORT_ORDER + ")"
            + "VALUES ('MASTER', 4);"
            + "INSERT INTO " + TABLE_GROUPS + " ("
            + COL_NAME + ", " + COL_SORT_ORDER + ")"
            + "VALUES ('OTHER', 5);";
	
	public HomeMageDatabase(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.v(DEBUG_TAG, "create sql");
		db.execSQL(CREATE_TABLE_NODES);
        db.execSQL(CREATE_TABLE_GROUPS);
        //TO BE REMOVED AFTER FULLY IMPLEMENT GROUPS
        db.execSQL(POPULATE_GROUPS);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DEBUG_TAG, "Upgrading database.  Existing table will be dropped and recreated. ["
				+ oldVersion + "] -> [" + newVersion +"]");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NODES);
		onCreate(db);

	}

}
