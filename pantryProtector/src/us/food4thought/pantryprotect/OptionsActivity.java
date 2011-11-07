package us.food4thought.pantryprotect;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

public class OptionsActivity extends Activity {
	public static final String PREFS_NAME = "Options";
	private static int mHour = 12, mMinute = 0, mDay = 5;
	private TextView mTimeDisplay;
	private ToggleButton toggleNotifications;
	private ToggleButton toggleVibration;
	private ToggleButton toggleLED;
	private Button save;
	private Button time;
	static final int TIME_DIALOG_ID = 0;
	static final int NOTI_DIALOG_ID = 1;
	static final int VIBR_DIALOG_ID = 2;
	static final int FLAS_DIALOG_ID = 3;
	private static boolean ledOn = true, vibrateOn = true, notifOn = true;
	
	// the callback received when the user "sets" the time in the dialog
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
    	    new TimePickerDialog.OnTimeSetListener() {
    	        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    	            mHour = hourOfDay;
    	            mMinute = minute;
    	            updateDisplay();
    	        }
    	    };
	
    Button.OnClickListener saveOnClickListener = new Button.OnClickListener(){
	   public void onClick(View arg0) {
		   SavePreferences();
	   }
    };
    	    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.options);

		createDisplay();		
		LoadPreferences();
		
		// display the set time
		updateDisplay();
	}
	
	private void SavePreferences(){
		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean("LED", ledOn);
		editor.commit();
		
		editor.putBoolean("Vibrate", vibrateOn);
		editor.commit();
		
		editor.putBoolean("Notifications", notifOn);
		editor.commit();
		
		editor.putInt("Hour", mHour);
		editor.commit();
		
		editor.putInt("Minute", mMinute);
		editor.commit();
		
		editor.putInt("Days", mDay);
		editor.commit();
		
		Toast.makeText(this, "Preferences Saved Successfully", Toast.LENGTH_SHORT).show();
	}
	
	private void LoadPreferences(){
		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
		ledOn = sharedPreferences.getBoolean("LED", true);
		vibrateOn = sharedPreferences.getBoolean("Vibrate", true);
		notifOn = sharedPreferences.getBoolean("Notifications", true);
		mHour = sharedPreferences.getInt("Hour", 12);
		mMinute = sharedPreferences.getInt("Minute", 0);
		mDay = sharedPreferences.getInt("Days", 5);
	}
	
	private void createDisplay(){
		// Capture View elements
		mTimeDisplay = (TextView) findViewById(R.id.timeDisplay);
		
		// Create actions for the save button
		save = (Button) findViewById(R.id.save);
		save.setOnClickListener(saveOnClickListener);		
		
		// Create actions for the Set Time button
		time = (Button) findViewById(R.id.setTimebutton1);
		time.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				showDialog(TIME_DIALOG_ID);
			}
		});
		
		toggleLED = (ToggleButton) findViewById(R.id.ledtogglebutton);
		toggleLED.setChecked(ledOn);
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
		toggleVibration.setChecked(vibrateOn);
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
		toggleNotifications.setChecked(notifOn);
		// Add a click listener to the toggle button
		toggleNotifications.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		        // Perform action on clicks
		        if (toggleNotifications.isChecked()) {
	            	showDialog(NOTI_DIALOG_ID);
		        	//setContentView(R.layout.numberpicker_dialog);
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
	
	// Update the TextView displaying the time.
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
		case VIBR_DIALOG_ID:
			dialog = VibrateNumberPickerDialog(this);
			break;
        default:
        	dialog = null;
        }
        return dialog;
    }
    
    protected Dialog VibrateNumberPickerDialog(Context context){
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
		dialog.setTitle("Pick Number of Days"); 
		TextView text = (TextView) dialog.findViewById(R.id.text);
    	text.setText("Set the number of days before expiration to begin receiving notifications in the status bar.");
    	NumberPicker notifday = (NumberPicker) dialog.findViewById(R.id.numpick);
    	//Button set = (Button) dialog.findViewById(R.id.numpick);

    	return dialog;
    }
    
   
    @Override
    protected void onDestroy() {
        super.onDestroy();
    } 
}