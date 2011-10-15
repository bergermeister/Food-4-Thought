package com.example.helloandroid;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class InvTabActivity extends TabActivity {
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Resources res = getResources();
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;
        
        final String[] LOCATIONS = new String[] {"Pantry","Refrigerator"};
        
        //for(int i = -1; i < LOCATIONS.length; i++) {
	        intent = new Intent().setClass(this, InventoryActivity.class);
	        /*String name = "All";
	        if(i > -1)
	        	name = LOCATIONS[i];
	        spec = tabHost.newTabSpec("inv"+name).setIndicator(name, res.getDrawable(R.drawable.ic_tab_inv)).setContent(intent);
	        tabHost.addTab(spec);
        }*/
        
        tabHost.setCurrentTab(0);
    }
}