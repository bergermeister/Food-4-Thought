package us.food4thought.pantryprotect;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;

import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class RecipeList extends ListActivity {
	private InvDBAdapter helper;							// database access
	private static final int ACTIVITY_CREATE = 0;			// key: creating new item
	private static final int ACTIVITY_EDIT = 1;				// key: editing pre-existing item
	private static final int DELETE_ID = Menu.FIRST + 1;
	private Cursor cursor;
	private int accessMode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.item_list);
		
		this.getListView().setDividerHeight(2);
		helper = new InvDBAdapter(this);
		helper.open();
		fillData();
		
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
		
		if( accessMode == 0 )
			registerForContextMenu(getListView());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.recipe_menu, menu);
		return true;
	}

	// Reaction to the menu selection
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.insert:
			createItem();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.insert:
			createItem();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			helper.deleteRecipe(info.id);
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void createItem() {
		Intent i = new Intent(this, RecipeAdd.class);
		i.putExtra("requestMode", accessMode);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	// ListView and view (row) on which was clicked, position and
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		if( accessMode != 0 ) {
			
			setResult((int)id);
			finish();
			
		} else {

			Intent i = new Intent(this, RecipeDisplay.class);
			i.putExtra(InvDBAdapter.KEY_ROWID, id);
			// Activity returns an result if called with startActivityForResult

			startActivityForResult(i, ACTIVITY_EDIT);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(RESULT_OK);
		}
		return super.onKeyDown(keyCode, event);
	}
	

	// Called with the result of the other activity
	// requestCode was the origin request code send to the activity
	// resultCode is the return code, 0 is everything is ok
	// intend can be use to get some data from the caller
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();
		if( accessMode != 0 && resultCode != RESULT_OK ) {
			setResult(resultCode);
			finish();
		}
	}

	private void fillData() {
		
		cursor = helper.fetchAllRecipes();
		startManagingCursor(cursor);

		String[] from = new String[] { InvDBAdapter.KEY_SUMMARY };
		int[] to = new int[] { R.id.label };

		// Now create an array adapter and set it to display using our row
		SimpleCursorAdapter notes = new SimpleCursorAdapter(this,
				R.layout.item_row, cursor, from, to);
		setListAdapter(notes);
		
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (helper != null) {
			helper.close();
		}
	}
}