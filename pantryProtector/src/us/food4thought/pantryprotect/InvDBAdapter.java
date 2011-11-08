package us.food4thought.pantryprotect;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class InvDBAdapter {

	// Database fields
	public static final String KEY_ROWID = "_id";
	public static final String KEY_CATEGORY = "category";
	public static final String KEY_SUMMARY = "summary";
	public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_EXPIRATION = "expiration";
	private static final String DATABASE_TABLE = "item";
	private static final String LOCATION_TABLE = "location";
	private static final String GLIST_TABLE = "glist";
	private Context context;
	private SQLiteDatabase database;
	private InventoryDatabaseHelper dbHelper;

	public InvDBAdapter(Context context) {
		this.context = context;
	}

	public InvDBAdapter open() throws SQLException {
		dbHelper = new InventoryDatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	
	/*
	 * FOOD ITEMS
	 * 	-category
	 * 	-summary (name)
	 * 	-description
	 * 	-expiration (date)
	 */
	
/*Create a new food item If the food item is successfully created return the new * rowId for that note, otherwise return a -1 to indicate failure. */

	public long createItem(String category, String summary, String description, String expiration) {
		ContentValues initialValues = createContentValues(category, summary,
				description, expiration);

		return database.insert(DATABASE_TABLE, null, initialValues);
	}

	
/** * Update the food item */

	public boolean updateItem(long rowId, String category, String summary,
			String description, String expiration) {
		ContentValues updateValues = createContentValues(category, summary,
				description, expiration);

		return database.update(DATABASE_TABLE, updateValues, KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	
/*Deletes food item */

	public boolean deleteItem(long rowId) {
		return database.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	
/*Return a Cursor over the list of all items in the database * * @return Cursor over all notes */

	public Cursor fetchAllItems(String order_by, String filter_by) {
		if( order_by.equalsIgnoreCase("Name") ) {
			String WHERE = null;
			if( filter_by != null ) {
				WHERE = KEY_SUMMARY + " like '" + filter_by + "%'";
			}
			return database.query(DATABASE_TABLE, new String[] { KEY_ROWID,
					KEY_CATEGORY, KEY_SUMMARY, KEY_DESCRIPTION, KEY_EXPIRATION }, WHERE, null, null,
					null, KEY_SUMMARY + " ASC");
		} else if( order_by.equalsIgnoreCase("Location") ) {
			String WHERE = null;
			if( filter_by != null ) {
				WHERE = KEY_CATEGORY + "='" + filter_by + "'";
			}
			return database.query(DATABASE_TABLE, new String[] { KEY_ROWID,
					KEY_CATEGORY, KEY_SUMMARY, KEY_DESCRIPTION, KEY_EXPIRATION }, WHERE, null, null,
					null, KEY_CATEGORY + " ASC");
		} else if( order_by.equalsIgnoreCase("Expiration Date") ) {
			return database.query(DATABASE_TABLE, new String[] { KEY_ROWID,
					KEY_CATEGORY, KEY_SUMMARY, KEY_DESCRIPTION, KEY_EXPIRATION }, null, null, null,
					null, KEY_EXPIRATION + " ASC");
		} else {
			return database.query(DATABASE_TABLE, new String[] { KEY_ROWID,
					KEY_CATEGORY, KEY_SUMMARY, KEY_DESCRIPTION, KEY_EXPIRATION }, null, null, null,
					null, null);
		}
	}
	public long createGList(String category, String summary, String description, String expiration) {
		ContentValues initialValues = createContentValues(category, summary,
				description, expiration);

		return database.insert(GLIST_TABLE, null, initialValues);
	}
	/** * Update the food item */

	public boolean updateGrocery(long rowId, String category, String summary,
			String description, String expiration) {
		ContentValues updateValues = createContentValues(category, summary,
				description, expiration);

		return database.update(GLIST_TABLE, updateValues, KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	
/*Deletes food item */

	public boolean deleteGrocery(long rowId) {
		return database.delete(GLIST_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}
/*Return a Cursor positioned at the defined item */

	public Cursor fetchItem(long rowId) throws SQLException {
		Cursor mCursor = database.query(true, DATABASE_TABLE, new String[] {
				KEY_ROWID, KEY_CATEGORY, KEY_SUMMARY, KEY_DESCRIPTION, KEY_EXPIRATION },
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	private ContentValues createContentValues(String category, String summary,
			String description, String expiration) {
		ContentValues values = new ContentValues();
		values.put(KEY_CATEGORY, category);
		values.put(KEY_SUMMARY, summary);
		values.put(KEY_DESCRIPTION, description);
		values.put(KEY_EXPIRATION, expiration);
		return values;
	}
	
	
	/*
	 * STORAGE LOCATIONS
	 * 	-summary (name)
	 * 	-description
	 */
	
/*Create a new location item If the location item is successfully created return the new * rowId for that node, otherwise return a -1 to indicate failure. */

	public long createLocation(String summary, String description) {
		ContentValues initialValues = createLocationContentValues(summary,
				description);

		return database.insert(LOCATION_TABLE, null, initialValues);
	}

	
/** * Update the location */

	public boolean updateLocation(long rowId, String summary,
			String description) {
		ContentValues updateValues = createLocationContentValues(summary,
				description);

		return database.update(LOCATION_TABLE, updateValues, KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	
/*Deletes location */

	public boolean deleteLocation(long rowId) {
		return database.delete(LOCATION_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	
/*Return a Cursor over the list of all locations in the database * * @return Cursor over all notes */

	public Cursor fetchAllLocations() {
		return database.query(LOCATION_TABLE, new String[] { KEY_ROWID,
				KEY_SUMMARY, KEY_DESCRIPTION }, null, null, null,
				null, null);
	}

	
/*Return a Cursor positioned at the defined location */

	public Cursor fetchLocation(long rowId) throws SQLException {
		Cursor mCursor = database.query(true, LOCATION_TABLE, new String[] {
				KEY_ROWID, KEY_SUMMARY, KEY_DESCRIPTION },
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	private ContentValues createLocationContentValues(String summary,
			String description) {
		ContentValues values = new ContentValues();
		values.put(KEY_SUMMARY, summary);
		values.put(KEY_DESCRIPTION, description);
		return values;
	}
	
	public ArrayList <Item> fetchAll(){
		ArrayList <Item> l = new ArrayList<Item>();
		String [] s = new String[] {KEY_DESCRIPTION, KEY_EXPIRATION, KEY_SUMMARY, KEY_CATEGORY};
		Cursor cursor = database.query(DATABASE_TABLE, s, null, null, null, null, null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			String name = cursor.getString(cursor.getColumnIndex(InvDBAdapter.KEY_SUMMARY));
			String exp = cursor.getString(cursor.getColumnIndex(InvDBAdapter.KEY_EXPIRATION));
			l.add(new Item(name, exp, " ", " ", " "));
			cursor.moveToNext();
		}
		return l;
	}
	
	/*Return a Cursor over the list of all items in the database * * @return Cursor over all notes */

	public Cursor fetchAllGroceries(String order_by, String filter_by) {
		if( order_by.equalsIgnoreCase("Name") ) {
			String WHERE = null;
			if( filter_by != null ) {
				WHERE = KEY_SUMMARY + " like '" + filter_by + "%'";
			}
			return database.query(GLIST_TABLE, new String[] { KEY_ROWID,
					KEY_CATEGORY, KEY_SUMMARY, KEY_DESCRIPTION, KEY_EXPIRATION }, WHERE, null, null,
					null, KEY_SUMMARY + " ASC");
		} else if( order_by.equalsIgnoreCase("Location") ) {
			String WHERE = null;
			if( filter_by != null ) {
				WHERE = KEY_CATEGORY + "='" + filter_by + "'";
			}
			return database.query(GLIST_TABLE, new String[] { KEY_ROWID,
					KEY_CATEGORY, KEY_SUMMARY, KEY_DESCRIPTION, KEY_EXPIRATION }, WHERE, null, null,
					null, KEY_CATEGORY + " ASC");
		} else if( order_by.equalsIgnoreCase("Expiration Date") ) {
			return database.query(GLIST_TABLE, new String[] { KEY_ROWID,
					KEY_CATEGORY, KEY_SUMMARY, KEY_DESCRIPTION, KEY_EXPIRATION }, null, null, null,
					null, KEY_EXPIRATION + " ASC");
		} else {
			return database.query(GLIST_TABLE, new String[] { KEY_ROWID,
					KEY_CATEGORY, KEY_SUMMARY, KEY_DESCRIPTION, KEY_EXPIRATION }, null, null, null,
					null, null);
		}
	}

	
/*Return a Cursor positioned at the defined item */

	public Cursor fetchGrocery(long rowId) throws SQLException {
		Cursor mCursor = database.query(true, GLIST_TABLE, new String[] {
				KEY_ROWID, KEY_CATEGORY, KEY_SUMMARY, KEY_DESCRIPTION, KEY_EXPIRATION },
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
}