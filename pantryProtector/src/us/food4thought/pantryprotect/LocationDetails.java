package us.food4thought.pantryprotect;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LocationDetails extends Activity {
	private EditText mTitleText;
	private EditText mBodyText;
	private Long mRowId;
	private InvDBAdapter mDbHelper;
	private Button confirmButton, cancelButton;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		mDbHelper = new InvDBAdapter(this);
		mDbHelper.open();
		setContentView(R.layout.location_edit);
		mTitleText = (EditText) findViewById(R.id.loc_summary);
		mBodyText = (EditText) findViewById(R.id.loc_description);
		confirmButton = (Button) findViewById(R.id.loc_button);
		cancelButton = (Button) findViewById(R.id.loc_cancel);
		mRowId = null;
		Bundle extras = getIntent().getExtras();
		mRowId = (bundle == null) ? null : (Long) bundle
				.getSerializable(InvDBAdapter.KEY_ROWID);
		if (extras != null) {
			mRowId = extras.getLong(InvDBAdapter.KEY_ROWID);
		}
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
		
		// hide the cancel button if no location already exists
		Cursor cursor = mDbHelper.fetchAllLocations();
		if( cursor == null || cursor.getCount() < 1 ) {
			cancelButton.setVisibility(View.GONE);
		}

	}
	
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
		populateFields();
	}

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
