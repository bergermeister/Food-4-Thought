package us.food4thought.pantryprotect;

import java.util.Calendar;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class InvDBAdapter implements IDebugSwitch{

	// Database fields
	public static final String KEY_ROWID = "_id";
	public static final String KEY_CATEGORY = "category";
	public static final String KEY_SUMMARY = "summary";
	public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_EXPIRATION = "expiration";
    public static final String KEY_RECIPE_LIST = "recipe_id";
    public static final String KEY_INGREDIENTS = "item_id";
    public static final String KEY_INSTRUCTIONS = "details";
	public static final String KEY_RECIPES = "recipe";
	private static final String DATABASE_TABLE = "item";
	private static final String LOCATION_TABLE = "location";
	private static final String GLIST_TABLE = "glist";
	private static final String MEALS_TABLE = "mealplans";
	private static final String RECIPE_TABLE = "recipes";
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
	
	private ContentValues createContentValues2(String category, String summary,
			String description, String expiration, String recipe) {
		ContentValues values = new ContentValues();
		values.put(KEY_CATEGORY, category);
		values.put(KEY_SUMMARY, summary);
		values.put(KEY_DESCRIPTION, description);
		values.put(KEY_EXPIRATION, expiration);
		values.put(KEY_RECIPES, recipe);
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
	
	public String fetchAll(int day){
		int count = 0;
		String data = "00";
		String [] s = new String[] {KEY_DESCRIPTION, KEY_EXPIRATION, KEY_SUMMARY, KEY_CATEGORY};
		Cursor cursor = database.query(DATABASE_TABLE, s, null, null, null, null, null);
		cursor.moveToFirst();
		final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int d = c.get(Calendar.DAY_OF_MONTH);
		while(!cursor.isAfterLast()){
			String exp = cursor.getString(cursor.getColumnIndex(s[1]));
			Integer month2;
			Integer d2;
			Integer year2;
			int i = exp.indexOf('-');
			month2 = Integer.parseInt(exp.substring(0, i));
			int i2 = exp.indexOf('-', 3);
			d2 = Integer.parseInt(exp.substring(i + 1, i2));
			year2 = Integer.parseInt(exp.substring(i2 + 1, exp.length() - 1));
			//System.out.println(exp);
			if (debug) System.out.println(month2 + "-" + d2 + "-" + year2 + "\t" + month + "-" + d + "-" + year);
			if (year2 == year){
				if (month2 == month){
					if (d2 - d <= day){
						count++;
						if (d2 - d <= 0){
							data = "11";
						}
					}
				}
				else if(month2 < month){
					count++;
					data = "11";
				}
				else if(month2 > month){
					if(d - d2 > 26){
						count++;
					}
				}
			}
			else if(year2 > year){
				if(month2 == 1 && month == 12){
					if(d - d2 > 26){
						count++;
					}
				}
			}
			else if(year2 < year){
				count++;
				data = "11";
			}
			cursor.moveToNext();
		}
		data = data + count;
		return data;
	}
	
	// Method has pointer errors, requires fixing
	/*
	public ArrayList <Item> fetchAll(){
		ArrayList <Item> l = new ArrayList<Item>();
		String [] s = new String[] {KEY_DESCRIPTION, KEY_EXPIRATION, KEY_SUMMARY, KEY_CATEGORY};
		Cursor cursor = database.query(DATABASE_TABLE, s, null, null, null, null, null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			final String exp = cursor.getString(1);
			final String name = cursor.getString(2);
			l.add(new Item(name, exp, " ", " ", " "));
			for (int i = 0; i < l.size(); i++){
				System.out.println(l.get(i).getName());
			}
			
			cursor.moveToNext();
		}
		
		return l;
	}
	*/
	
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
	
	/**
	 * Meal Plans
	 * - ROWID = date based identification string
	 * - RECIPE_LIST = delimited string of recipe identification numbers
	 **/

	// test if a meal plan date already exists in the database
	public boolean dateExists(String dateID) throws SQLException {
		Cursor mCursor = database.query(MEALS_TABLE, new String[] { KEY_ROWID, KEY_RECIPE_LIST }, KEY_ROWID + "='" + dateID + "'", null, null, null, null);
		if(mCursor == null || mCursor.getCount() < 1)
			return false;
		else
			return true;
	}
	
	// creates a new meal plan and return the rowID if successful
	public long createMeal(String dateID, String recipeList) throws SQLException {
		ContentValues initialValues = createMealValues(dateID, recipeList);
		
		return database.insert(MEALS_TABLE, null, initialValues );
	}
	
	// updates an existing meal plan
	public boolean updateMeal(String dateID, String recipeList) throws SQLException {
		ContentValues updateValues = createMealValues(dateID, recipeList);

		return database.update(MEALS_TABLE, updateValues, KEY_ROWID + "='"
				+ dateID + "'", null) > 0;
	}
	
	// deletes an existing meal plan
	public boolean deleteMeal(String dateID) {
		return database.delete(MEALS_TABLE, KEY_ROWID + "='" + dateID + "'", null) > 0;
	}

	// return a cursor of all meals in the database
	public Cursor fetchAllMeals() throws SQLException {
		Cursor mCursor = database.query(MEALS_TABLE, new String[] { KEY_ROWID, KEY_RECIPE_LIST }, null, null, null, null, null);
		if(mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// return a cursor of the specified meal
	public Cursor fetchMeal(String mealID) {
		Cursor mCursor = database.query(MEALS_TABLE, new String[] { KEY_ROWID, KEY_RECIPE_LIST }, KEY_ROWID + "='" + mealID + "'", null, null, null, null);
		if(mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	private ContentValues createMealValues(String dateID, String recipeList) {
		ContentValues values = new ContentValues();
		values.put(KEY_ROWID, dateID);
		values.put(KEY_RECIPE_LIST, recipeList);
		return values;
	}
	
	/**
	 * methods for recipes
	 */
	
	/*public Cursor fetchRecipe(long rowId) throws SQLException {
		Cursor mCursor = database.query(RECIPE_TABLE, new String[] { KEY_ROWID, KEY_SUMMARY, KEY_INGREDIENTS, KEY_INSTRUCTIONS }, KEY_ROWID + "=" + rowId, null, null,
				null, null);
		if(mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
			
	}*/
	public long createRecipe(String summary, String description) {
		ContentValues initialValues = createLocationContentValues(summary,
				description);

		return database.insert(RECIPE_TABLE, null, initialValues);
	}

	

	public boolean updateRecipe(long rowId, String summary,
			String description) {
		ContentValues updateValues = createLocationContentValues(summary,
				description);

		return database.update(RECIPE_TABLE, updateValues, KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	

	public boolean deleteRecipe(long rowId) {
		return database.delete(RECIPE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	

	public Cursor fetchAllRecipes() {
		return database.query(RECIPE_TABLE, new String[] { KEY_ROWID,
				KEY_SUMMARY, KEY_DESCRIPTION }, null, null, null,
				null, null);
	}


	public Cursor fetchRecipe(long rowId) throws SQLException {
		Cursor mCursor = database.query(true, RECIPE_TABLE, new String[] {
				KEY_ROWID, KEY_SUMMARY, KEY_DESCRIPTION },
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
}