package com.appsolve.acimnew;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.HashMap;
import java.util.Map;

public class AlarmDetailsActivity extends Activity {

    public final String TAG = this.getClass().getSimpleName();


    private DataBaseHelper dbHelper = new DataBaseHelper(this);

	private AlarmModel alarmDetails;

    HashMap<Integer, Boolean> weekAlarmList = new HashMap<Integer, Boolean>();


    private TimePicker timePicker;
	private EditText edtName;
	private CustomSwitch chkWeekly;
	private CustomSwitch chkSunday;
	private CustomSwitch chkMonday;
	private CustomSwitch chkTuesday;
	private CustomSwitch chkWednesday;
	private CustomSwitch chkThursday;
	private CustomSwitch chkFriday;
	private CustomSwitch chkSaturday;
	private TextView txtToneSelection;

	private CheckBox vibrateCheckBox;

    private TextView sundayText;
    private TextView mondayText;
    private TextView tuesdayText;
    private TextView wednesdayText;
    private TextView thursdayText;
    private TextView fridayText;
    private TextView saturdayText;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_ACTION_BAR);

		setContentView(R.layout.alrm_details_activity);

		getActionBar().setTitle("Create New Alarm");
		getActionBar().setDisplayHomeAsUpEnabled(true);

		timePicker = (TimePicker) findViewById(R.id.alarm_details_time_picker);
		edtName = (EditText) findViewById(R.id.alarm_details_name);
		chkWeekly = (CustomSwitch) findViewById(R.id.alarm_details_repeat_weekly);
		/*chkSunday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_sunday);
		chkMonday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_monday);
		chkTuesday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_tuesday);
		chkWednesday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_wednesday);
		chkThursday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_thursday);
		chkFriday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_friday);
		chkSaturday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_saturday);*/
		txtToneSelection = (TextView) findViewById(R.id.alarm_label_tone_selection);

        sundayText = (TextView) findViewById(R.id.alarm_item_sunday);
        mondayText = (TextView) findViewById(R.id.alarm_item_monday);
        tuesdayText = (TextView) findViewById(R.id.alarm_item_tuesday);
        wednesdayText = (TextView) findViewById(R.id.alarm_item_wednesday);
        thursdayText = (TextView) findViewById(R.id.alarm_item_thursday);
        fridayText = (TextView) findViewById(R.id.alarm_item_friday);
        saturdayText = (TextView) findViewById(R.id.alarm_item_saturday);

        vibrateCheckBox = (CheckBox) findViewById(R.id.vibrateCheckBox);


		long id = getIntent().getExtras().getLong("id");

		if (id == -1) {
			alarmDetails = new AlarmModel();
		} else {
			alarmDetails = dbHelper.getAlarm(id);

			timePicker.setCurrentMinute(alarmDetails.timeMinute);
			timePicker.setCurrentHour(alarmDetails.timeHour);

			edtName.setText(alarmDetails.name);

			chkWeekly.setChecked(alarmDetails.repeatWeekly);
			//chkSunday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.SUNDAY));
            weekAlarmList.put(AlarmModel.SUNDAY, alarmDetails.getRepeatingDay(AlarmModel.SUNDAY));
			//chkMonday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.MONDAY));
            weekAlarmList.put(AlarmModel.MONDAY, alarmDetails.getRepeatingDay(AlarmModel.MONDAY));
			//chkTuesday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.TUESDAY));
            weekAlarmList.put(AlarmModel.TUESDAY, alarmDetails.getRepeatingDay(AlarmModel.TUESDAY));
			//chkWednesday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.WEDNESDAY));
            weekAlarmList.put(AlarmModel.WEDNESDAY, alarmDetails.getRepeatingDay(AlarmModel.WEDNESDAY));
			//chkThursday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.THURSDAY));
            weekAlarmList.put(AlarmModel.THURSDAY, alarmDetails.getRepeatingDay(AlarmModel.THURSDAY));
			//chkFriday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.FRDIAY));
            weekAlarmList.put(AlarmModel.FRDIAY, alarmDetails.getRepeatingDay(AlarmModel.FRDIAY));
			//chkSaturday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.SATURDAY));
            weekAlarmList.put(AlarmModel.SATURDAY, alarmDetails.getRepeatingDay(AlarmModel.SATURDAY));


            updateTextColor(sundayText, alarmDetails.getRepeatingDay(AlarmModel.SUNDAY));
            updateTextColor(mondayText, alarmDetails.getRepeatingDay(AlarmModel.MONDAY));
            updateTextColor(tuesdayText, alarmDetails.getRepeatingDay(AlarmModel.TUESDAY));
            updateTextColor(wednesdayText, alarmDetails.getRepeatingDay(AlarmModel.WEDNESDAY));
            updateTextColor(thursdayText, alarmDetails.getRepeatingDay(AlarmModel.THURSDAY));
            updateTextColor(fridayText,alarmDetails.getRepeatingDay(AlarmModel.FRDIAY));
            updateTextColor(saturdayText, alarmDetails.getRepeatingDay(AlarmModel.SATURDAY));


            vibrateCheckBox.setChecked(alarmDetails.alarmVibrate);

			txtToneSelection.setText(RingtoneManager.getRingtone(this, alarmDetails.alarmTone).getTitle(this));
		}

		final LinearLayout ringToneContainer = (LinearLayout) findViewById(R.id.alarm_ringtone_container);
		ringToneContainer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
				//startActivityForResult(intent , 1);

                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                startActivityForResult(intent, 1);
			}
		});


        final RelativeLayout repeatContainer = (RelativeLayout) findViewById(R.id.repeat_container);
        repeatContainer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

                final CharSequence[] items = getResources().getStringArray(R.array.days);



                if (weekAlarmList.isEmpty())
                    for (int i = 0; i < 7; i++)
                        weekAlarmList.put(i, false);

                final boolean [] selected = {weekAlarmList.get(0), weekAlarmList.get(1), weekAlarmList.get(2), weekAlarmList.get(3), weekAlarmList.get(4), weekAlarmList.get(5), weekAlarmList.get(6)};

                AlertDialog.Builder builder = new AlertDialog.Builder(AlarmDetailsActivity.this);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        updateTextColor(sundayText, weekAlarmList.get(AlarmModel.SUNDAY));
                        updateTextColor(mondayText, weekAlarmList.get(AlarmModel.MONDAY));
                        updateTextColor(tuesdayText, weekAlarmList.get(AlarmModel.TUESDAY));
                        updateTextColor(wednesdayText, weekAlarmList.get(AlarmModel.WEDNESDAY));
                        updateTextColor(thursdayText, weekAlarmList.get(AlarmModel.THURSDAY));
                        updateTextColor(fridayText, weekAlarmList.get(AlarmModel.FRDIAY));
                        updateTextColor(saturdayText, weekAlarmList.get(AlarmModel.SATURDAY));

                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setTitle("Repeat")
                        .setMultiChoiceItems(items, selected, new DialogInterface.OnMultiChoiceClickListener() {
                            public void onClick(DialogInterface dialogInterface, int item, boolean b) {
                                Log.d("Myactivity", String.format("%s: %s", items[item], b));


                                weekAlarmList.put(item, b);
                            }
                        });

                builder.create().show();

			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
        Uri toneUri;

		if (resultCode == RESULT_OK) {
	        switch (requestCode) {
		        case 1: {
                    Log.d(TAG, "EXTRA_RINGTONE_PICKED_URI :" + data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI).toString());
                    alarmDetails.alarmTone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
		        	txtToneSelection.setText(RingtoneManager.getRingtone(this, alarmDetails.alarmTone).getTitle(this));
		            break;
		        }
		        default: {
		            break;
		        }
	        }
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.alarm_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case android.R.id.home: {
				finish();
				break;
			}
			case R.id.action_save_alarm_details: {
				updateModelFromLayout();

				AlarmManagerHelper.cancelAlarms(this);

				if (alarmDetails.id < 0) {
					dbHelper.createAlarm(alarmDetails);
				} else {
					dbHelper.updateAlarm(alarmDetails);
				}

				AlarmManagerHelper.setAlarms(this);

				Intent newIntent = new Intent();
				setResult(RESULT_OK, newIntent);
				finish();
			}
		}

		return super.onOptionsItemSelected(item);
	}

	private void updateModelFromLayout() {
		alarmDetails.timeMinute = timePicker.getCurrentMinute().intValue();
		alarmDetails.timeHour = timePicker.getCurrentHour().intValue();
		alarmDetails.name = edtName.getText().toString();
		alarmDetails.repeatWeekly = chkWeekly.isChecked();
		/*alarmDetails.setRepeatingDay(AlarmModel.SUNDAY, chkSunday.isChecked());
		alarmDetails.setRepeatingDay(AlarmModel.MONDAY, chkMonday.isChecked());
		alarmDetails.setRepeatingDay(AlarmModel.TUESDAY, chkTuesday.isChecked());
		alarmDetails.setRepeatingDay(AlarmModel.WEDNESDAY, chkWednesday.isChecked());
		alarmDetails.setRepeatingDay(AlarmModel.THURSDAY, chkThursday.isChecked());
		alarmDetails.setRepeatingDay(AlarmModel.FRDIAY, chkFriday.isChecked());
		alarmDetails.setRepeatingDay(AlarmModel.SATURDAY, chkSaturday.isChecked());*/
		alarmDetails.isEnabled = true;
		alarmDetails.alarmVibrate = vibrateCheckBox.isChecked();



        for (Map.Entry<Integer, Boolean> entry : weekAlarmList.entrySet()) {
            Integer key = entry.getKey();
            Boolean value = entry.getValue();

            alarmDetails.setRepeatingDay(key, value);
        }
	}

    private void updateTextColor(TextView view, boolean isOn) {
        if (isOn) {
            view.setTextColor(Color.BLACK);
        } else {
            view.setTextColor(Color.LTGRAY);
        }
    }

}
