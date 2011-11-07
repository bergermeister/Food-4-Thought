package us.food4thought.pantryprotect;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class PantryProtectorLocalService extends Service{
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
	private static InvDBAdapter adapter;
	private static Long mRowId;
	private List <Item> l;


	// Returns this service to the activity and allows for binding
	// the activity to this service.
	public class LocalBinder extends Binder{
		PantryProtectorLocalService getservice(){
			return PantryProtectorLocalService.this;
		}
	}
	
	// Called when the service is first created.
	public void onCreate(Bundle bundle){
		super.onCreate();
		
		// Create Notification Manager to handle notifications
		notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		adapter = new InvDBAdapter(this);
		adapter.open();
		mRowId = (bundle == null) ? null : (Long) bundle.getSerializable(InvDBAdapter.KEY_ROWID);
		
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
		
		if(notifOn){
			count++;
			Notification note = new Notification(R.drawable.icon, "Food Expiring", System.currentTimeMillis());
			
			PendingIntent in = PendingIntent.getActivity(this, 0, new Intent(this, PantryProtectorActivity.class), 0);
			//NotifierHelper.sendNotification(, PantryProtectorActivity.class, "Items about to expire!", s1+"/"+s+"/"+count, count, true, true);
			
			note.setLatestEventInfo(this, 
					count + " items about to expire!",
					count + " items expiring!" /*+ from[0] + from[1]*/, in);			

			notifManager.notify(NOTIF_ID, note);
			
		}
	}
}
