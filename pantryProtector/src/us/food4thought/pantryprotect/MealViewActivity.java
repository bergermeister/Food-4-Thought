package us.food4thought.pantryprotect;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
//import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class MealViewActivity extends ListActivity {
	
	private InvDBAdapter mDatabase;
	private String mMealID;
	private ArrayList<String> recipeID;
	private ArrayList<String> recipeText;
    private static final int DELETE_ID = Menu.FIRST + 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.meal_view);
		recipeID = new ArrayList<String>();
		recipeText = new ArrayList<String>();
		
		// connect to the database
		mDatabase = new InvDBAdapter(this);
		mDatabase.open();
		
		// get the meal ID from the intent
		Bundle extras = getIntent().getExtras();
		mMealID = extras.getString(InvDBAdapter.KEY_ROWID);
//		Toast.makeText(getApplicationContext(), mMealID, Toast.LENGTH_SHORT).show();
		
		// set the display text to the meal date (ID)
		TextView dateTitle = (TextView) findViewById(R.id.meal_date);
		dateTitle.setText(mMealID);
		
		// populate the list
		fillData();
		registerForContextMenu(getListView());
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		mDatabase.close();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.meal_view_menu, menu);
		return true;
	}

	// Reaction to the menu selection
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_recipe:
			pickRecipe();
			return true;
		case R.id.clear_all:
			recipeID.clear();
			recipeText.clear();
			updateData();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
//			TextView text = (TextView) info.targetView;
//			Toast.makeText(this, (String) text.getText() + " deleted.", Toast.LENGTH_SHORT).show();
			recipeID.remove(info.position);
			recipeText.remove(info.position);
			updateData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	// ListView and view (row) on which was clicked, position and
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Intent i = new Intent(this, RecipeDisplay.class);
		i.putExtra(InvDBAdapter.KEY_ROWID, (long) Integer.parseInt(recipeID.get(position)));
		// Activity returns an result if called with startActivityForResult
//		Toast.makeText(this, "ID: " + id + "\nrecipeID: " + recipeID.get(position), Toast.LENGTH_SHORT).show();

		startActivityForResult(i, 0);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
			// save changes to made to the meal plan
			saveToDatabase();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void fillData() {
//		Toast.makeText(getApplicationContext(), "Hi there!", Toast.LENGTH_SHORT).show();
		// fetch all the meals from the database
		Cursor cursor = mDatabase.fetchMeal(mMealID);
		startManagingCursor(cursor);
//		Toast.makeText(getApplicationContext(), "I got past fetchMeal!", Toast.LENGTH_SHORT).show();

		if(cursor != null && cursor.getCount() > 0) {
			String recipeString = cursor.getString(cursor.getColumnIndex(InvDBAdapter.KEY_RECIPE_LIST));
//			Toast.makeText(getApplicationContext(), "I got past getString!", Toast.LENGTH_SHORT).show();
			if(recipeString != null) {
				// parse out recipe IDs
				String[] recipeParser = recipeString.split(":");
				if(recipeParser.length > 0 && recipeParser[0] != "") {
					recipeID = new ArrayList<String>();
					for(int i = 0; i < recipeParser.length; i++) {
						recipeID.add(recipeParser[i]);				
					}
					recipeText.clear();
					for(int i = 0; i < recipeID.size(); i++) {
						Cursor iCursor = mDatabase.fetchRecipe(Integer.parseInt((String) recipeID.toArray()[i]));
						recipeText.add(i, iCursor.getString(iCursor.getColumnIndex(InvDBAdapter.KEY_SUMMARY)));
					}
//					Toast.makeText(getApplicationContext(), "I got past creating the text list!", Toast.LENGTH_SHORT).show();

					// set up the adapter
					updateData();
//					Toast.makeText(getApplicationContext(), "I got past updating the data!", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
	
	private void updateData() {
		// refresh the list view
		String[] strArray = new String[] {};
		ArrayAdapter<String> notes = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recipeText.toArray(strArray));
		setListAdapter(notes);
	}
	
	private void saveToDatabase() {
//		Toast.makeText(getApplicationContext(), "" + recipeID.size(), Toast.LENGTH_SHORT).show();
		String formatRecipeList = "";
		for(int i = 0; i < recipeID.size(); i++) {
			if(i != 0)
				formatRecipeList += ":";
			formatRecipeList += recipeID.toArray()[i];
		}
		
		if(mDatabase.dateExists(mMealID)) {
			mDatabase.updateMeal(mMealID, formatRecipeList);
		} else {
			mDatabase.createMeal(mMealID, formatRecipeList);
		}
	}
	
	private void pickRecipe() {
		Intent i = new Intent(this, RecipeList.class);
		i.putExtra("requestMode", 1);
		startActivityForResult(i, 0);
	}
	
	// Called with the result of the other activity
	// requestCode was the origin request code send to the activity
	// resultCode is the return code, 0 is everything is ok
	// intend can be use to get some data from the caller
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if(resultCode != RESULT_OK) {
			// resultCode is id of selected recipe
			recipeID.add( new Integer(resultCode).toString() );
			Cursor cursor = mDatabase.fetchRecipe(resultCode);
			recipeText.add(cursor.getString(cursor.getColumnIndex(InvDBAdapter.KEY_SUMMARY)));

			// update the list with changes
			updateData();
		}
	}
}