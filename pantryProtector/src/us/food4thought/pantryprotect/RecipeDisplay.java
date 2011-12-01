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

public class RecipeDisplay extends Activity {
	private TextView mTitleText;
	private TextView mBodyText;
	private Long mRowId;
	private InvDBAdapter mDbHelper;

	private Button cancelButton;
	private boolean scan = false;


	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		mDbHelper = new InvDBAdapter(this);
		mDbHelper.open();
		setContentView(R.layout.recipe_display);

		mTitleText = (TextView) findViewById(R.id.textView1);
		mBodyText = (TextView) findViewById(R.id.textView2);
		cancelButton = (Button) findViewById(R.id.item_cancel);

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
			Cursor todo = mDbHelper.fetchRecipe(mRowId);
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
	protected void onResume() {
		super.onResume();
		populateFields();
	}
}
