package com.example.helloandroid;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.helloandroid.InvDBAdapter;

public class InventoryActivity extends ListActivity {
	private InvDBAdapter helper;
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private Cursor cursor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item_list);
		this.getListView().setDividerHeight(2);
		helper = new InvDBAdapter(this);
		helper.open();
		fillData();
		registerForContextMenu(getListView());
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
			helper.deleteItem(info.id);
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void createItem() {
		Intent i = new Intent(this, ItemDetails.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	// ListView and view (row) on which was clicked, position and
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, ItemDetails.class);
		i.putExtra(helper.KEY_ROWID, id);
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

	}

	private void fillData() {
		cursor = helper.fetchAllItems();
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

	/*int locationId;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		locationId = extras.getInt("locationId");
		String[] array = new String[1 + COUNTRIES.length];
		array[0] = "Tab ID = " + Integer.toString(locationId);
		for(int i = 0; i < COUNTRIES.length; i++)
			array[i+1] = (COUNTRIES[i]);
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, array));

		  ListView lv = getListView();
		  lv.setTextFilterEnabled(true);

		  lv.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view,
		        int position, long id) {
		      // When clicked, show a toast with the TextView text
		      Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
		          Toast.LENGTH_SHORT).show();
		    }
		  });
		
		//TextView tv = new TextView(this);
		//tv.setText("This is the Inventory tab.");
		//setContentView(R.layout.inv);
	}
	
	static final String[] COUNTRIES = new String[] {
	    "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra",
	    "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina",
	    "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan",
	    "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium",
	    "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia",
	    "Bosnia and Herzegovina", "Botswana", "Bouvet Island", "Brazil", "British Indian Ocean Territory",
	    "British Virgin Islands", "Brunei", "Bulgaria", "Burkina Faso", "Burundi",
	    "Cote d'Ivoire", "Cambodia", "Cameroon", "Canada", "Cape Verde",
	    "Cayman Islands", "Central African Republic", "Chad", "Chile", "China",
	    "Christmas Island", "Cocos (Keeling) Islands", "Colombia", "Comoros", "Congo",
	    "Cook Islands", "Costa Rica", "Croatia", "Cuba", "Cyprus", "Czech Republic",
	    "Democratic Republic of the Congo", "Denmark", "Djibouti", "Dominica", "Dominican Republic",
	    "East Timor", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea",
	    "Estonia", "Ethiopia", "Faeroe Islands", "Falkland Islands", "Fiji", "Finland",
	    "Former Yugoslav Republic of Macedonia", "France", "French Guiana", "French Polynesia",
	    "French Southern Territories", "Gabon", "Georgia", "Germany", "Ghana", "Gibraltar",
	    "Greece", "Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guinea", "Guinea-Bissau",
	    "Guyana", "Haiti", "Heard Island and McDonald Islands", "Honduras", "Hong Kong", "Hungary",
	    "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy", "Jamaica",
	    "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Kuwait", "Kyrgyzstan", "Laos",
	    "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg",
	    "Macau", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands",
	    "Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia", "Moldova",
	    "Monaco", "Mongolia", "Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia",
	    "Nauru", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia", "New Zealand",
	    "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "North Korea", "Northern Marianas",
	    "Norway", "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru",
	    "Philippines", "Pitcairn Islands", "Poland", "Portugal", "Puerto Rico", "Qatar",
	    "Reunion", "Romania", "Russia", "Rwanda", "Sqo Tome and Principe", "Saint Helena",
	    "Saint Kitts and Nevis", "Saint Lucia", "Saint Pierre and Miquelon",
	    "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Saudi Arabia", "Senegal",
	    "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands",
	    "Somalia", "South Africa", "South Georgia and the South Sandwich Islands", "South Korea",
	    "Spain", "Sri Lanka", "Sudan", "Suriname", "Svalbard and Jan Mayen", "Swaziland", "Sweden",
	    "Switzerland", "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "The Bahamas",
	    "The Gambia", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey",
	    "Turkmenistan", "Turks and Caicos Islands", "Tuvalu", "Virgin Islands", "Uganda",
	    "Ukraine", "United Arab Emirates", "United Kingdom",
	    "United States", "United States Minor Outlying Islands", "Uruguay", "Uzbekistan",
	    "Vanuatu", "Vatican City", "Venezuela", "Vietnam", "Wallis and Futuna", "Western Sahara",
	    "Yemen", "Yugoslavia", "Zambia", "Zimbabwe"
	  };*/
}