package com.appsolve.acimnew;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AlarmAdapter extends BaseAdapter {
	public static final String DEBUG_TAG = "AlarmAdapter";
	ArrayList<AlarmDetails> alarmList = new ArrayList<AlarmDetails>();
	 private static LayoutInflater inflater=null;
	 private final Context context;
	 ImageButton btn;
	 int position;
	 
    public AlarmAdapter(Context context, ArrayList<AlarmDetails> items) {
		this.context = context;
		alarmList = items;
		inflater = LayoutInflater.from(context);   
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    	String time, strHour, strMinute;
    	int hour, minute;
    	String period; // am or pm
        View vi=convertView;
        final int fixedPosition = position;
        if(convertView==null)
            vi = inflater.inflate(R.layout.notif_item2, null);
        
        CheckBox checkbox = (CheckBox)vi.findViewById(R.id.alarmSet);
        checkbox.setChecked(alarmList.get(position).getState());        
        checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //rowLayout.setBackgroundColor(Color.GRAY);                       
                    alarmList.get(fixedPosition).setState(isChecked);
                    Log.d(DEBUG_TAG, "position :"+Integer.toString(fixedPosition)+" isChecked "+Boolean.toString(isChecked));                    
                    if (isChecked)
                    	setAlarm(alarmList.get(fixedPosition).getTime(),alarmList.get(fixedPosition).getId());
                    else
                    	cancelAlarm(alarmList.get(fixedPosition).getId());                    	
            }          
        });
        TextView title = (TextView) vi.findViewById(R.id.title);
        title.setText(alarmList.get(position).getName());
        TextView timeView = (TextView) vi.findViewById(R.id.time);
        TextView periodView = (TextView) vi.findViewById(R.id.period);
        
        hour = alarmList.get(position).getTime()/3600;
        minute = (alarmList.get(position).getTime()/60)%60;
        
        if (hour >= 12) 
        	period = "pm";
        else
        	period = "am";

        if (hour > 12)
        	hour -= 12;
                
        strHour = Integer.toString(hour);
        if (minute <10) {
        	strMinute = "0";
        	strMinute += Integer.toString(minute);
        }
        else
        	strMinute = Integer.toString(minute);        	
        
		time = strHour + ":" + strMinute;
        timeView.setText(time);       
        periodView.setText(period);

        return vi;
    }

	public int getCount() {
		return alarmList.size();
	}

	public Object getItem(int position) {
		return alarmList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
	
	public void setAlarm(long alarmTime, int  alarmID) {
		long time;
		int hour, minute;
		
		Log.d(DEBUG_TAG, "alarmID :"+Integer.toString(alarmID));
		
		
		hour = (int) (alarmTime/3600);
		minute = (int) ((alarmTime/60)%60);
		
		
		Calendar timeOff = Calendar.getInstance();
		
		Date dat  = new Date();//initializes to now
	    Calendar cal_alarm = Calendar.getInstance();
	    Calendar cal_now = Calendar.getInstance();
	    cal_now.setTime(dat);
	    cal_alarm.setTime(dat);
	    cal_alarm.set(Calendar.HOUR_OF_DAY,hour);//set the alarm time
	    cal_alarm.set(Calendar.MINUTE, minute);
	    cal_alarm.set(Calendar.SECOND,0);
	    if(cal_alarm.before(cal_now))//if its in the past increment
	        cal_alarm.add(Calendar.DATE,1);
		
		
		
	        
		timeOff.set(Calendar.HOUR, (int) (alarmTime/3600));
		timeOff.set(Calendar.MINUTE, (int) ((alarmTime/60)%60));
		
		Log.d(DEBUG_TAG, "Alarm hour :"+Long.toString(alarmTime/3600));
		Log.d(DEBUG_TAG, "Alarm minute :"+Long.toString((alarmTime/60)%60));
		

		
		
		Intent intent = new Intent(context, AlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmID, intent, 0);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
		//alarmManager.set(AlarmManager.RTC_WAKEUP, timeOff.getTimeInMillis(), pendingIntent);
		alarmManager.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
		//alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (10 * 1000), pendingIntent);
		//alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (10 * 1000), (24 * 60 * 60 * 1000), pendingIntent);
		
		
		Log.d(DEBUG_TAG, "timeOff :"+Long.toString(timeOff.getTimeInMillis()));
		Log.d(DEBUG_TAG, "current :"+Long.toString(System.currentTimeMillis()));
		Log.d(DEBUG_TAG, "curr +1 :"+Long.toString(System.currentTimeMillis() + (60 *1000)));
		
		time = timeOff.getTimeInMillis() - System.currentTimeMillis();
		Log.d(DEBUG_TAG, "time :"+Long.toString(time));
		
		
		Toast.makeText(context, "Alarm set for "+hour+":"+minute, Toast.LENGTH_LONG).show();
		
		
		/*
        //Create an offset from the current time in which the alarm will go off.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 5);
 
        //Create a new PendingIntent and add it to the AlarmManager
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
            12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = 
            (AlarmManager)getSystemService(Activity.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                pendingIntent);
        
		Log.d(DEBUG_TAG, "timeOff :"+Long.toString(cal.getTimeInMillis()));
		Log.d(DEBUG_TAG, "current :"+Long.toString(System.currentTimeMillis()));
        */
        Log.d(DEBUG_TAG, "Alarm set");
		
	}
	
	private void cancelAlarm(int  alarmID) {
		Log.d(DEBUG_TAG, "alarmID :"+Integer.toString(alarmID));
		
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
		Intent intent = new Intent(context,AlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmID, intent, 0);
		alarmManager.cancel(pendingIntent);
		pendingIntent.cancel();
		
		Toast.makeText(context, "Alarm canceled ", Toast.LENGTH_LONG).show();
		
	}     
}