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
	public static final String PREFS_NAME = "Options";						// Name of Shared Preferences
	private static int mHour = 12, mMinute = 0, mDay = 5;					// Variable for time and number of days
	private TextView mTimeDisplay;											// TextView to display the time
	private ToggleButton toggleNotifications;								// Toggle button for notifications
	private ToggleButton toggleVibration;									// Toggle button for vibrations
	private ToggleButton toggleLED;											// Toggle button for LEDs
	private Button save;													// Push button for saving preferences
	private Button time;													// Push button for setting time
	static final int TIME_DIALOG_ID = 0;									// Time select dialog ID
	static final int NOTI_DIALOG_ID = 1;									// Day select dialog ID for Notifications
	static final int VIBR_DIALOG_ID = 2;									// Day select dialog ID for Vibrations
	static final int FLAS_DIALOG_ID = 3;									// Day select dialog ID for Flashing
	private static boolean ledOn = true, vibrateOn = true, notifOn = true;	// Flags for notifications, flashing, and vibration
	
	// the callback received when the user "sets" the time in the dialog
    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		// Sets the time received from the Time pick dialog window and updates display
	    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	        mHour = hourOfDay;
	        mMinute = minute;
	        updateDisplay();
	    }
    };
	
    // Creates a listener for the save button which saves the settings to shared preferences.
    Button.OnClickListener saveOnClickListener = new Button.OnClickListener(){
	   public void onClick(View arg0) {
		   SavePreferences();
	   }
    };

    // Called when the activity is first created.
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Sets the view to the options.xml layout
		setContentView(R.layout.options);

		// Loads preferences
		LoadPreferences();
		
		// Creates the display and intializes listeners
		createDisplay();
		
		// display the set time
		updateDisplay();
	}
	
	// Saves settings to shared preferences when the save button is clicked
	private void SavePreferences(){
		// Gets the shared preferences if they exist, else creates them.
		SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		
		// Creates an editor for shared preferences.
		SharedPreferences.Editor editor = sharedPreferences.edit();
		
		// Commits the current setting to the shared preferences for flashing lights
		editor.putBoolean("LED", ledOn);
		editor.commit();
		
		// Commits the current setting to the shared preferences for vibrations
		editor.putBoolean("Vibrate", vibrateOn);
		editor.commit();
		
		// Commits the current setting to the shared preferences for Notifications
		editor.putBoolean("Notifications", notifOn);
		editor.commit();
		
		// Commits the current setting to the shared preferences for the hour
		editor.putInt("Hour", mHour);
		editor.commit();
		
		// Commits the current setting to the shared preferences for minutes
		editor.putInt("Minute", mMinute);
		editor.commit();
		
		// Commits the current setting to the shared preferences for days
		editor.putInt("Days", mDay);
		editor.commit();
		
		// Notify users of successful save
		Toast.makeText(this, "Preferences Saved Successfully", Toast.LENGTH_SHORT).show();
	}
	
	// Load shared preferences to settings
	private void LoadPreferences(){
		// Retrieve shared preferences
		SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		
		// Sets all flags and variables to the stored shared preferences value.
		ledOn = sharedPreferences.getBoolean("LED", true);
		vibrateOn = sharedPreferences.getBoolean("Vibrate", true);
		notifOn = sharedPreferences.getBoolean("Notifications", true);
		mHour = sharedPreferences.getInt("Hour", 12);
		mMinute = sharedPreferences.getInt("Minute", 0);
		mDay = sharedPreferences.getInt("Days", 5);
	}
	
	// Initialize buttons and listeners
	private void createDisplay(){
		// Capture View elements
		mTimeDisplay = (TextView) findViewById(R.id.timeDisplay);
		
		// Set listener and Create actions for the save button
		save = (Button) findViewById(R.id.save);
		save.setOnClickListener(saveOnClickListener);		
		
		// Set listener and Create actions for the Set Time button
		time = (Button) findViewById(R.id.setTimebutton1);
		time.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				showDialog(TIME_DIALOG_ID);
			}
		});
		
		// Set listener and toggle flag based on button click for flashing LED
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
		
		// Set listener and Create actions for the vibration toggle button
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

		// Set listener and Create actions for the Notifications Toggle button
		toggleNotifications = (ToggleButton) findViewById(R.id.notificationtogglebutton);
		toggleNotifications.setChecked(notifOn);
		toggleNotifications.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		        if (toggleNotifications.isChecked()) {
	            	//showDialog(NOTI_DIALOG_ID);					// Future enhancement
	            	notifOn = true;
		        } 
		        else {
		        	notifOn = false;
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
    
    // Selects proper dialog screen to show
    @Override
    protected Dialog onCreateDialog(int id) {
    	Dialog dialog;
    	
    	// Switch case to create proper dialog window
        switch (id) {
        case TIME_DIALOG_ID:
        	// Creates a Time Picker dialog
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
    
    // Creates a dialog screen for day picker for vibrations
    protected Dialog VibrateNumberPickerDialog(Context context){
    	Dialog dialog;
    	dialog = new Dialog(context);
    	
    	// Sets the view to the standard number picker dialog window
		dialog.setContentView(R.layout.numberpicker_dialog);
		
		// Set the title and text to display
		dialog.setTitle("Custom Dialog 2");
		TextView text2 = (TextView) dialog.findViewById(R.id.text);
    	text2.setText("Test2");
    	
    	return dialog;
    }
    
    // Creates a dialog screen for day picker for notifications
    protected Dialog NotificationNumberPickerDialog(Context context){
		Dialog dialog;
		dialog = new Dialog(context);
		
		// Sets the view to the standard number picker dialog window
		dialog.setContentView(R.layout.numberpicker_dialog);
		
		// Set the title and text to display
		dialog.setTitle("Pick Number of Days"); 
		TextView text = (TextView) dialog.findViewById(R.id.text);
    	text.setText("Set the number of days before expiration to begin receiving notifications in the status bar.");
    	//NumberPicker notifday = (NumberPicker) dialog.findViewById(R.id.numpick);					// Future feature

    	return dialog;
    }
    
   
    @Override
    protected void onDestroy() {
        super.onDestroy();
    } 
}