package us.food4thought.pantryprotect;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RecipeDetails extends Activity {
	private TextView mTitleText;
	private TextView mBodyText;
	private Long mRowId;
	private InvDBAdapter mDbHelper;
	private Spinner recs;

	private Button confirmButton, cancelButton;
	private boolean scan = false;
	private static ArrayList<String> recArray = new ArrayList<String>();
	private static final String[] strArray = new String[] {};	// empty string array for casting purposes

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		mDbHelper = new InvDBAdapter(this);
		mDbHelper.open();
		setContentView(R.layout.recipe_item);

		mTitleText = (TextView) findViewById(R.id.textView1);
		recs = (Spinner) findViewById(R.id.category);
		cancelButton = (Button) findViewById(R.id.item_cancel);
		confirmButton = (Button) findViewById(R.id.edit_button);
		
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
		

		
		// populate fields
		populateFields();

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
		
	}
	
	private void populateFields() {
		if (mRowId != null && !scan) {
			Cursor cursor1 = mDbHelper.fetchAllRecipes();
			startManagingCursor(cursor1);
			
			String[] from = new String[] { InvDBAdapter.KEY_SUMMARY };
			int[] to = new int[] { android.R.id.text1 };

			SimpleCursorAdapter adapter = new SimpleCursorAdapter(getBaseContext(),
					android.R.layout.simple_spinner_item, cursor1, from, to);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			recs.setAdapter(adapter);
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
        
        Toast.makeText(getApplicationContext(), summary, 2000);
		

		if (mRowId == null || scan) {
			long id = mDbHelper.createRecipe(summary, description);
			if (id > 0) {
				mRowId = id;
			}
		} else {
			mDbHelper.updateRecipe(mRowId, summary, description);
		}
	}
}
