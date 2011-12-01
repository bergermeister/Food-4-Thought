package us.food4thought.pantryprotect;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class InventoryDatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "applicationdata";

	private static final int DATABASE_VERSION = 1;

	// Create database for items
	private static final String DATABASE_CREATE = "create table item (_id integer primary key autoincrement, "
			+ "category text not null, summary text not null, description text not null, expiration text not null);";
	
	// Create database for locations
	private static final String LOCATION_CREATE = "create table location (_id integer primary key autoincrement, "
			+ "summary text not null, description text not null);";
	
	// Create database for grocery list items
	private static final String GLIST_CREATE = "create table glist (_id integer primary key autoincrement, "
			+ "category text not null, summary text not null, description text not null, expiration text not null);";
	
	// Create database for meal plan items
	private static final String MEALS_CREATE = "create table mealplans (_id text primary key, "
			+ "recipe_id text);";

	// Constructor
	public InventoryDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Called when first created, creates the databases if they do not exist
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(LOCATION_CREATE);
		database.execSQL(DATABASE_CREATE);
		database.execSQL(GLIST_CREATE);
		database.execSQL(MEALS_CREATE);
	}

	// Method is called during an upgrade of the database, e.g. if you increase
	// the database version
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(InventoryDatabaseHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS item");
		database.execSQL("DROP TABLE IF EXISTS location");
		database.execSQL("DROP TABLE IF EXISTS glist");
		database.execSQL("DROP TABLE IF EXISTS mealplans");
		onCreate(database);
	}
}