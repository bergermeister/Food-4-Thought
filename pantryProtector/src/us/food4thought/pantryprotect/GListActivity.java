package us.food4thought.pantryprotect;

import java.util.ArrayList;
import java.util.Collections;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

public class GListActivity extends ListActivity {
	private InvDBAdapter helper;											// Adapter to link to database
	private static final int ACTIVITY_CREATE = 0;							// Flag for calling an Activity to create it
	private static final int ACTIVITY_EDIT = 1;								// Flag for calling an Activity to edit it
	private static final int DELETE_ID = Menu.FIRST + 1;					// 
	private Cursor cursor;													// Cursor for iterating through database
	private Spinner mSort;													// Spinner for sorting
	private Spinner mFilter;												// Spinner for filtering
	private static ArrayList<String> firstChars = new ArrayList<String>();	// Array of the first character in each item
	private static ArrayList<String> locations = new ArrayList<String>();	// Array of locations
	private static final String[] strArray = new String[] {};				// Array of strings
	
	// Called when the activity is created.
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Sets the view to item_list.xml and links the spinner variables to their location in the xml file
		setContentView(R.layout.item_list);
		mSort = (Spinner) findViewById(R.id.inv_sort);
		mFilter = (Spinner) findViewById(R.id.inv_filter);
		this.getListView().setDividerHeight(2);
		
		// Opens a link to the database
		helper = new InvDBAdapter(this);
		helper.open();
		
		// Populates the window with items
		fillData();
		registerForContextMenu(getListView());
		
		// Sort method Spinner changed
		mSort.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				Resources r = getResources();				
				String sortMethod = mSort.getSelectedItem().toString();

				if( sortMethod.equalsIgnoreCase(r.getStringArray(R.array.sort_by)[1]) ) {
					// generate sort by Name filter list

					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							getBaseContext(), android.R.layout.simple_spinner_item, firstChars.toArray(strArray));
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					mFilter.setAdapter(adapter);

				} else if( sortMethod.equalsIgnoreCase(r.getStringArray(R.array.sort_by)[2]) ) {
					// generate sort by Location filter list
					
					cursor = helper.fetchAllLocations();
					startManagingCursor(cursor);

					cursor.moveToFirst();
					locations.clear();
					while( !cursor.isAfterLast() ) {
						locations.add(cursor.getString(cursor.getColumnIndex(InvDBAdapter.KEY_SUMMARY)));
						cursor.moveToNext();
					}
					locations.add(0, "None");
					
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							getBaseContext(), android.R.layout.simple_spinner_item, locations.toArray(strArray));
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					mFilter.setAdapter(adapter);
					
				} else {
					// generate blank filter list

					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							getBaseContext(), android.R.layout.simple_spinner_item, new String[] {});
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					mFilter.setAdapter(adapter);
					
				}
				
				// having changed the sort method, re-populate the inventory 
				fillData();
				
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});
		
		mFilter.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				
				// having changed the filter method, re-populate the inventory 
				fillData();
				
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.listmenu, menu);
		return true;
	}

	// Reaction to the menu selection
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.insert:
			createItem();
			return true;
		case R.id.new_loc:
			createLocation();
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
			helper.deleteGrocery(info.id);
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void createItem() {
		Intent i = new Intent(this, GroceryDetails.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}
	
	private void createLocation() {
		Intent i = new Intent(this, LocationDetails.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	// ListView and view (row) on which was clicked, position and
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, GroceryDetails.class);
		i.putExtra(InvDBAdapter.KEY_ROWID, id);
		// Activity returns an result if called with startActivityForResult
		
		startActivityForResult(i, ACTIVITY_EDIT);
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
		if( requestCode == ACTIVITY_CREATE && mSort.getSelectedItem().toString().equalsIgnoreCase("Name") ) {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					getBaseContext(), android.R.layout.simple_spinner_item, firstChars.toArray(strArray));
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mFilter.setAdapter(adapter);
		} else if( requestCode == ACTIVITY_CREATE && mSort.getSelectedItem().toString().equalsIgnoreCase("Location") ) {
			cursor = helper.fetchAllLocations();
			startManagingCursor(cursor);

			cursor.moveToFirst();
			locations.clear();
			while( !cursor.isAfterLast() ) {
				locations.add(cursor.getString(cursor.getColumnIndex(InvDBAdapter.KEY_SUMMARY)));
				cursor.moveToNext();
			}
			locations.add(0, "None");
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					getBaseContext(), android.R.layout.simple_spinner_item, locations.toArray(strArray));
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mFilter.setAdapter(adapter);
		}
	}

	private void fillData() {
		String filter_by = null;
		if( mFilter.getCount() != 0 && mFilter.getSelectedItemPosition() != 0 ) {
			filter_by = mFilter.getSelectedItem().toString();
		}
		
		cursor = helper.fetchAllGroceries(mSort.getSelectedItem().toString(), filter_by);
		startManagingCursor(cursor);

		String[] from = new String[] { InvDBAdapter.KEY_SUMMARY };
		int[] to = new int[] { R.id.label };

		// Now create an array adapter and set it to display using our row
		SimpleCursorAdapter notes = new SimpleCursorAdapter(this,
				R.layout.item_row, cursor, from, to);
		
		setListAdapter(notes);
		ArrayList<String> items = new ArrayList<String>();
		firstChars.clear();
		cursor.moveToFirst();
		while( !cursor.isAfterLast() ) {
			items.add(cursor.getString(cursor.getColumnIndex(InvDBAdapter.KEY_SUMMARY)));
			boolean alreadyExists = false;
			String firstChar = cursor.getString(cursor.getColumnIndex(InvDBAdapter.KEY_SUMMARY)).substring(0, 1);
			for( String s : firstChars ) {
				if( s.equalsIgnoreCase(firstChar) ) {
					alreadyExists = true;
					break;
				}
			}
			if( !alreadyExists ) {
				firstChars.add(firstChar);
			}
			cursor.moveToNext();
		}
		
		/*ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_list_item_1, items.toArray(strArray));
		setListAdapter(adapter);*/
		Collections.sort(firstChars);
		firstChars.add(0, "None");
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

