package co.id.ramadanrizky.todofirebasefix3.broadcast_receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import co.id.ramadanrizky.todofirebasefix3.fragment.HomeFragment;

public class NotificationPublish extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification_id";
    public static String NOTIFICATION = "notification";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d("ONRECEIVE", "CALLED");

        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra(NOTIFICATION);

        int id = intent.getIntExtra(NOTIFICATION_ID, 0);

        manager.notify(id, notification);
    }
}
