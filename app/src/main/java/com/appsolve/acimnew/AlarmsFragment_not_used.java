package com.appsolve.acimnew;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by johnn on 15/12/2014.
 */
public class AlarmsFragment_not_used extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AlarmDetails alarmDetails = new AlarmDetails();
        ListView listView;
        final MainActivity mActivity =	(MainActivity) this.getActivity();

        View view = inflater.inflate(R.layout.alarmform, container, false);

        Bundle b = getArguments();
        alarmDetails.setId(b.getInt("id"));
        alarmDetails.setName(b.getString("name"));
        alarmDetails.setState(b.getBoolean("state"));
        alarmDetails.setTime(b.getInt("time"));
        alarmDetails.setRepeat(b.getInt("repeat"));
        alarmDetails.setRingtone(b.getString("ringtone"));
        alarmDetails.setVibrate(b.getBoolean("vibrate"));

        listView = (ListView)view.findViewById(R.id.list);
        AlarmFormAdapter adapter = new AlarmFormAdapter(getActivity(), alarmDetails);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
                switch (pos) {
                    case 0: // Turn alarm on

                        break;
	        		/*case 1:  // Set time
	        			//showDialog(alarmDetails.getTime());
	        			break;*/
                    case 1:  // Repeat

                        /*AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                        builder.setTitle("Repeat");
                        builder.setMultiChoiceItems(mActivity.getResources().getStringArray(R.array.days), )


                        AlertDialog alert = builder.create();
                        alert.show();*/


                        final CharSequence[] items = mActivity.getResources().getStringArray(R.array.days);
                        final boolean [] selected = {true, false, true, false, false, false, false};

                        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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
                                    }
                                });

                        builder.create().show();

	        			/*final CharSequence[] items = {"Red", "Green", "Blue"};
	        			Log.d(TAG, "mActivity.getResources().getStringArray(R.array.days) :"+mActivity.getResources().getStringArray(R.array.days).toString());
        		    	final ArrayList<Integer> mSelectedItems = new ArrayList<Integer>();  // Where we track the selected items
        		        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        		        builder.setCancelable(false); // This blocks the 'BACK' button
        		        builder.setMessage("Repeat");
        		        builder.setMultiChoiceItems(R.array.days, null,
        		                new DialogInterface.OnMultiChoiceClickListener() {
	        		         @Override
	        		         public void onClick(DialogInterface dialog, int which,
	        		                 boolean isChecked) {
	        		             if (isChecked) {
	        		                 // If the user checked the item, add it to the selected items
	        		                 mSelectedItems.add(which);
	        		             } else if (mSelectedItems.contains(which)) {
	        		                 // Else, if the item is already in the array, remove it
	        		                 mSelectedItems.remove(Integer.valueOf(which));
	        		             }
	        		         }
	        		        });
        		        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        		            @Override
        		            public void onClick(DialogInterface dialog, int which) {
        		            	dialog.dismiss();
        		            }
        		        });
        		        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        		            @Override
        		            public void onClick(DialogInterface dialog, int which) {
        		            	dialog.dismiss();
        		            }
        		        });
        		        AlertDialog dialog = builder.show();
        		        TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
        		        messageView.setGravity(Gravity.CENTER);*/
                        break;
                    case 2:  // Ringone
                        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                        getActivity().startActivityForResult(intent, 5);
                        break;
                    case 3:  //Vibrate


                        break;
                    case 4: // Label


                        break;
                    default:
                        break;
                }

            }
        });
        getActivity().setTitle("Set Alarm");
        return view;
    }
}
