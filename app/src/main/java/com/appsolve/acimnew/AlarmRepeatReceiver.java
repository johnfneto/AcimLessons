package com.appsolve.acimnew;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AlarmRepeatReceiver extends BroadcastReceiver {
    public static final String ACIM_PREFS = "Acim_Prefs";
    public static final String TODAYS_LESSON = "Todays_lesson";
    public static final String REPEAT_MESSAGE = "repeat_message";



    private NotificationManager mNotificationManager;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.d("AlarmReceiver - ", "onReceive");

        displayNotification();


    }

    protected void displayNotification() {
        Log.i("Start", "notification");

        SharedPreferences acimSettings = context.getSharedPreferences(ACIM_PREFS, Context.MODE_PRIVATE);

      /* Invoking the default notification service */
        NotificationCompat.Builder  mBuilder = new NotificationCompat.Builder(context);

        mBuilder.setContentTitle("acim Reminder");
        mBuilder.setAutoCancel(true);

        int lessonNo = acimSettings.getInt(TODAYS_LESSON, 1) - 1;
        String msg = acimSettings.getString(REPEAT_MESSAGE, "");

        if (msg.isEmpty())
            mBuilder.setContentText(context.getResources().getStringArray(R.array.lesson_exercises)[lessonNo]);
        else
            mBuilder.setContentText(msg);
        //mBuilder.setTicker("setTicker");
                mBuilder.setSmallIcon(R.drawable.ic_ab);
        mBuilder.setColor(context.getResources().getColor(R.color.darkslategray));

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
        /*Intent resultIntent = new Intent(this, MainActivity.class);
        if (type.equals("review"))
            resultIntent.putExtra("reviews", "true");
        else
            resultIntent.putExtra("inbox", "true");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);

        //Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        //PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);*/

        //mBuilder.setContentIntent(resultPendingIntent);
        //mBuilder.setContentIntent(contentIntent);

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

      /* notificationID allows you to update the notification later on. */
        mNotificationManager.notify(1, mBuilder.build());
    }


    /**
     * Remove the app's notification
     */
    private void clearNotification()
    {
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(1);
    }
}