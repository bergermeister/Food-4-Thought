package com.example.helloandroid;

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
	private static final String DATABASE_TABLE = "item";
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

	
/*Create a new food item If the food item is successfully created return the new * rowId for that note, otherwise return a -1 to indicate failure. */

	public long createItem(String category, String summary, String description) {
		ContentValues initialValues = createContentValues(category, summary,
				description);

		return database.insert(DATABASE_TABLE, null, initialValues);
	}

	
/** * Update the food item */

	public boolean updateItem(long rowId, String category, String summary,
			String description) {
		ContentValues updateValues = createContentValues(category, summary,
				description);

		return database.update(DATABASE_TABLE, updateValues, KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	
/*Deletes food item */

	public boolean deleteItem(long rowId) {
		return database.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	
/*Return a Cursor over the list of all items in the database * * @return Cursor over all notes */

	public Cursor fetchAllItems() {
		return database.query(DATABASE_TABLE, new String[] { KEY_ROWID,
				KEY_CATEGORY, KEY_SUMMARY, KEY_DESCRIPTION }, null, null, null,
				null, null);
	}

	
/*Return a Cursor positioned at the defined item */

	public Cursor fetchItem(long rowId) throws SQLException {
		Cursor mCursor = database.query(true, DATABASE_TABLE, new String[] {
				KEY_ROWID, KEY_CATEGORY, KEY_SUMMARY, KEY_DESCRIPTION },
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	private ContentValues createContentValues(String category, String summary,
			String description) {
		ContentValues values = new ContentValues();
		values.put(KEY_CATEGORY, category);
		values.put(KEY_SUMMARY, summary);
		values.put(KEY_DESCRIPTION, description);
		return values;
	}
}