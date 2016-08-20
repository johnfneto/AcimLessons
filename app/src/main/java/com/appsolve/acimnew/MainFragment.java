package com.appsolve.acimnew;

/**
 * Created by johnn on 3/07/15.
 */

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Fragment that appears in the "content_frame"
 */
public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";
    public static final String ACIM_PREFS = "Acim_Prefs";
    public static final String TODAYS_LESSON = "Todays_lesson";
    public static final String TEXT_SCROLL = "Text_scroll";
    public static final String TEXT_CHAPTER = "Text_chapter";
    public static final String TEXT_SECTION = "Text_section";
    public static final String LAST_DATE = "Last_date";
    public static final String ADVANCE_LESSON = "Advance_lesson";

    public static final String REPEAT_EVERY = "repeat_every";
    public static final String REPEAT_ALARM_ON = "repeat_alarm_on";
    public static final String REPEAT_MESSAGE = "repeat_message";
    public static final String REPEAT_START_TIME = "repeat_start_time";
    public static final String REPEAT_FINISH_TIME = "repeat_finish_time";

    static DateFormat outputhhmmFormat = new SimpleDateFormat("h:mm a");
    static DateFormat outputHFormat = new SimpleDateFormat("H");
    static DateFormat outputMFormat = new SimpleDateFormat("m");
    public static final String ARG_FRAGMENT_NUMBER = "planet_number";

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    MainActivity mActivity;

    static AlarmListAdapter mAdapter;

    public MainFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear(); //Clear view of previous menu
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.alarms, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int i = getArguments().getInt(ARG_FRAGMENT_NUMBER);
        String menuItem;
        WebView webView;

        mActivity =	(MainActivity) this.getActivity();


        View rootView = null;

        switch (i) {
            case 0:  // Lessons
                Date currentDate = null;
                try {
                    currentDate = sdf.parse(sdf.format(new Date()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                SharedPreferences acimSettings;
                Boolean advanceLesson = false;
                int lessonNo = 1;

                acimSettings = getActivity().getSharedPreferences(ACIM_PREFS, Context.MODE_PRIVATE);
                if (acimSettings.contains(ADVANCE_LESSON))
                    advanceLesson = acimSettings.getBoolean(ADVANCE_LESSON, false);
                if (advanceLesson) {
                    if (acimSettings.contains(TODAYS_LESSON))
                        lessonNo = acimSettings.getInt(TODAYS_LESSON, 1);

                    if (acimSettings.contains(LAST_DATE)) {
                        Date lastDate = null;
                        try {
                            lastDate = sdf.parse(acimSettings.getString(LAST_DATE, ""));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (currentDate.after(lastDate)) {
                            if (lessonNo == 365)
                                lessonNo = 1;
                            else
                                lessonNo++;
                            SharedPreferences.Editor editor = acimSettings.edit();
                            editor.putInt(TODAYS_LESSON, lessonNo);
                            editor.putString(LAST_DATE, sdf.format(currentDate));
                            editor.commit();
                        }
                    }
                }

                mActivity.searchText = null;
                Bundle bundle = new Bundle();
                bundle.putInt("lessonNo", lessonNo);
                MainActivity.LessonFragment fragment = new MainActivity.LessonFragment();
                //fragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                break;
            case 1:  // Text
                getActivity().getActionBar().setSubtitle(null);
                rootView = inflater.inflate(R.layout.fragment_text, container, false);
                ExpandableListView lstView = (ExpandableListView) rootView.findViewById(R.id.expList);
                MyExpandableListAdapter adpt = new MyExpandableListAdapter(getActivity(), lstView, mActivity.lstModel);
                lstView.setDividerHeight(0);
                lstView.setAdapter(adpt);
                lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d(TAG, "position : " + position);

                    }
                });
                lstView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                        Log.d(TAG, "groupPosition :" + groupPosition + "  childPosition :" + childPosition);

                        Bundle bundle = new Bundle();
                        bundle.putInt("groupPosition", groupPosition);
                        bundle.putInt("childPosition", childPosition);
                        MainActivity.TextFragment fragment = new MainActivity.TextFragment();
                        fragment.setArguments(bundle);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                        return false;
                    }
                });

                menuItem = getResources().getStringArray(R.array.menu_array)[i];
	            /*
	            webView = (WebView)rootView.findViewById(R.id.webView);
	            webView.loadUrl("file:///android_asset/text2.html");*/

                mActivity.searchText = null;
                getActivity().setTitle(menuItem);
                break;
            case 2:    // Manual for Teachers
                getActivity().getActionBar().setSubtitle(null);
                rootView = inflater.inflate(R.layout.fragment_manual, container, false);

                final ArrayList<String> chapList = new ArrayList<String>();
                ListView listView;

                //Reads the entries on acimchapters file (which contains list of Chapters)
                // and loads it to chapList
                InputStream iFile = getResources().openRawResource(R.raw.manualchapters);
                try {

                    DataInputStream dataIO = new DataInputStream(iFile);
                    String strLine = null;
                    while ((strLine = dataIO.readLine()) != null) {
                        if (!strLine.equals("Introduction"))
                            strLine = strLine.substring(8);
                        chapList.add(strLine);
                    }
                    dataIO.close();
                    iFile.close();

                } catch (Exception e) {
                    Log.e(TAG, "InputStreamToString failure1", e);
                }


                listView = (ListView) rootView.findViewById(R.id.list);
                ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.list_item2, chapList);
                listView.setAdapter(adapter);
                listView.setDividerHeight(0);

                listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
                        Log.d(TAG, "pos :" + pos);


                        Bundle bundle = new Bundle();
                        bundle.putString("chapter", chapList.get(pos));
                        MainActivity.ManualFragment fragment = new MainActivity.ManualFragment();
                        fragment.setArguments(bundle);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                    }
                });
                mActivity.searchText = null;
                break;
            case 3: // My Alarms
                getActivity().getActionBar().setSubtitle(null);
                setHasOptionsMenu(true);
                final SharedPreferences sharedPrefs = getActivity().getSharedPreferences(ACIM_PREFS, Context.MODE_PRIVATE);
                rootView = inflater.inflate(R.layout.notification, container, false);

                ListView AlarmsListView = (ListView) rootView.findViewById(R.id.alarmsList);
                mAdapter = new AlarmListAdapter(getActivity(), mActivity.dbHelper.getAlarms());
                AlarmsListView.setAdapter(mAdapter);
                AlarmsListView.setDivider(this.getResources().getDrawable(android.R.color.transparent));
                AlarmsListView.setCacheColorHint(0);


                /*listView2 = (ListView) rootView.findViewById(R.id.alarmsList);
                final ArrayList<AlarmDetails> alarmList = mActivity.getAlarms();
                AlarmListAdapter mAdapter = new AlarmListAdapter(getActivity(), mActivity.dbHelper.getAlarms());
                listView2.setAdapter(mAdapter);
                listView2.setDivider(this.getResources().getDrawable(android.R.color.transparent));
                listView2.setCacheColorHint(0);
                listView2.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                        Bundle b = new Bundle();
                        b.putInt("id", alarmList.get(position).getId());
                        b.putString("name", alarmList.get(position).getName());
                        b.putBoolean("state", alarmList.get(position).getState());
                        b.putInt("time", alarmList.get(position).getTime());
                        b.putInt("repeat", alarmList.get(position).getRepeat());
                        b.putString("ringtone", alarmList.get(position).getRingtone());
                        b.putBoolean("vibrate", alarmList.get(position).getVibrate());

                        AlarmsFragment fragment = new AlarmsFragment();
                        fragment.setArguments(b);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment)
                                .addToBackStack(null)
                                .commit();
                    }
                });*/


                final TextView repeatText = (TextView) rootView.findViewById(R.id.repeatText);
                final ImageView msgView = (ImageView) rootView.findViewById(R.id.msgView);

                repeatText.setText("Repeat every " + getResources().getStringArray(R.array.repeat_times)[sharedPrefs.getInt(REPEAT_EVERY, 0)]);
                repeatText.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Log.d(TAG, "Repeat");
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Repeat every:")
                                .setSingleChoiceItems(R.array.repeat_times, -1, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Log.d(TAG, "Repeat every :" + getResources().getStringArray(R.array.repeat_times)[which]);
                                        repeatText.setText("Repeat every " + getResources().getStringArray(R.array.repeat_times)[which]);
                                        sharedPrefs.edit().putInt(REPEAT_EVERY, which).commit();
                                        dialog.dismiss();
                                    }
                                });
                        builder.create().show();
                    }
                });

                /* Retrieve a PendingIntent that will perform a broadcast */
                Intent alarmIntent = new Intent(getActivity(), AlarmRepeatReceiver.class);
                final PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmIntent, 0);

                CheckBox checkBox = (CheckBox) rootView.findViewById(R.id.checkBox1);
                Log.d(TAG, "sharedPrefs.getBoolean(REPEAT_ALARM_ON, false) :" + sharedPrefs.getBoolean(REPEAT_ALARM_ON, false));
                checkBox.setChecked(sharedPrefs.getBoolean(REPEAT_ALARM_ON, false));
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (buttonView.isChecked()) {
                            AlarmManager manager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                            int interval = getResources().getIntArray(R.array.repeat_times_min)[sharedPrefs.getInt(REPEAT_EVERY, 0)] * 1000 * 60;
                            Log.d(TAG, "interval :" + getResources().getIntArray(R.array.repeat_times_min)[sharedPrefs.getInt(REPEAT_EVERY, 0)]);
                            manager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + interval, interval, pendingIntent);
                            sharedPrefs.edit().putBoolean(REPEAT_ALARM_ON, true).commit();
                        } else {
                            AlarmManager manager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                            manager.cancel(pendingIntent);
                            sharedPrefs.edit().putBoolean(REPEAT_ALARM_ON, false).commit();
                        }
                    }
                });

                msgView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        LayoutInflater layoutInflater = mActivity.getLayoutInflater();
                        View promptView = layoutInflater.inflate(R.layout.form_dialog, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
                        alertDialogBuilder.setView(promptView);
                        ((TextView) promptView.findViewById(R.id.title)).setText("Please type message:");
                        final EditText msgText = (EditText) promptView.findViewById(R.id.subjectText);
                        if (sharedPrefs.getString(REPEAT_MESSAGE, "").equals(""))
                            msgText.setText(mActivity.getResources().getStringArray(R.array.lesson_exercises)[sharedPrefs.getInt(TODAYS_LESSON, 1) - 1]);
                        else
                            msgText.setText(sharedPrefs.getString(REPEAT_MESSAGE, ""));
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        sharedPrefs.edit().putString(REPEAT_MESSAGE, msgText.getText().toString()).commit();
                                    }
                                })
                                .setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                        alertDialogBuilder.show();
                    }
                });
                break;
            case 4:  // Find a Lesson
                getActivity().getActionBar().setSubtitle(null);
                rootView = inflater.inflate(R.layout.fragment_find_lesson, container, false);

                final List<String> lessonsList = new ArrayList<String>();
                ListView listView3;

                final EditText editLesson = (EditText) rootView.findViewById(R.id.editText);
                Button okButton = (Button) rootView.findViewById(R.id.okButton);

                okButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        String strLessonNo = editLesson.getText().toString();
                        if (!strLessonNo.equals("")) {
                            if (Integer.parseInt(strLessonNo) < 1 ||
                                    Integer.parseInt(strLessonNo) > 365) {
                                CharSequence text = "Invalid lesson number";
                                int duration = Toast.LENGTH_LONG;
                                Toast toast = Toast.makeText(getActivity(), text, duration);
                                toast.show();
                                editLesson.setText("");
                            } else {
                                Bundle bundle = new Bundle();
                                bundle.putInt("lessonNo", Integer.parseInt(editLesson.getText().toString()) -1);
                                MainActivity.LessonFragment fragment = new MainActivity.LessonFragment();
                                fragment.setArguments(bundle);
                                FragmentManager fragmentManager = getFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                                mActivity.hideKeyboard();
                            }
                        }
                    }

                });

                //Reads the entries on acimchapters file (which contains list of Chapters)
                // and loads it to chapList
                InputStream jFile = getResources().openRawResource(R.raw.lessons_list);
                try {

                    DataInputStream dataIO = new DataInputStream(jFile);
                    String strLine = null;
                    while ((strLine = dataIO.readLine()) != null) {
                        lessonsList.add(strLine);
                    }
                    dataIO.close();
                    jFile.close();

                } catch (Exception e) {
                    Log.e(TAG, "InputStreamToString failure1", e);
                }


                listView3 = (ListView) rootView.findViewById(R.id.list);
                ArrayAdapter adapter3 = new ArrayAdapter(getActivity(), R.layout.list_item2, lessonsList);
                listView3.setAdapter(adapter3);
                listView3.setDividerHeight(0);

                listView3.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
                        Log.d(TAG, "pos :" + pos);

                        Bundle bundle = new Bundle();
                        bundle.putInt("lessonNo", pos);
                        MainActivity.LessonFragment fragment = new MainActivity.LessonFragment();
                        fragment.setArguments(bundle);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                    }
                });

                mActivity.searchText = null;
                break;
            case 5: // Search
                getActivity().getActionBar().setSubtitle(null);
                mActivity.startSearch(null, false, null, false);
                break;
            case 6: // Bookmarks list
                getActivity().getActionBar().setSubtitle(null);
                BookmarksListFragment bookmarksFragment = new BookmarksListFragment();
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, bookmarksFragment).commit();
                break;
            case 7: // I feel guided
                getActivity().getActionBar().setSubtitle(null);
                showDialog("You are being guided to read this text:");
                break;
            default:
                break;
        }

        return rootView;
    }


    public static void updateAlarmAdapter(List<AlarmModel> alarms) {
        //Refresh the list of the alarms in the adaptor
        mAdapter.setAlarms(alarms);
        //Notify the adapter the data has changed
        mAdapter.notifyDataSetChanged();
    }

    private void showDialog(String msg) {
        final Dialog dialog = new Dialog(mActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.custom_dialog);
        final TextView msgView = (TextView) dialog.findViewById(R.id.textView);
        msgView.setText(msg);
        Button button = (Button)dialog.findViewById(R.id.okButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int[] pos;
                Random random = new Random();
                int rand = random.nextInt(267); //Generates a random number int the range [0-267]
                rand++;
                Log.d(TAG, "rand: " + Integer.toString(rand));
                pos = mActivity.getChapterPos(rand);
                Bundle bundle2 = new Bundle();
                bundle2.putInt("groupPosition", pos[0]);
                bundle2.putInt("childPosition", pos[1]);
                MainActivity.TextFragment textFragment = new MainActivity.TextFragment();
                textFragment.setArguments(bundle2);
                getFragmentManager().beginTransaction().replace(R.id.content_frame, textFragment).commit();

                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
