package us.food4thought.pantryprotect;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class PantryProtectorLocalService extends Service implements IDebugSwitch{
	public static final String PREFS_NAME = "Options";							// Name of Shared Preferences
	private static final String TAG = "PantryProtectorLocalService";			// Tag for this service, used for logging
	private static final int NOTIF_ID = 1234;									// ID for notification manager
	private static NotificationManager notifManager;							// Notification Manager
	private static Timer timer;													// Timer used for repeating schedules
	private static Date date = new Date();										// Date required for Timer
	private static long day = 86400000;											// The length of one day in milliseconds
	//private static long minute =  60000;										// The length of one minute in milliseconds
	private static int count = 0, mHour = 12, mMinute = 0;						// Initialize counter
																				// Initialize hour and minute for the Date
																				// Notifications
	private static boolean notifOn = true, flashOn = true, vibrateOn = true;	// Initialize flags
	private final IBinder mBinder = new LocalBinder();							// Create a local binder for this service
	private InvDBAdapter adapter = new InvDBAdapter(this);						// Create an adapter to link to database

	// Class to encapsulate the task to run
	private class task extends TimerTask {
			@Override
			public void run(){
				showNotification();
			}
	}
			
	// Returns this service to the activity and allows for binding
	// the activity to this service.
	public class LocalBinder extends Binder{
		PantryProtectorLocalService getservice(){
			return PantryProtectorLocalService.this;
		}
	}
	
	// Called when the service is first created.
	public void onCreate(){
		super.onCreate();
		
		// Create Notification Manager to handle notifications
		notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		Log.d(TAG, "onCreate");
	}
	
	// Called after the service has been created and begins running
	public int onStartCommand(Intent intent, int flags, int startid){
		// Log that the service ran the onStartCommand
		Log.d(TAG, "onStartCommand");
		
		// Notify that the service has started
		if(debug)
			Toast.makeText(this, "Notification Service Started.", Toast.LENGTH_SHORT).show();
		
		// Create a schedule for checking the database
		startschedule();
		
		// START_STICKY allows the service to be toggled on and off
		return START_STICKY;
	}
	
	// Called when the service ends
	@Override
	public void onDestroy(){
		// Log that the service has terminated
		Log.d(TAG, "onDestroy");
		
		// Cancel the persistent notification
		notifManager.cancel(NOTIF_ID);
		
		// Cancel the schedule
		timer.cancel();
		
		// Notify that the service has terminated
		if(debug)
			Toast.makeText(this, "Notification Service Stopped.", Toast.LENGTH_SHORT).show();
	}
	
	// Returns a binder to this service.
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	// Creates the schedule for checking the database.
	private void startschedule(){
		timer = new Timer("notificationTimer", true);
		
		// Create a schedule at a Fixed rate to check the database and create notifications
		timer.scheduleAtFixedRate(new task(), date, day);
	}
	
	// Update local preferences from shared preferences
	private void LoadPreferences(){
		SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		flashOn = sharedPreferences.getBoolean("LED", true);
		vibrateOn = sharedPreferences.getBoolean("Vibrate", true);
		notifOn = sharedPreferences.getBoolean("Notifications", true);
		mHour = sharedPreferences.getInt("Hour", 12);
		mMinute = sharedPreferences.getInt("Minute", 0);
	}
	
	// Checks the database then creates and pushes notifications and alerts to the android.
	public void showNotification(){
		String s;
		count = 0;
		
		// Load the shared preferences
		LoadPreferences();
		
		if (notifOn){
			// Open the database adapter
			adapter.open();
			
			s = adapter.fetchAll(5);
			count = Integer.parseInt(s.substring(2));
			
			// BROKEN: fetch all of the items about to expire
			//l = new ArrayList <Item> (adapter.fetchAll());
			
			// Close the database adapter
			adapter.close();
			
			// If notifications are on
			if(count > 0){
				Notification note = new Notification(R.drawable.icon, "Food Expiring", System.currentTimeMillis());
	
				PendingIntent in = PendingIntent.getActivity(this, 0, new Intent(this, PantryProtectorActivity.class), 0);
	
				note.icon = R.drawable.icon;
				note.tickerText = "Food Expiring";
				note.when = System.currentTimeMillis();
				note.number = count;
				note.flags |= Notification.FLAG_AUTO_CANCEL;
				
				// Add flashing to the notifications
				if (flashOn && s.charAt(1) == '1') {
					// add lights to notifications
					if(debug) Log.i("DEBUG", ">>>>> Flashing. >>>>>");
		            note.flags |= Notification.FLAG_SHOW_LIGHTS;
		            note.ledARGB = Color.CYAN;
		            note.ledOnMS = 500;
		            note.ledOffMS = 500;
		        }
				 
				// Add vibration to notifications
		        if (vibrateOn && s.charAt(0) == '1') {
		            // add vibration to notifications
		        	if (debug) Log.i("DEBUG", ">>>>> ViBrAtInG. >>>>>");
				    note.vibrate = new long[] {100, 200, 200, 200, 200, 200, 1000, 200, 200, 200, 1000, 200};
				}
		        
				note.setLatestEventInfo(this, 
						"Items are about to expire!",
						count + " items are about to expire!" , in);			
				
				notifManager.notify(NOTIF_ID, note);
				
			}
			
			// Debug statement for checking proper data flow
			if (debug && (date.getHours() != mHour || date.getMinutes() != mMinute)){
				System.out.println("Original Schedule: " 
						+ date.getDay() + "," 
						+ date.getHours() + ":" 
						+ date.getMinutes());
				date = new Date();
				date.setHours(mHour);
				date.setMinutes(mMinute);
				timer.cancel();
				System.out.println("Changed Schedule: " 
									+ date.getDay() + "," 
									+ date.getHours() + ":" 
									+ date.getMinutes());
				startschedule();
			}
		}
	}
}
