package us.food4thought.pantryprotect;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RecipeAdd extends Activity {
	private EditText mTitleText;
	private EditText mBodyText;
	private Long mRowId;
	private InvDBAdapter mDbHelper;
	private Button confirmButton, cancelButton;
	private boolean scan = false;
	private int accessMode;
	/*private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, 
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDisplay();
                }
            };
	static final int DATE_DIALOG_ID = 0;*/

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		mDbHelper = new InvDBAdapter(this);
		mDbHelper.open();
		setContentView(R.layout.recipe_edit);
		//mCategory = (Spinner) findViewById(R.id.category);
		mTitleText = (EditText) findViewById(R.id.edit_summary);
		//mBodyText = (EditText) findViewById(R.id.edit_description);
		//mDateDisplay = (TextView) findViewById(R.id.edit_expiration);
		confirmButton = (Button) findViewById(R.id.edit_button);
		cancelButton = (Button) findViewById(R.id.item_cancel);
		//mPickDate = (Button) findViewById(R.id.pickDate);
		mRowId = null;
		Bundle extras = getIntent().getExtras();
		mRowId = (bundle == null) ? null : (Long) bundle
				.getSerializable(InvDBAdapter.KEY_ROWID);
		if (extras != null) {
			mRowId = extras.getLong(InvDBAdapter.KEY_ROWID);
			if(extras.containsKey("SCAN_TITLE"))
			{
				scan = true;
				mTitleText.setText(extras.getString("SCAN_TITLE"));
			}
		}
		
		// populate spinner list
		/*InvDBAdapter helper = new InvDBAdapter(this);
		helper.open();
		Cursor cursor = helper.fetchAllLocations();
		startManagingCursor(cursor);

		String[] from = new String[] { InvDBAdapter.KEY_SUMMARY };
		int[] to = new int[] { android.R.id.text1 };

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getBaseContext(),
				android.R.layout.simple_spinner_item, cursor, from, to);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mCategory.setAdapter(adapter);
		
		helper.close();*/
		
		// populate fields
		populateFields();
		
		// add a click listener to the Confirm button
		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				saveState();
				if( accessMode == 0 )
					setResult(RESULT_OK);
				else
					setResult(mRowId.intValue());
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
       /* mPickDate.setOnClickListener(new View.OnClickListener() {
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
    }*/
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		Intent i = getIntent();
		Bundle bundle = i.getExtras();
		if( bundle != null && bundle.containsKey("requestMode") )
			accessMode = bundle.getInt("requestMode");
		else
			accessMode = 0;
	}
	
	private void populateFields() {
		if (mRowId != null && !scan) {
			Cursor todo = mDbHelper.fetchRecipe(mRowId);
			startManagingCursor(todo);

			if(todo.getCount() > 0) {

				mTitleText.setText(todo.getString(todo
						.getColumnIndexOrThrow(InvDBAdapter.KEY_SUMMARY)));
				mBodyText.setText(todo.getString(todo
						.getColumnIndexOrThrow(InvDBAdapter.KEY_DESCRIPTION)));
			}
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
		//Cursor temp = (Cursor) mCategory.getSelectedItem();
		//String category = temp.getString(temp.getColumnIndex(InvDBAdapter.KEY_SUMMARY));
		String summary = mTitleText.getText().toString();
		String description = "%";
        //String expiration = mDateDisplay.getText().toString();
		

		if (mRowId == null || mRowId == 0 || scan) {
			long id = mDbHelper.createRecipe(summary, description);
			if (id > 0) {
				mRowId = id;
			}
		} else {
			mDbHelper.updateRecipe(mRowId, summary, description);
		}
        
        Toast.makeText(getApplicationContext(), summary + " :: " + mRowId.toString(), Toast.LENGTH_SHORT).show();
	}
}
