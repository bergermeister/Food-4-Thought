package com.example.helloandroid;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class ScanActivity extends Activity {
	
	private OnClickListener scanClick = new OnClickListener() {
		public void onClick(View v) {
			zxingScan();
		}
	};
	
	private OnClickListener skipClick = new OnClickListener() {
		public void onClick(View v) {
			if(spinny.isShown())
				resetMainMenu();
		}
	};
	
	private void resetMainMenu() {
		scanButton.setVisibility(View.VISIBLE);
		skipButton.setText(R.string.skip_text);
		spinny.setVisibility(View.GONE);
	}
	
	private Button scanButton;
	private Button skipButton;
	private View spinny;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.scan);
		
		scanButton = (Button)findViewById(R.id.zxing_button);
		skipButton = (Button)findViewById(R.id.skip_button);
		spinny = findViewById(R.id.scanningNow);
		
		scanButton.setOnClickListener(scanClick);
		skipButton.setOnClickListener(skipClick);
	}
	
	public void onResume() {
		super.onResume();

		resetMainMenu();
	}
	
	public void zxingScan() {
		spinny.setVisibility(View.VISIBLE);
		scanButton.setVisibility(View.GONE);
		skipButton.setText(R.string.abort_text);
	}
}