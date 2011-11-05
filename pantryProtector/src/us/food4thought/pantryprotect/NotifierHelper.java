package us.food4thought.pantryprotect;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

public class NotifierHelper implements IDebugSwitch {
    private static final int NOTIFY_1 = 0x1001;
    public static void sendNotification(Activity caller, Class<?> activityToLaunch, String title, String msg, int numberOfEvents, boolean flashLed, boolean vibrate) {
        NotificationManager notifier = (NotificationManager) caller.getSystemService(Context.NOTIFICATION_SERVICE);
        // Create this outside the button so we can increment the number drawn over the notification icon.
        // This indicates the number of alerts for this event.
        final Notification notify = new Notification(R.drawable.icon, "", System.currentTimeMillis());
        notify.icon = R.drawable.icon;
        notify.tickerText = "New Alerts from Your App!";
        notify.when = System.currentTimeMillis();
        notify.number = numberOfEvents;
        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        if (flashLed) {
        // add lights
            notify.flags |= Notification.FLAG_SHOW_LIGHTS;
            notify.ledARGB = Color.CYAN;
            notify.ledOnMS = 500;
            notify.ledOffMS = 500;
        }
 
        if (vibrate) {
            // add vibs
            if (debug) Log.i("DEBUG", ">>>>> ViBrAtInG. >>>>>");
            notify.vibrate = new long[] {100, 200, 200, 200, 200, 200, 1000, 200, 200, 200, 1000, 200};
        }
        Intent toLaunch = new Intent(caller, activityToLaunch);
        PendingIntent intentBack = PendingIntent.getActivity(caller, 0, toLaunch, 0);
        notify.setLatestEventInfo(caller, title, msg, intentBack);
        notifier.notify(NOTIFY_1, notify);
    }
    public static void clear(Activity caller) {
        NotificationManager notifier = (NotificationManager) caller.getSystemService(Context.NOTIFICATION_SERVICE);
        notifier.cancelAll();
    }
}
