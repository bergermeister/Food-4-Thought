package us.food4thought.pantryprotect;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class PantryProtectorLocalService extends Service {
	private static final String TAG = "PantryProtectorLocalService";
	private static NotificationManager notifManager;
	private static final int NOTIF_ID = 1234;
	private Timer timer = new Timer();
	private static int time = 60000;
	
	@Override
	public void onCreate(){
		super.onCreate();
		
		// Create Notification Manager to handle notifications
		notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		Log.d(TAG, "onCreate");
		startservice();
	}
	
	@Override
	public void onDestroy(){
		Log.d(TAG, "onDestroy");
	}
	
	@Override
	public void onStart(Intent intent, int startid){
		Log.d(TAG, "onStart");
	}
	
	private void startservice(){
		timer.scheduleAtFixedRate(new TimerTask(){
			@Override
			public void run(){
				notice();
			}}, 0, time);
	}
	
	public void notice(){
		if(true){
			Notification note = new Notification(R.drawable.icon, "New E-mail", System.currentTimeMillis());
			
			PendingIntent in = PendingIntent.getActivity(this, 0, new Intent(this, PantryProtectorActivity.class), 0);
			
			note.setLatestEventInfo(this, "New E-mail", "You have bleh", in);
			
			notifManager.notify(NOTIF_ID, note);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}