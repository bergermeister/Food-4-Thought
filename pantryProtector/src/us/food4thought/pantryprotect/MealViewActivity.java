package us.food4thought.pantryprotect;

import java.util.ArrayList;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

class MealViewActivity extends ListActivity {
	
	private InvDBAdapter mDatabase;
	private String mMealID;
	private String[] recipeID;
	private ArrayList<String> recipeText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// connect to the database
		mDatabase = new InvDBAdapter(this);
		mDatabase.open();
		
		// get the meal ID from the intent
		Bundle extras = getIntent().getExtras();
		mMealID = extras.getString(InvDBAdapter.KEY_ROWID);
		
		// set the display text to the meal date (ID)
		TextView dateTitle = (TextView) findViewById(R.id.meal_date);
		dateTitle.setText(mMealID);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		mDatabase.close();
	}
	
	private void fillData() {
		// fetch all the meals from the database
		Cursor cursor = mDatabase.fetchMeal(mMealID);
		startManagingCursor(cursor);

		if(cursor != null) {
			// parse out recipe IDs
			recipeID = cursor.getString(cursor.getColumnIndex(InvDBAdapter.KEY_RECIPE_LIST)).split(":");
			recipeText.clear();
			for(int i = 0; i < recipeID.length; i++) {
				Cursor iCursor = mDatabase.fetchRecipe(Integer.parseInt(recipeID[i]));
				recipeText.add(i, iCursor.getString(iCursor.getColumnIndex(InvDBAdapter.KEY_SUMMARY)));
			}

			// set up the adapter
			ArrayAdapter<String> notes = new ArrayAdapter<String>(this, R.layout.meal_list_row, (String[]) recipeText.toArray());
			setListAdapter(notes);
		}
	}
}