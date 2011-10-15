/**
 * HelloAndroid
 * 
 * Version 0.0.0
 *
 * 09/22/2011
 */

package com.example.helloandroid;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class HelloAndroid extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Resources res = getResources();
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;
        
        intent = new Intent().setClass(this, ScanActivity.class);
        spec = tabHost.newTabSpec("scan").setIndicator("Scan", res.getDrawable(R.drawable.ic_tab_scan)).setContent(intent);
        tabHost.addTab(spec);
        
        //intent = new Intent().setClass(this, InvTabActivity.class);
        intent = new Intent().setClass(this, InventoryActivity.class);
        spec = tabHost.newTabSpec("inv").setIndicator("Inventory", res.getDrawable(R.drawable.ic_tab_inv)).setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, GListActivity.class);
        spec = tabHost.newTabSpec("glist").setIndicator("Grocery List", res.getDrawable(R.drawable.ic_tab_inv)).setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, OptionsActivity.class);
        spec = tabHost.newTabSpec("opts").setIndicator("Options", res.getDrawable(R.drawable.ic_tab_opts)).setContent(intent);
        tabHost.addTab(spec);
        
        tabHost.setCurrentTab(0);
    }
}