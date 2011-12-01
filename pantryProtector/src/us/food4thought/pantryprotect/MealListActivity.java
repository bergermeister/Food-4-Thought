package us.food4thought.pantryprotect;

import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView;

public class MealListActivity extends ListActivity {

	private InvDBAdapter mDatabase;
	private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, 
                                      int month, int day) {
                	String mealID =  (String) DateFormat.format("EEEE, MMM dd, yyyy",
                			new Date(year, month, day));
                	if(!mDatabase.dateExists(mealID))
                		startViewMeal(MEAL_CREATE, mealID);
                	else
                		startViewMeal(MEAL_EDIT, mealID);
                }
            };
    
    static final int DATE_DIALOG_ID = 0;
    private static final int DELETE_ID = Menu.FIRST + 1;
    
    static final int MEAL_CREATE = 0;
    static final int MEAL_EDIT = 1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.meal_list);
    	
    	// connect to the database
    	mDatabase = new InvDBAdapter(this);
    	mDatabase.open();
    	
    	// load existing meal plans into the list
    	fillData();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	
    	mDatabase.close();
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.meal_list_menu, menu);
		return true;
	}

	// Reaction to the menu selection
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_meal:
			 showDialog(DATE_DIALOG_ID);
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
	    switch (id) {
	    case DATE_DIALOG_ID:
	    	
	        return new DatePickerDialog(this,
	                    mDateSetListener,
	                    Calendar.getInstance().get(Calendar.YEAR),
	                    Calendar.getInstance().get(Calendar.MONTH),
            			Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
	    }
	    return null;
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			ListView view = (ListView) findViewById(android.R.id.list);
			TextView text = (TextView) view.getSelectedItem();
			mDatabase.deleteMeal((String) text.getText());
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	private void fillData() {
		// fetch all the meals from the database
		Cursor cursor = mDatabase.fetchAllMeals();
		startManagingCursor(cursor);
		
		// set the to and from fields
		String[] from = new String[] { InvDBAdapter.KEY_ROWID };
		int[] to = new int[] { R.id.label };
		
		// set up the adapter
		SimpleCursorAdapter notes = new SimpleCursorAdapter(this, R.layout.meal_list_row, cursor, from, to);
		setListAdapter(notes);
	}
	
	private void startViewMeal(int accessMethod, String mealID) {
		Intent i = new Intent().setClass(this, MealViewActivity.class);
		i.putExtra(InvDBAdapter.KEY_ROWID, mealID);
		startActivityForResult(i, accessMethod);
	}
	
	// Called with the result of the other activity
	// requestCode was the origin request code send to the activity
	// resultCode is the return code, 0 is everything is ok
	// intend can be use to get some data from the caller
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		
		// update the list with changes
		fillData();
	}
}