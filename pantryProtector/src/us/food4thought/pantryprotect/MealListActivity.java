package us.food4thought.pantryprotect;

import java.util.Calendar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;

public class MealListActivity extends ListActivity {

	private InvDBAdapter mDatabase;
	private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, 
                                      int month, int day) {
                	Time parser = new Time();
                	parser.set(day, month, year);
                	if(!mDatabase.dateExists(Time.getJulianDay(parser.toMillis(true), parser.gmtoff)))
                		createNewMeal(year, month, day);
                	else
                		editMeal(Time.getJulianDay(parser.toMillis(true), parser.gmtoff));
                }
            };
    
    static final int DATE_DIALOG_ID = 0;
    
    static final int MEAL_CREATE = 0;
    static final int MEAL_EDIT = 1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.meal_list);
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
	
	private void createNewMeal(int year, int month, int day) {
		Intent i = new Intent().setClass(this, MealViewActivity.class);
		i.putExtra("_year", year);
		i.putExtra("_month", month);
		i.putExtra("_day", day);
		startActivityForResult(i, MEAL_CREATE);
	}
	
	private void editMeal(long mealID) {
		
	}
}