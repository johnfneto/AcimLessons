package com.appsolve.acimnew;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

public class AlarmFormAdapter extends BaseAdapter {
	public static final String DEBUG_TAG = "acimAdvLog";
	 private static String[] list;
	 AlarmDetails alarmDetails = new AlarmDetails();
	 private static LayoutInflater inflater=null;
	 private final Context context;
	 ImageButton btn;
	 int position;
	 
    public AlarmFormAdapter(Context context, AlarmDetails alarmDetails) {
		this.context = context;
		this.alarmDetails = alarmDetails;
		inflater = LayoutInflater.from(context);   
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    	TextView title, subTitle;
    	String time, strHour, strMinute;
    	int hour, minute;
    	String period; // am or pm
    	final int fixedPosition;
    	String alarmTime;
        View vi=convertView;
        
        fixedPosition = position;

        switch (position) {
		case 0: // Turn alarm on / off
	        //if(convertView==null) vi = inflater.inflate(R.layout.alarm_item1, null);
			vi = inflater.inflate(R.layout.alarm_item1, null);
	        title = (TextView) vi.findViewById(R.id.title);
	        
	        final CheckBox checkbox = (CheckBox)vi.findViewById(R.id.alarmSet);
	        	checkbox.setChecked(alarmDetails.getState());
	        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	            public void onCheckedChanged(CompoundButton buttonView,
	                boolean isChecked) {
	            	alarmDetails.setState(isChecked);
	            }
	          });	
			break;
		/*case 1:  // Set time
	        //if(convertView==null) vi = inflater.inflate(R.layout.alarm_item2, null);
	        vi = inflater.inflate(R.layout.alarm_item2, null);
	        title = (TextView) vi.findViewById(R.id.title);
	        subTitle = (TextView) vi.findViewById(R.id.subTitle);
	        title.setText("Time");
	        
	        hour = alarmDetails.getTime()/3600;
	        minute = (alarmDetails.getTime()/60)%60;
	        
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
	        
			time = strHour + ":" + strMinute+" "+period;
	        subTitle.setText(time);
			break;*/
		case 1:  // Repeat
	        //if(convertView==null) vi = inflater.inflate(R.layout.alarm_item2, null);
	        vi = inflater.inflate(R.layout.alarm_item2, null);
	        title = (TextView) vi.findViewById(R.id.title);
	        title.setText("Repeat");
	        subTitle = (TextView) vi.findViewById(R.id.subTitle);
	        if (alarmDetails.getRepeat() == 0)
	        	subTitle.setText("Su M Tu W Th F Sa");
			break;	
		case 2:  // Ringone
	        //if(convertView==null) vi = inflater.inflate(R.layout.alarm_item2, null);
	        vi = inflater.inflate(R.layout.alarm_item2, null);
	        title = (TextView) vi.findViewById(R.id.title);
	        subTitle = (TextView) vi.findViewById(R.id.subTitle);
	        title.setText("Ringtone");
	        subTitle.setText(alarmDetails.getRingtone());
			break;
		case 3:  //Vibrate
	        //if(convertView==null) vi = inflater.inflate(R.layout.alarm_item1, null);
	        vi = inflater.inflate(R.layout.alarm_item1, null);
	        title = (TextView) vi.findViewById(R.id.title);
	        title.setText("Vibrate");		
	        
	        CheckBox checkbox2 = (CheckBox)vi.findViewById(R.id.alarmSet);
        	checkbox2.setChecked(alarmDetails.getVibrate());
			break;	
		case 4: // Label
			//if(convertView==null) vi = inflater.inflate(R.layout.alarm_item2, null);
			vi = inflater.inflate(R.layout.alarm_item3, null);
			break;			
		default:
			break;
		}
        
        
        
        /*
        CheckBox checkbox = (CheckBox)vi.findViewById(R.id.alarmSet);

        checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //rowLayout.setBackgroundColor(Color.GRAY);                       
            	alarmDetails.setState(isChecked);
                    Log.d(DEBUG_TAG, "position :"+Integer.toString(fixedPosition)+" isChecked "+Boolean.toString(isChecked));
            }               
        });*/
        	

        /*
        time = Integer.toString(alarmList.get(position).getTime()/3600) + ":" 
        		+ Integer.toString((alarmList.get(position).getTime()/60)%60);
        timeView.setText(time);       
        */
        
        
        //!!! and this is the most important part: you are settin listener for the whole row
        //vi.setOnClickListener(new OnItemClickListener(position));
        return vi;
    }

	public int getCount() {
		return 5;
	}

	public Object getItem(int position) {
		Object item;
        switch (position) {
        case 0: // Turn alarm on / off
        	item = alarmDetails.getState();
			break;
		case 1:  // Repeat
			item = alarmDetails.getRepeat();
			break;	
		case 2:  // Ringone
			item = alarmDetails.getRingtone();
			break;
		case 3:  //Vibrate
			item = alarmDetails.getVibrate();
			break;	
		case 4: // Label
			item = alarmDetails.getName();
			break;			
		default:
			item = null;
			break;
		}
		
		
		return item;
	}

	public long getItemId(int position) {
		return position;
	}
}