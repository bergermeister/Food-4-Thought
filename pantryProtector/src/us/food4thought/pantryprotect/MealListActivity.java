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
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView;
//import android.widget.Toast;

public class MealListActivity extends ListActivity {

	private InvDBAdapter mDatabase;
	private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, 
                                      int month, int day) {
                	String mealID =  (String) DateFormat.format("MM/dd/yyyy",
                			new Date(year - 1900, month, day));
//                	Toast.makeText(getBaseContext(), "" + year, Toast.LENGTH_SHORT).show();
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
		case R.id.clear_all:
			shredMeals();
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
			TextView text = (TextView)( (LinearLayout)getListView().getChildAt(info.position) ).getChildAt(0);
//			Toast.makeText(this, (String) text.getText() + " deleted.", Toast.LENGTH_SHORT).show();
			mDatabase.deleteMeal((String) text.getText());
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	// ListView and view (row) on which was clicked, position and
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, MealViewActivity.class);
		i.putExtra(InvDBAdapter.KEY_ROWID, ((TextView)((LinearLayout)v).getChildAt(0)).getText());
		// Activity returns an result if called with startActivityForResult

		startActivityForResult(i, MEAL_EDIT);
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
	
	private void shredMeals() {
		int position = 0;
		TextView text;
		while( getListView().getChildAt(position) != null ) {
			text = (TextView)( (LinearLayout)getListView().getChildAt(position) ).getChildAt(0);
			mDatabase.deleteMeal((String) text.getText());
			position++;
		}
		fillData();
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