package com.appsolve.acimnew;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class AlarmService extends Service {

	public static String TAG = AlarmService.class.getSimpleName();

    public static final String ACIM_PREFS = "Acim_Prefs";
    public static final String TODAYS_LESSON = "Todays_lesson";



    private NotificationManager mNotificationManager;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {



        Log.d(TAG, "onStartCommand");
		
		Intent alarmIntent = new Intent(getBaseContext(), AlarmScreen.class);
		alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		alarmIntent.putExtras(alarmIntent);
		getApplication().startActivity(alarmIntent);



        //displayNotification();

		AlarmManagerHelper.setAlarms(this);
		
		return super.onStartCommand(intent, flags, startId);
    }

    protected void displayNotification() {
        Log.d(TAG, "notification");

        SharedPreferences acimSettings = getSharedPreferences(ACIM_PREFS, Context.MODE_PRIVATE);

      /* Invoking the default notification service */
        NotificationCompat.Builder  mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setContentTitle("Today's lesson:");
        mBuilder.setAutoCancel(true);

        int lessonNo = acimSettings.getInt(TODAYS_LESSON, 1) - 1;

        mBuilder.setContentText(getResources().getStringArray(R.array.lesson_titles)[lessonNo]);
        //mBuilder.setTicker("setTicker");
        mBuilder.setSmallIcon(R.drawable.ic_ab);
        mBuilder.setColor(getResources().getColor(R.color.darkslategray));

      /* Increase notification number every time a new notification arrives */
        // mBuilder.setNumber(++numMessages);


      /* Add Big View Specific Configuration */
        //NotificationCompat.InboxStyle inboxStyle =  new NotificationCompat.InboxStyle();
        NotificationCompat.BigTextStyle bigTextStyle =  new NotificationCompat.BigTextStyle();


        // Sets a title for the Inbox style big view
        //bigTextStyle.setBigContentTitle(test[0]);

        /*if (alert.contains(newline))
            bigTextStyle.bigText("New online booking" + alert.substring(alert.indexOf(newline), alert.length()));
        else*/


        //bigTextStyle.bigText("BigText");
        //mBuilder.setStyle(bigTextStyle);



      /* Creates an explicit intent for an Activity in your app */
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);

        //Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        //mBuilder.setContentIntent(contentIntent);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

      /* notificationID allows you to update the notification later on. */
        mNotificationManager.notify(1, mBuilder.build());
    }
	
}