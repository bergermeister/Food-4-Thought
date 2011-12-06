package us.food4thought.pantryprotect;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
		if (extras != null && extras.containsKey(InvDBAdapter.KEY_ROWID)) {
			mRowId = extras.getLong(InvDBAdapter.KEY_ROWID);
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
		if (mRowId != null && mRowId != 0 && !scan) {
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
