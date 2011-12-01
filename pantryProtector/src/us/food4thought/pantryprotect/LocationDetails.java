package us.food4thought.pantryprotect;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LocationDetails extends Activity {
	private EditText mTitleText;							// Text box for the location name
	private EditText mBodyText;								// Text box for the location details
	private Long mRowId;									// Row id of the database
	private InvDBAdapter mDbHelper;							// Adapter to connect to the database
	private Button confirmButton, cancelButton;				// buttons to confirm or cancel

	// Called when the activity is first created
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		// Open the link to the database
		mDbHelper = new InvDBAdapter(this);
		mDbHelper.open();
		
		// set the view to the location_edit.xml layout
		setContentView(R.layout.location_edit);
		
		// Associate the variables with their corresponding entities in the .xml file
		mTitleText = (EditText) findViewById(R.id.loc_summary);
		mBodyText = (EditText) findViewById(R.id.loc_description);
		confirmButton = (Button) findViewById(R.id.loc_button);
		cancelButton = (Button) findViewById(R.id.loc_cancel);
		
		// Initialize the row id
		mRowId = null;
		
		// Set the row id to the first row of the database
		Bundle extras = getIntent().getExtras();
		mRowId = (bundle == null) ? null : (Long) bundle
				.getSerializable(InvDBAdapter.KEY_ROWID);
		if (extras != null) {
			mRowId = extras.getLong(InvDBAdapter.KEY_ROWID);
		}
		
		// Populates all locations
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
				setResult(RESULT_CANCELED);
				finish();
			}

		});
		
		// hide the cancel button if no location already exists
		Cursor cursor = mDbHelper.fetchAllLocations();
		if( cursor == null || cursor.getCount() < 1 ) {
			cancelButton.setVisibility(View.GONE);
		}

	}
	
	// Populate all the body with all locations
	private void populateFields() {
		if (mRowId != null) {
			Cursor todo = mDbHelper.fetchLocation(mRowId);
			startManagingCursor(todo);

			mTitleText.setText(todo.getString(todo
					.getColumnIndexOrThrow(InvDBAdapter.KEY_SUMMARY)));
			mBodyText.setText(todo.getString(todo
					.getColumnIndexOrThrow(InvDBAdapter.KEY_DESCRIPTION)));
		}
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(InvDBAdapter.KEY_ROWID, mRowId);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setResult(RESULT_CANCELED);
		populateFields();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && mDbHelper.fetchAllLocations().getCount() == 0) {
			// require user to create a location
			Toast.makeText(getApplicationContext(), R.string.location_req, Toast.LENGTH_LONG).show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// Saves the state, storing the details of the location in the database
	private void saveState() {
		String summary = mTitleText.getText().toString();
		String description = mBodyText.getText().toString();
		

		if (mRowId == null) {
			long id = mDbHelper.createLocation(summary, description);
			if (id > 0) {
				mRowId = id;
			}
		} else {
			mDbHelper.updateLocation(mRowId, summary, description);
		}
	}
}
