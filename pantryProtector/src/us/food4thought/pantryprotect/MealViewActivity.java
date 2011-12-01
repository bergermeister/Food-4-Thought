package us.food4thought.pantryprotect;

import java.util.ArrayList;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MealViewActivity extends ListActivity {
	
	private InvDBAdapter mDatabase;
	private String mMealID;
	private ArrayList<String> recipeID;
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
		
		// populate the list
		fillData();
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
			String recipeString = cursor.getString(cursor.getColumnIndex(InvDBAdapter.KEY_RECIPE_LIST));
			if(recipeString != null) {
				// parse out recipe IDs
				String[] recipeParser = recipeString.split(":");
				recipeID = new ArrayList<String>();
				for(int i = 0; i < recipeParser.length; i++) {
					recipeID.add(recipeParser[i]);				
				}
				recipeText.clear();
				for(int i = 0; i < recipeID.size(); i++) {
					Cursor iCursor = mDatabase.fetchRecipe(Integer.parseInt((String) recipeID.toArray()[i]));
					recipeText.add(i, iCursor.getString(iCursor.getColumnIndex(InvDBAdapter.KEY_SUMMARY)));
				}

				// set up the adapter
				updateData();
			}
		}
	}
	
	private void updateData() {
		// refresh the list view
		ArrayAdapter<String> notes = new ArrayAdapter<String>(this, R.layout.meal_list_row, (String[]) recipeText.toArray());
		setListAdapter(notes);
	}
	
	private void saveToDatabase() {
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
}