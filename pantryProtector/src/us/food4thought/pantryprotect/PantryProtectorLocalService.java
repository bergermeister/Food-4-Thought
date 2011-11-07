package us.food4thought.pantryprotect;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class PantryProtectorLocalService extends Service implements IDebugSwitch{
	private static final String TAG = "PantryProtectorLocalService";
	private static final int NOTIF_ID = 1234;
	private static NotificationManager notifManager;
	private static Timer timer = new Timer();
	private static Date date = new Date();
	private static long day = 86400000;
	private static long minute =  60000;
	private static int count = 0;
	private static boolean notifOn = true, flashOn = true, vibrateOn = true;
	private final IBinder mBinder = new LocalBinder();
	private InvDBAdapter adapter = new InvDBAdapter(this);
	private ArrayList <Item> l = new ArrayList<Item>();


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
		Log.d(TAG, "onStartCommand");
		
		Toast.makeText(this, "Notification Service Started.", Toast.LENGTH_SHORT).show();
		
		// Begin checking the database
		startschedule();
		
		// START_STICKY allows the service to be toggled on and off
		return START_STICKY;
	}
	
	@Override
	public void onDestroy(){
		Log.d(TAG, "onDestroy");
		// Cancel the persistent notification
		notifManager.cancel(NOTIF_ID);
		timer.cancel();
		Toast.makeText(this, "Notification Service Stopped.", Toast.LENGTH_SHORT).show();
	}
	
	// Returns a binder to this service.
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	// Creates the schedule for checking the database.
	private void startschedule(){
		// Create a schedule at a Fixed rate to create notifications
		timer.scheduleAtFixedRate(new TimerTask(){
			@Override
			public void run(){
				showNotification();
			}}, date, minute/15);
	}
		
	// Checks the database then creates and pushes notifications and alerts to the android.
	public void showNotification(){
		adapter.open();
		l = adapter.fetchAll();
		adapter.close();
		count = l.size();
		if(notifOn){
			
			//NotifierHelper.sendNotification(notifManager, PantryProtectorActivity.class, "Items about to expire!", "/"+count, count, true, true);
			
			//count++;
			Notification note = new Notification(R.drawable.icon, "Food Expiring", System.currentTimeMillis());

			PendingIntent in = PendingIntent.getActivity(this, 0, new Intent(this, PantryProtectorActivity.class), 0);

			note.icon = R.drawable.icon;
			note.tickerText = "Food Expiring";
			note.when = System.currentTimeMillis();
			note.number = count;
			note.flags |= Notification.FLAG_AUTO_CANCEL;
			if (flashOn) {
				// add lights
	            note.flags |= Notification.FLAG_SHOW_LIGHTS;
	            note.ledARGB = Color.CYAN;
	            note.ledOnMS = 500;
	            note.ledOffMS = 500;
	        }
			 
	        if (vibrateOn) {
	            // add vibs
	        	if (debug) Log.i("DEBUG", ">>>>> ViBrAtInG. >>>>>");
			    note.vibrate = new long[] {100, 200, 200, 200, 200, 200, 1000, 200, 200, 200, 1000, 200};
			}
	        
			note.setLatestEventInfo(this, 
					count + " items about to expire!",
					count + " items expiring!" , in);			
			
			notifManager.notify(NOTIF_ID, note);
			
		}
	}
}
