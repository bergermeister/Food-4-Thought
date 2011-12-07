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

public class CookBook extends TabActivity {
	
	public static int expired = 5;
	private InvDBAdapter mDbHelper = new InvDBAdapter(this);
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {  	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Resources res = getResources();
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;
        
        intent = new Intent().setClass(this, Ingredients.class);
        spec = tabHost.newTabSpec("inv").setIndicator("Ingredients", res.getDrawable(R.drawable.ic_tab_inv)).setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, RecipeList.class);
        spec = tabHost.newTabSpec("inv").setIndicator("Recipes", res.getDrawable(R.drawable.ic_tab_inv)).setContent(intent);
        tabHost.addTab(spec);

        int tab_id = 0;
        if(savedInstanceState != null && savedInstanceState.containsKey("last_tab")) {
        	tab_id = savedInstanceState.getInt("last_tab");
        }
        
        tabHost.setCurrentTab(tab_id);
    }
    
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

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		// saves current tab to saved state bundle
		outState.putInt("last_tab", getTabHost().getCurrentTab());
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