/* PANTRY PROTECTOR

/**
 * HelloAndroid
 * 
 * Version 0.0.0
 *
 * 09/22/2011

 */

package us.food4thought.pantryprotect;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;

public class PantryProtectorActivity extends TabActivity {
	
	public static int expired = 5;
	private InvDBAdapter mDbHelper = new InvDBAdapter(this);
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {  	
        super.onCreate(savedInstanceState);
        
        // Set the view to the main.xml layout
        setContentView(R.layout.main);
        
        // Sets aside resources for this application
        Resources res = getResources();
        
        // Create a host variable for tabs
        TabHost tabHost = getTabHost();
        
        // Create a spec variable for each tab
        TabHost.TabSpec spec;
        
        // Create an intent, this will be used for each activity that corresponds to the tab being created
        Intent intent;
        
        // Create an intent to launch the ScanActivity, add an icon to its tab with spec, add the tab linked to ScanActivity
        intent = new Intent().setClass(this, ScanActivity.class);
        spec = tabHost.newTabSpec("scan").setIndicator("Scan", res.getDrawable(R.drawable.ic_tab_scan)).setContent(intent);
        tabHost.addTab(spec);
        
        // Create an intent to launch the Inventory, add an icon to its tab with spec, add the tab linked to Inventory
        intent = new Intent().setClass(this, InventoryActivity.class);
        spec = tabHost.newTabSpec("inv").setIndicator("Inventory", res.getDrawable(R.drawable.ic_tab_inv)).setContent(intent);
        tabHost.addTab(spec);
        
        // Create an intent to launch the Grocery List, add an icon to its tab with spec, add the tab linked to Grocery List
        intent = new Intent().setClass(this, GListActivity.class);
        spec = tabHost.newTabSpec("glist").setIndicator("Grocery List", res.getDrawable(R.drawable.ic_tab_inv)).setContent(intent);
        tabHost.addTab(spec);
        
        // Create an intent to launch the Meal List, add an icon to its tab with spec, add the tab linked to Meal List
        intent = new Intent().setClass(this, MealListActivity.class);
        spec = tabHost.newTabSpec("meals").setIndicator("Meals").setContent(intent);
        tabHost.addTab(spec);
        
        // Create an intent to launch the OptionsActivity, add an icon to its tab with spec, add the tab linked to OptionsActivity
        intent = new Intent().setClass(this, OptionsActivity.class);
        spec = tabHost.newTabSpec("opts").setIndicator("Options", res.getDrawable(R.drawable.ic_tab_opts)).setContent(intent);
        tabHost.addTab(spec);
        
        // Initializes the current tab to ScanActivity
        tabHost.setCurrentTab(0);
        
        // Start the notification service
    	startService(new Intent(PantryProtectorActivity.this, PantryProtectorLocalService.class));
    }
    
    // Controller class, can be used to listen to and communicate with Services running
    public static class Controller extends Activity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.main);

            // Watch for button clicks.
            Button button = (Button)findViewById(R.id.invFrame);
            button.setOnClickListener(mStartListener);
            button = (Button)findViewById(R.id.include1);
            button.setOnClickListener(mStopListener);
        }

        private OnClickListener mStartListener = new OnClickListener() {
            public void onClick(View v) {
                // Make sure the service is started.  It will continue running
                // until someone calls stopService().  The Intent we use to find
                // the service explicitly specifies our service component, because
                // we want it running in our own process and don't want other
                // applications to replace it.
                startService(new Intent(Controller.this, PantryProtectorLocalService.class));
            }

        };

        private OnClickListener mStopListener = new OnClickListener() {
            public void onClick(View v) {
                // Cancel a previous call to startService().  Note that the
                // service will not actually stop at this point if there are
                // still bound clients.
                stopService(new Intent(Controller.this, PantryProtectorLocalService.class));
            }

				
        };
    }
    
    // Used to ensure that there is at least one location in the database
    @Override
	protected void onResume() {
		super.onResume();
	    mDbHelper.open();
		Cursor temp = mDbHelper.fetchAllLocations();
		if( temp == null || temp.getCount() < 1 ) {
			Intent intent = new Intent(this, LocationDetails.class);
			startActivity(intent);
		}
    }
    
    // Called with the result of the other activity
 	// requestCode was the origin request code send to the activity
 	// resultCode is the return code
 	// intend can be use to get some data from the caller
 	@Override
 	protected void onActivityResult(int requestCode, int resultCode,
 			Intent intent) {
 		super.onActivityResult(requestCode, resultCode, intent);
 		onResume();
 	}
}