package us.food4thought.pantryprotect;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

public class OptionsActivity extends Activity {
	private static int mHour = 12, mMinute = 0, mDay = 5;
	private TextView mTimeDisplay;
	private ToggleButton toggleNotifications;
	private ToggleButton toggleVibration;
	private ToggleButton toggleLED;
	private Button save;
	private Button time;
	FileOutputStream fos;
	FileInputStream fis;
	static final int TIME_DIALOG_ID = 0;
	static final int NOTI_DIALOG_ID = 1;
	static final int ALER_DIALOG_ID = 2;
	private static boolean ledOn = true, vibrateOn = true, notifOn = true;
	
	private NumberPicker.OnChangedListener mChangedListener = new NumberPicker.OnChangedListener() {
		
		public void onChanged(NumberPicker picker, int oldVal, int newVal) {
			mDay = newVal;
		}
	};
	
	// the callback received when the user "sets" the time in the dialog
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
    	    new TimePickerDialog.OnTimeSetListener() {
    	        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    	            mHour = hourOfDay;
    	            mMinute = minute;
    	            updateDisplay();
    	        }
    	    };
	
	@Override
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.options);
		
		readConfig();
		createDisplay();

		// display the set time
		updateDisplay();
	}
	
	private void startServ(){
		startService(new Intent(this, PantryProtectorLocalService.class));
	}
	
	private void stopServ(){
		stopService(new Intent(this, PantryProtectorLocalService.class));
	}
	
	private void createDisplay(){
		// Capture View elements
		mTimeDisplay = (TextView) findViewById(R.id.timeDisplay);
		
		// Create actions for the save button
		save = (Button) findViewById(R.id.save);
		save.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				
				try {
					fos = openFileOutput("config.cfg", Context.MODE_PRIVATE);
					fos.write((mHour + ":" + mMinute).getBytes());
					fos.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(notifOn){
					//startServ();
				}
			}
		});
		
		
		
		// Create actions for the Set Time button
		time = (Button) findViewById(R.id.setTimebutton1);
		time.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				showDialog(TIME_DIALOG_ID);
			}
		});
		
		toggleLED = (ToggleButton) findViewById(R.id.ledtogglebutton);
		toggleLED.setChecked(true);
		toggleLED.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				if(toggleLED.isChecked()){
					ledOn = true;
				}
				else {
					ledOn = false;
				}
			}
		});
		
		// Create actions for the Alert Toggle button
		toggleVibration = (ToggleButton) findViewById(R.id.vibratetogglebutton);
		toggleVibration.setChecked(true);
		toggleVibration.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				if(toggleVibration.isChecked()){
					vibrateOn = true;
				}
				else {
					vibrateOn = false;
				}
			}
		});

		// Create actions for the Notifications Toggle button
		toggleNotifications = (ToggleButton) findViewById(R.id.notificationtogglebutton);
		toggleNotifications.setChecked(true);
		// Add a click listener to the toggle button
		toggleNotifications.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		        // Perform action on clicks
		        if (toggleNotifications.isChecked()) {
	            	//showDialog(NOTI_DIALOG_ID);
		        	setContentView(R.layout.numberpicker_dialog);
	            	notifOn = true;
	            	//startServ();
		        } 
		        else {
		        	notifOn = false;
		        	//stopServ();
		        }
		    }

		});
	}
	
	//
    private void updateDisplay() {
        mTimeDisplay.setText(
            new StringBuilder()
            		.append(" ").append(pad(mHour))
            		.append(":")
                    .append(pad(mMinute)));
    }
    		
    // Adds padding around a number
    private static String pad(int c) {
    	if (c >= 10)
    		return String.valueOf(c);
    	else
    		return "0" + String.valueOf(c);
    }
    
    private void readConfig(){
		String text;
		byte [] buffer = new byte[500];
		try {
			fis = openFileInput("config.cfg");
			fis.read(buffer);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Toast.makeText(this, "No File", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		text = buffer.toString();
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
		
    }
    
    // Creates a dialog screen for a Time Picker
    @Override
    protected Dialog onCreateDialog(int id) {
    	Dialog dialog;
    	
        switch (id) {
        case TIME_DIALOG_ID:
            dialog = new TimePickerDialog(this, mTimeSetListener, mHour, mMinute, false);
            break;
		case NOTI_DIALOG_ID:
			dialog = NotificationNumberPickerDialog(this);
			break;
		case ALER_DIALOG_ID:
			dialog = AlertNumberPickerDialog(this);
			break;
        default:
        	dialog = null;
        }
        return dialog;
    }
    
    protected Dialog AlertNumberPickerDialog(Context context){
    	Dialog dialog;
    	dialog = new Dialog(context);
		dialog.setContentView(R.layout.numberpicker_dialog);
		dialog.setTitle("Custom Dialog 2");
		TextView text2 = (TextView) dialog.findViewById(R.id.text);
    	text2.setText("Test2");
    	
    	return dialog;
    }
    
    protected Dialog NotificationNumberPickerDialog(Context context){
		Dialog dialog;
		dialog = new Dialog(context);
		dialog.setContentView(R.layout.numberpicker_dialog);
		dialog.setTitle("Custom Dialog"); 
		TextView text = (TextView) dialog.findViewById(R.id.text);
    	text.setText("Test1");
    	//NumberPicker notifday = (NumberPicker) dialog.findViewById(R.id.numpick);
    	//mDay = notifday.mCurrent;
    	
    	return dialog;
    }
    
   
    @Override
    protected void onDestroy() {
        super.onDestroy();
    } 
}