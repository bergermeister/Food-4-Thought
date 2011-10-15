package us.food4thought.pantryprotect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


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
	private TextView scanResult;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.scan);
		
		scanButton = (Button)findViewById(R.id.zxing_button);
		skipButton = (Button)findViewById(R.id.skip_button);
		spinny = findViewById(R.id.scanningNow);
		scanResult = (TextView)findViewById(R.id.textView2);
		
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
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.setPackage("com.google.zxing.client.android");
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, 0);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	    if (requestCode == 0) {
	        if (resultCode == RESULT_OK) {
	            String contents = intent.getStringExtra("SCAN_RESULT");
	            String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
	            scanResult.setText(contents);
	            resetMainMenu();
	        } else if (resultCode == RESULT_CANCELED) {
	            scanResult.setText("Fail Scan");
	        }
	    }
	}
}