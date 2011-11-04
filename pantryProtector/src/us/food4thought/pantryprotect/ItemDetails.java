package us.food4thought.pantryprotect;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ItemDetails extends Activity {
	private EditText mTitleText;
	private EditText mBodyText;
	private Long mRowId;
	private InvDBAdapter mDbHelper;
	private Spinner mCategory;
	private TextView mDateDisplay;
	private int mYear, mMonth, mDay;
	private Button mPickDate, confirmButton, cancelButton;
	private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, 
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDisplay();
                }
            };
	static final int DATE_DIALOG_ID = 0;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		mDbHelper = new InvDBAdapter(this);
		mDbHelper.open();
		setContentView(R.layout.item_edit);
		mCategory = (Spinner) findViewById(R.id.category);
		mTitleText = (EditText) findViewById(R.id.edit_summary);
		mBodyText = (EditText) findViewById(R.id.edit_description);
		mDateDisplay = (TextView) findViewById(R.id.edit_expiration);
		confirmButton = (Button) findViewById(R.id.edit_button);
		cancelButton = (Button) findViewById(R.id.item_cancel);
		mPickDate = (Button) findViewById(R.id.pickDate);
		mRowId = null;
		Bundle extras = getIntent().getExtras();
		mRowId = (bundle == null) ? null : (Long) bundle
				.getSerializable(InvDBAdapter.KEY_ROWID);
		if (extras != null) {
			mRowId = extras.getLong(InvDBAdapter.KEY_ROWID);
		}
		
		// populate spinner list
		InvDBAdapter helper = new InvDBAdapter(this);
		helper.open();
		Cursor cursor = helper.fetchAllLocations();
		startManagingCursor(cursor);

		String[] from = new String[] { InvDBAdapter.KEY_SUMMARY };
		int[] to = new int[] { android.R.id.text1 };

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getBaseContext(),
				android.R.layout.simple_spinner_item, cursor, from, to);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mCategory.setAdapter(adapter);
		
		helper.close();
		
		// populate fields
		populateFields();
		
		// add a click listener to the Confirm button
		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				saveState();
				setResult(RESULT_OK);
				finish();
			}

		});
		
		// add a click listener to the Cancel button
		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				setResult(RESULT_OK);
				finish();
			}

		});
		
		// add a click listener to the date picker
        mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

		
		// get the current date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        // display the current date (this method is below)
        updateDisplay();

	}

	@Override
	protected Dialog onCreateDialog(int id) {
	    switch (id) {
	    case DATE_DIALOG_ID:
	        return new DatePickerDialog(this,
	                    mDateSetListener,
	                    mYear, mMonth, mDay);
	    }
	    return null;
	}
	
    private void updateDisplay() {
        mDateDisplay.setText(
            new StringBuilder()
                    // Month is 0 based so add 1
                    .append(mMonth + 1).append("-")
                    .append(mDay).append("-")
                    .append(mYear).append(" "));
    }
	
	private void populateFields() {
		if (mRowId != null) {
			Cursor todo = mDbHelper.fetchItem(mRowId);
			startManagingCursor(todo);
			String category = todo.getString(todo
					.getColumnIndexOrThrow(InvDBAdapter.KEY_CATEGORY));
			
			for (int i=0; i<mCategory.getCount(); i++){
				Cursor temp = (Cursor) mCategory.getItemAtPosition(i);
				String s = temp.getString(temp.getColumnIndex(InvDBAdapter.KEY_SUMMARY)); 
				Log.e(null, s +" " + category);
				if (s.equalsIgnoreCase(category)){
					mCategory.setSelection(i);
				}
			}
			
			mTitleText.setText(todo.getString(todo
					.getColumnIndexOrThrow(InvDBAdapter.KEY_SUMMARY)));
			mBodyText.setText(todo.getString(todo
					.getColumnIndexOrThrow(InvDBAdapter.KEY_DESCRIPTION)));
			mDateDisplay.setText(todo.getString(todo.getColumnIndexOrThrow(InvDBAdapter.KEY_EXPIRATION)));
		}
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(InvDBAdapter.KEY_ROWID, mRowId);
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	}

	private void saveState() {
		Cursor temp = (Cursor) mCategory.getSelectedItem();
		String category = temp.getString(temp.getColumnIndex(InvDBAdapter.KEY_SUMMARY));
		String summary = mTitleText.getText().toString();
		String description = mBodyText.getText().toString();
        String expiration = mDateDisplay.getText().toString();
        
        Toast.makeText(getApplicationContext(), category, 2000);
		

		if (mRowId == null) {
			long id = mDbHelper.createItem(category, summary, description, expiration);
			if (id > 0) {
				mRowId = id;
			}
		} else {
			mDbHelper.updateItem(mRowId, category, summary, description, expiration);
		}
	}
}
