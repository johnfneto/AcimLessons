package com.appsolve.acimnew;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AlarmReceiver extends BroadcastReceiver   {
	public static final String DEBUG_TAG = "acimAdvLog";
	public static final String ACIM_PREFS = "Acim_Prefs";
    public static final String TODAYS_LESSON = "Todays_lesson";
    public static final String LAST_DATE = "Last_date";
	public static final int NOTIFICATION_ID = 1;
	SharedPreferences acimSettings;
	
	public void onReceive(Context context, Intent intent) {
	Toast.makeText(context, "Alarm worked.", Toast.LENGTH_LONG).show();
	Log.d(DEBUG_TAG, "alarmOff.................. :");
	

	createNotification(context);
	}
	
	public void createNotification(Context context) {
		final String strLessonNo;
		String lessonTitle = null;
		
		Intent intent = new Intent(context, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // Retrieve the shared preferences
        acimSettings = context.getSharedPreferences(ACIM_PREFS, Context.MODE_WORLD_WRITEABLE | Context.MODE_WORLD_READABLE);		
        if (acimSettings.contains(TODAYS_LESSON) && 
        		((Integer.parseInt(acimSettings.getString(TODAYS_LESSON, "")) > 0 && 
        				Integer.parseInt(acimSettings.getString(TODAYS_LESSON, "")) < 366))) {
        	strLessonNo = acimSettings.getString(TODAYS_LESSON, "");
        } else {
   
        	strLessonNo = "1";
        } 
        
        
        String FileName = "lesson" + strLessonNo;
        
        // Read raw file into string and populate TextView
        
        Resources res = context.getResources();
        int fileId = res.getIdentifier(FileName, "raw", context.getPackageName());
        InputStream iFile = context.getResources().openRawResource(fileId);
        try {           
        	lessonTitle = readLessonTitle(iFile);

        } catch (Exception e) {
            Log.e(DEBUG_TAG, "InputStreamToString failure", e);
        }
        
        Log.d(DEBUG_TAG, "lessonTitle :"+lessonTitle);
        
        Bundle b = new Bundle();
        b.putString("key", strLessonNo); //Your id
        b.putBoolean("alarm", true);
        intent.putExtras(b); //Put your id to your next Intent
		
		NotificationManager manger = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher, "Your lesson is waiting", System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
		//PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		
		notification.setLatestEventInfo(context, "Lesson "+strLessonNo, lessonTitle, contentIntent);
		//notification.flags = Notification.FLAG_INSISTENT;

		
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE; 
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.sound = (Uri) intent.getParcelableExtra("Ringtone");
		//notification.vibrate = (long[]) intent.getExtras().get("vibrationPatern");

		// The PendingIntent to launch our activity if the user selects this notification
		manger.notify(NOTIFICATION_ID, notification);
	}
	
    public String readLessonTitle(InputStream is) throws IOException {
    	boolean b = false; 
    	int start, end;
    	String tmp1, tmp2, lessonTitle;
    	    	
        StringBuffer sBuffer = new StringBuffer();
        DataInputStream dataIO = new DataInputStream(is);
        String strLine = null;
        
        while ((strLine = dataIO.readLine()) != null || !b) {
        	if (strLine.contains("<b>")) 
        		sBuffer.append(strLine);
        	if (strLine.contains("<b>") && strLine.contains("</b>"))
        		b =true;
        	else
        		if (strLine.contains("</b>")) {
        			sBuffer.append(strLine);
        			b = true;
        		}        	
        }
        dataIO.close();
        is.close();
        
        //<b>I am never upset for the reason I think.</b>        
        tmp1 = sBuffer.toString();        
        start = tmp1.indexOf("<b>");                 
        tmp2 = tmp1.substring(start+3); // tmp2 = everything after <b>
        end = tmp2.indexOf("</b>");        
        lessonTitle = tmp2.substring(0,end); // lessonTitle = evrything from beging till </b>
                
        return lessonTitle;
    }	
}
