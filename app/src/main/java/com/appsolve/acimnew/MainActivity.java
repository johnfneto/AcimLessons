/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appsolve.acimnew;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;


public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    public static final String ACIM_PREFS = "Acim_Prefs";
    public static final String TODAYS_LESSON = "Todays_lesson";
    public static final String TEXT_SCROLL = "Text_scroll";
    public static final String TEXT_CHAPTER = "Text_chapter";
    public static final String TEXT_SECTION = "Text_section";
    public static final String LAST_DATE = "Last_date";
    public static final String ADVANCE_LESSON = "Advance_lesson";

    public static final String REPEAT_EVERY = "repeat_every";
    public static final String REPEAT_ALARM_ON = "repeat_alarm_on";
    public static final String REPEAT_START_TIME = "repeat_start_time";
    public static final String REPEAT_FINISH_TIME = "repeat_finish_time";

    static DateFormat outputhhmmFormat = new SimpleDateFormat("h:mm a");
    static DateFormat outputHFormat = new SimpleDateFormat("H");
    static DateFormat outputMFormat = new SimpleDateFormat("m");

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private static final int NO_STATE = 0;
    private static final int SEARCH = 1;
    private static final int ALARMS = 2;


    static MyExpandableListAdapter adpt;
    static List<SampleModel> lstModel;

    static AlarmListAdapter mAdapter;
    static DataBaseHelper dbHelper;


    static ArrayList<String> searchList = new ArrayList<String>();
    static ArrayList<HashMap<String, String>> bookmarksList = new ArrayList<HashMap<String, String>>();
    static private String myQuery;

    String chosenRingtone;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mMenuTitles;

    //static DataBaseHelper db; //Calls the constructor of the DataBaseHelper Class

    ProgressDialog dialog;

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    int state;

    private static FragmentManager fragmentManager;

    ProgressDialog progressdialog;

    Boolean bookMark = false;
    int webPosition;
    static String searchText;
    float scrollPosition;

    static BookmarkModel bookmarkDetails;

    @Override
    protected void onStart() {
        super.onStart();

        fragmentManager = getSupportFragmentManager();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");

        dbHelper = new DataBaseHelper(MainActivity.this);


        loadChapters();


        mTitle = mDrawerTitle = getTitle();
        mMenuTitles = getResources().getStringArray(R.array.menu_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new MyArrayAdapter(this,
                R.layout.drawer_list_item, mMenuTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        //getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar)));


        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                state = NO_STATE;
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        /*if (savedInstanceState == null) {
            selectItem(0);
        }*/

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, "search1");
            searchList.clear();
            doMySearch(query);
        }
        else {
            if (savedInstanceState == null) {
                selectItem(0);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_search).setVisible(!drawerOpen);
        //menu.findItem(R.id.menu_add_alarm);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        android.support.v4.app.FragmentManager fragmentManager;
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.action_add_new_alarm:
                startAlarmDetailsActivity(-1);       //launch the search dialog
                return true;
            /*case R.id.action_search:
                onSearchRequested();       //launch the search dialog
                return true;
            *//*case R.id.menu_book_mark:        // Book mark
                BookmarksListFragment bookmarksFragment = new BookmarksListFragment();
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, bookmarksFragment).commit();
                return true;*//*
            case R.id.menu_shuffle:
                int[] pos = new int[2];
                Random random = new Random();
                int rand = random.nextInt(267); //Generates a random number int the range [0-267]
                rand++;
                Log.d(TAG, "rand: " + Integer.toString(rand));
                pos = getChapterPos(rand);
                showDialog("You are being guided to read this text:");
                Bundle bundle = new Bundle();
                bundle.putInt("groupPosition", pos[0]);
                bundle.putInt("childPosition", pos[1]);
                TextFragment fragment = new TextFragment();
                fragment.setArguments(bundle);
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                return true;*/
            case R.id.menu_settings:
                getActionBar().setSubtitle(null);
                SettingsFragment settingsFragment = new SettingsFragment();
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, settingsFragment).commit();
                return true;
            case R.id.menu_feedback:
                showLikeItOrNot();


                /*getActionBar().setSubtitle(null);
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    Log.e(TAG, "ActivityNotFoundException", e);
                }*/
                return true;
            case R.id.menu_help:
                getActionBar().setSubtitle(null);
                HelpFragment aboutHelp = new HelpFragment();
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, aboutHelp).commit();
                return true;
            case R.id.menu_about_us:
                getActionBar().setSubtitle(null);
                AboutFragment aboutFragment = new AboutFragment();
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, aboutFragment).commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onSearchRequested() {
        Bundle bundle = new Bundle();
        bundle.putString("extra", "exttra info");
        // search initial query
        Log.d(TAG, "onSearchRequested");
        //startSearch("mary", false, bundle, false);
        startSearch(null, false, null, false);
        return true;
    }

    private void doMySearch(final String query) {
        progressdialog = ProgressDialog.show(MainActivity.this, "", "Searching for '" + query + "'. Please wait...", true);
        new SearchTask().execute(query);
    }

    public static String getSearchText() {
        return searchText;
    }

    public static void setSearchText(String mSearchText) {
        searchText = mSearchText;
    }


    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
            ((MyArrayAdapter) adapterView.getAdapter()).selectItem(position);
        }
    }


    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(MainFragment.ARG_FRAGMENT_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mMenuTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }


    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }


    public class MyArrayAdapter extends ArrayAdapter<String> {

        private int selectedItem;

        public MyArrayAdapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);
        }

        public void selectItem(int selectedItem) {
            this.selectedItem = selectedItem;
            notifyDataSetChanged();


            Fragment fragment = new MainFragment();
            Bundle args = new Bundle();
            args.putInt(MainFragment.ARG_FRAGMENT_NUMBER, selectedItem);
            fragment.setArguments(args);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(selectedItem, true);
            setTitle(mMenuTitles[selectedItem]);
            mDrawerLayout.closeDrawer(mDrawerList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = super.getView(position, convertView, parent);

            ((TextView) convertView).setTextColor(position == selectedItem ? Color.WHITE : Color.BLACK);

            return convertView;
        }
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Fragment that appears in the "content_frame"
     */
    /*public static class MyFragment extends Fragment {
        public static final String ARG_FRAGMENT_NUMBER = "planet_number";

        public MyFragment() {
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
                                Editor editor = acimSettings.edit();
                                editor.putInt(TODAYS_LESSON, lessonNo);
                                editor.putString(LAST_DATE, sdf.format(currentDate));
                                editor.commit();
                            }
                        }
                    }


                    Bundle bundle = new Bundle();
                    bundle.putInt("lessonNo", lessonNo);
                    LessonFragment fragment = new LessonFragment();
                    //fragment.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                    break;
                case 1:  // Text
                    rootView = inflater.inflate(R.layout.fragment_text, container, false);
                    lstView = (ExpandableListView) rootView.findViewById(R.id.expList);
                    //lstModel = new ArrayList<SampleModel>();
                    //load model here
                    //loadSampleData();
                    //loadChapters(getActivity());
                    adpt = new MyExpandableListAdapter(getActivity(), lstView, lstModel);
                    lstView.setDividerHeight(0);
                    lstView.setAdapter(adpt);
                    //lstView.setDividerHeight(5);
                    lstView.setOnItemClickListener(new OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Log.d(TAG, "position : " + position);

                        }
                    });
                    lstView.setOnChildClickListener(new OnChildClickListener() {
                        @Override
                        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                            Log.d(TAG, "groupPosition :" + groupPosition + "  childPosition :" + childPosition);

                            Bundle bundle = new Bundle();
                            bundle.putInt("groupPosition", groupPosition);
                            bundle.putInt("childPosition", childPosition);
                            TextFragment fragment = new TextFragment();
                            fragment.setArguments(bundle);
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                            return false;
                        }
                    });

                    menuItem = getResources().getStringArray(R.array.menu_array)[i];
	            *//*
	            webView = (WebView)rootView.findViewById(R.id.webView);
	            webView.loadUrl("file:///android_asset/text2.html");*//*

                    getActivity().setTitle(menuItem);
                    break;
                case 2:    // Manual for Teachers
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
                            ManualFragment fragment = new ManualFragment();
                            fragment.setArguments(bundle);
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                        }
                    });
                    break;
                case 3: // My Alarms
                    //ArrayList<AlarmDetails> alarmList = new ArrayList<AlarmDetails>();
                    ListView listView2;
                    RelativeLayout addAlarm, repeatAlarm;
                    CheckBox checkBox1;
                    MainActivity mActivity = (MainActivity) this.getActivity();

                    setHasOptionsMenu(true);

                    final SharedPreferences sharedPrefs = getActivity().getSharedPreferences(ACIM_PREFS, Context.MODE_PRIVATE);

                    db = new DataBaseHelper(mActivity);

                    rootView = inflater.inflate(R.layout.notification, container, false);

                    final TextView startText = (TextView) rootView.findViewById(R.id.startText);
                    final TextView endText = (TextView) rootView.findViewById(R.id.endText);
                    final TextView repeatText = (TextView) rootView.findViewById(R.id.repeatText);

                    startText.setText("Start: " + sharedPrefs.getString(REPEAT_START_TIME, "8:00am"));
                    endText.setText("Finish: " + sharedPrefs.getString(REPEAT_FINISH_TIME, "10:00pm"));
                    repeatText.setText("Every " + getResources().getStringArray(R.array.repeat_times)[sharedPrefs.getInt(REPEAT_EVERY, 0)]);

                    *//*addAlarm = (RelativeLayout) rootView.findViewById(R.id.addAlarm);
                    addAlarm.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {

                        }
                    });*//*



                    repeatText.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Log.d(TAG, "Repeat");
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Repeat every:")
                                    .setSingleChoiceItems(R.array.repeat_times, -1, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Log.d(TAG, "Repeat every :" + getResources().getStringArray(R.array.repeat_times)[which]);
                                            repeatText.setText("Every " + getResources().getStringArray(R.array.repeat_times)[which]);
                                            sharedPrefs.edit().putInt(REPEAT_EVERY, which).commit();
                                            dialog.dismiss();
                                        }
                                    });
                            builder.create().show();
                        }
                    });

                    final Calendar updateTime = Calendar.getInstance();
                    //updateTime.setTimeZone(TimeZone.getDefault());
                    //updateTime.set(Calendar.HOUR_OF_DAY, 20);
                    //updateTime.set(Calendar.MINUTE, 30);

                    *//* Retrieve a PendingIntent that will perform a broadcast *//*
                    Intent alarmIntent = new Intent(getActivity(), AlarmRepeatReceiver.class);
                    final PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmIntent, 0);



                    checkBox1 = (CheckBox) rootView.findViewById(R.id.checkBox1);
                    checkBox1.setChecked(sharedPrefs.getBoolean(REPEAT_ALARM_ON, false));
                    checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (buttonView.isChecked()) {
                                AlarmManager manager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                                int interval = getResources().getIntArray(R.array.repeat_times_min)[sharedPrefs.getInt(REPEAT_EVERY, 10)] * 1000 * 60;
                                Log.d(TAG, "interval :" + getResources().getIntArray(R.array.repeat_times_min)[sharedPrefs.getInt(REPEAT_EVERY, 10)]);
                                manager.setRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), interval, pendingIntent);
                                sharedPrefs.edit().putBoolean(REPEAT_ALARM_ON, true);
                            } else {
                                AlarmManager manager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                                manager.cancel(pendingIntent);
                                sharedPrefs.edit().putBoolean(REPEAT_ALARM_ON, false);

                            }
                        }
                    });

                    final LinearLayout startFinishLayout = (LinearLayout) rootView.findViewById(R.id.startFinishLayout);
                    startFinishLayout.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {

                            final Dialog dialog = new Dialog(v.getContext());
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.tabed_dialog_layout);

                            // get our tabHost from the xml
                            TabHost tabHost = (TabHost)dialog.findViewById(R.id.TabHost01);
                            tabHost.setup();

                            // create tab 1
                            TabHost.TabSpec spec1 = tabHost.newTabSpec("tab1");
                            spec1.setIndicator("Start time", getResources().getDrawable(R.drawable.ic_launcher));
                            spec1.setContent(R.id.timePicker1);
                            tabHost.addTab(spec1);
                            //create tab2
                            TabHost.TabSpec spec2 = tabHost.newTabSpec("tab2");
                            spec2.setIndicator("Finish time", getResources().getDrawable(R.drawable.ic_launcher));
                            spec2.setContent(R.id.timePicker2);
                            tabHost.addTab(spec2);


                            final TimePicker timePicker1 = (TimePicker) dialog.findViewById(R.id.timePicker1);
                            final TimePicker timePicker2 = (TimePicker) dialog.findViewById(R.id.timePicker2);


                            Date parsedStartDate = new Date();
                            Date parsedEndDate = new Date();
                            try {
                                parsedStartDate = outputhhmmFormat.parse(sharedPrefs.getString(REPEAT_START_TIME, "8:00am"));
                                parsedEndDate = outputhhmmFormat.parse(sharedPrefs.getString(REPEAT_FINISH_TIME, "10:10pm"));
                            }
                            catch (ParseException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }


                            timePicker1.setCurrentHour(Integer.parseInt(outputHFormat.format(parsedStartDate)));
                            timePicker1.setCurrentMinute(Integer.parseInt(outputMFormat.format(parsedStartDate)));

                            timePicker2.setCurrentHour(Integer.parseInt(outputHFormat.format(parsedEndDate)));
                            timePicker2.setCurrentMinute(Integer.parseInt(outputMFormat.format(parsedEndDate)));


                            Button button = (Button)dialog.findViewById(R.id.okButton);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d(TAG, "getCurrentHour :" + timePicker1.getCurrentHour());
                                    Log.d(TAG, "getCurrentMinute :" + timePicker1.getCurrentMinute());

                                    int hours1 = timePicker1.getCurrentHour();
                                    int minutes1 = timePicker1.getCurrentMinute();

                                    int hours2 = timePicker2.getCurrentHour();
                                    int minutes2 = timePicker2.getCurrentMinute();

                                    final Calendar startTime = Calendar.getInstance();
                                    final Calendar endTime = Calendar.getInstance();
                                    startTime.set(Calendar.HOUR_OF_DAY, hours1);
                                    startTime.set(Calendar.MINUTE, minutes1);
                                    endTime.set(Calendar.HOUR_OF_DAY, hours2);
                                    endTime.set(Calendar.MINUTE, minutes2);


                                    Date startTimeDate = startTime.getTime();
                                    Date endTimeDate = endTime.getTime();


                                    sharedPrefs.edit().putString(REPEAT_START_TIME, outputhhmmFormat.format(startTimeDate));
                                    sharedPrefs.edit().putString(REPEAT_FINISH_TIME, outputhhmmFormat.format(endTimeDate));


                                    startText.setText("Start: " + outputhhmmFormat.format(startTimeDate));
                                    endText.setText("Finish: " + outputhhmmFormat.format(endTimeDate));

                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        }
                    });


                    listView2 = (ListView) rootView.findViewById(R.id.alarmsList);
                    final ArrayList<AlarmDetails> alarmList = getAlarms();
                    //AlarmAdapter adapter2 = new AlarmAdapter(getActivity(), alarmList);
                    //listView2.setAdapter(adapter2);

                    mAdapter = new AlarmListAdapter(getActivity(), dbHelper.getAlarms());

                    listView2.setAdapter(mAdapter);
                    listView2.setDivider(this.getResources().getDrawable(android.R.color.transparent));
                    listView2.setCacheColorHint(0);
                    listView2.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                            int time = alarmList.get(position).getTime();
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
                    });

                    break;
                case 4:  // Finda a Lesson
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
                                    bundle.putInt("lessonNo", Integer.parseInt(editLesson.getText().toString()));
                                    LessonFragment fragment = new LessonFragment();
                                    fragment.setArguments(bundle);
                                    FragmentManager fragmentManager = getFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
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
                            bundle.putInt("lessonNo", pos + 1);
                            LessonFragment fragment = new LessonFragment();
                            fragment.setArguments(bundle);
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                        }
                    });


                    break;
                case 5:
                    startSearch(null, false, null, false);

                    break;
                case 6:
                case 7:
                    *//*rootView = inflater.inflate(R.layout.fragment_planet, container, false);

                    menuItem = getResources().getStringArray(R.array.menu_array)[i];

                    int imageId = getResources().getIdentifier(menuItem.toLowerCase(Locale.getDefault()),
                            "drawable", getActivity().getPackageName());
                    ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
                    getActivity().setTitle(menuItem);
                    break;*//*
                default:
                    break;
            }

            return rootView;
        }
    }*/







    /*void showAddAlarm() {
        AddAlarmFragment fragment = new AddAlarmFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();
    }*/



    public static class ChooseStartFinishTimeFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.finish_start_frag, container, false);

            FragmentTabHost myTabHost;
            myTabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
            myTabHost.setup(getActivity(), getFragmentManager(), R.id.realtabcontent);
            //myTabHost.setBackgroundResource(R.drawable.tab_indicator);

            myTabHost.addTab(myTabHost.newTabSpec("reviews").setIndicator("Start Time"),
                    StartFrag.class, null);

            myTabHost.addTab(myTabHost.newTabSpec("hours").setIndicator("Finish Time"),
                    FinishFrag.class, null);


            TabWidget widget = myTabHost.getTabWidget();
            for (int i = 0; i < widget.getChildCount(); i++) {
                View v = widget.getChildAt(i);
                // Look for the title view to ensure this is an indicator and not a divider.
                TextView tv = (TextView) v.findViewById(R.id.title);
                if (tv == null) {
                    continue;
                }
                //v.setBackgroundResource(R.drawable.tab_indicator_holo);
            }
            return view;
        }
    }


    public static class StartFrag extends android.support.v4.app.Fragment {
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.time_picker, container, false);
            return view;
        }
    }


    public static class FinishFrag extends android.support.v4.app.Fragment {
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.time_picker, container, false);
            return view;
        }
    }




    public static class LessonFragment extends Fragment {
        private static final String TAG = "LessonFragment";
        public static final String ACIM_PREFS = "Acim_Prefs";
        public static final String TODAYS_LESSON = "Todays_lesson";
        public static final String TEXT_SCROLL = "Text_scroll";
        public static final String TEXT_CHAPTER = "Text_chapter";
        public static final String TEXT_SECTION = "Text_section";
        public static final String LAST_DATE = "Last_date";
        public static final String ADVANCE_LESSON = "Advance_lesson";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        ViewPager viewPager;
        boolean noArg = true;

        Boolean bookMark = false;
        int webPosition;
        //String searchText;
        float scrollPosition;

        MainActivity mActivity;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            Log.d(TAG, "LessonFragment onCreate");
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onAttach(Activity activity) {
            Log.d(TAG, "LessonFragment onAttach");
            super.onAttach(activity);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            Log.d(TAG, "LessonFragment onActivityCreated");
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Log.d(TAG, "LessonFragment onCreateView");
            PagerAdapter pagerAdapter;
            PageListener pageListener;
            mActivity = (MainActivity) this.getActivity();

            String chapter;
            String section;
            String fileName;


            int groupPosition;
            int childPosition;


            View view = inflater.inflate(R.layout.fragment_display_lesson, container, false);

            int lessonNo = 0;
            Bundle b = getArguments();
            if (b != null) {
                if (b.containsKey("lessonNo")) {
                    lessonNo = b.getInt("lessonNo");
                    noArg = false;
                } else {

                }
                //setSearchText(b.getString("search"));
                groupPosition = b.getInt("groupPosition");
                childPosition = b.getInt("childPosition");
                webPosition = b.getInt("positionY");
                scrollPosition = b.getFloat("scrollPosition", 0);


                if (b.containsKey("bookMark"))
                    bookMark = b.getBoolean("bookMark", true);
                else
                    bookMark = false;
            }

            Log.d(TAG, "LessonFragment onCreateView -> searchText :" + getSearchText());

            /*viewPager = (ViewPager) view.findViewById(R.id.lessons_pager);
            pagerAdapter = new InfinitePagerAdapter(new ImagePagerAdapter(mActivity));
            viewPager.setAdapter(pagerAdapter);
            pageListener = new PageListener();
            viewPager.setOnPageChangeListener(pageListener);
            viewPager.setCurrentItem(lessonNo);*/



            viewPager = (ViewPager) view.findViewById(R.id.lessons_pager);
            //pagerAdapter = mActivity.new InfinitePagerAdapter(mActivity.new ImagePagerAdapter(mActivity));
            pagerAdapter = mActivity.new ImagePagerAdapter(mActivity);
            viewPager.setAdapter(pagerAdapter);
            pageListener = mActivity.new PageListener();
            viewPager.setOnPageChangeListener(pageListener);
            viewPager.setCurrentItem(lessonNo);

            getActivity().setTitle("Lesson " + (lessonNo+1));
            return view;
        }

        @Override
        public void onResume() {

            Date currentDate = null;
            try {
                currentDate = sdf.parse(sdf.format(new Date()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "LessonFragment onResume");

            if (noArg) {
                SharedPreferences acimSettings;
                Boolean advanceLesson = false;
                int lessonNo = 0;

                acimSettings = getActivity().getSharedPreferences(ACIM_PREFS, Context.MODE_PRIVATE);
                if (acimSettings.contains(ADVANCE_LESSON))
                    advanceLesson = acimSettings.getBoolean(ADVANCE_LESSON, false);
                Log.d(TAG, "advanceLesson :" + advanceLesson);
                if (advanceLesson) {
                    if (acimSettings.contains(TODAYS_LESSON))
                        lessonNo = acimSettings.getInt(TODAYS_LESSON, 0);
                    Log.d(TAG, "lessonNo :" + lessonNo);
                    Log.d(TAG, "LAST_DATE :" + acimSettings.getString(LAST_DATE, "0"));

                    Log.d(TAG, "currentDate :" + sdf.format(currentDate));

                    if (acimSettings.contains(LAST_DATE)) {
                        Date lastDate = null;
                        try {
                            lastDate = sdf.parse(acimSettings.getString(LAST_DATE, ""));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (currentDate.after(lastDate)) {
                            if (lessonNo == 364)
                                lessonNo = 0;
                            else
                                lessonNo++;
                            SharedPreferences.Editor editor = acimSettings.edit();
                            editor.putInt(TODAYS_LESSON, lessonNo);
                            editor.putString(LAST_DATE, sdf.format(currentDate));
                            editor.commit();
                        }
                    }
                }


                viewPager.setCurrentItem(lessonNo);


                getActivity().setTitle("Lesson " + (lessonNo+1));
            }


            super.onResume();
        }
    }


    public static class TextFragment extends Fragment {
        WebView webView;
        int saveChapter;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_display_text, container, false);

            Log.d(TAG, "searchText :"+searchText);

            setHasOptionsMenu(true);

            final WebView textWebView = (WebView) view.findViewById(R.id.webView);
            webView = textWebView;
            final TextView nextButton = (TextView) view.findViewById(R.id.nextButton);
            nextButton.setVisibility(View.GONE);

            //webView = textWebView;
            textWebView.getSettings().setSaveFormData(true);
            textWebView.getSettings().setBuiltInZoomControls(true);
            textWebView.getSettings().setJavaScriptEnabled(true);
            textWebView.setBackgroundColor(Color.argb(1, 0, 0, 0));
            //textWebView.setBackgroundColor(0);



            String chapter;
            String section;
            String fileName;
            final Boolean bookMark;

            int groupPosition;
            int childPosition;
            int webPosition;

            Bundle b = getArguments();
            //final String searchText = b.getString("search");
            groupPosition = b.getInt("groupPosition");
            childPosition = b.getInt("childPosition");
            webPosition = b.getInt("positionY");
            final float scrollPosition = b.getFloat("scrollPosition", 0);


            float webviewsize = textWebView.getContentHeight() - textWebView.getTop();
            Log.d(TAG, "textWebView.getTop() :" + textWebView.getTop());
            Log.d(TAG, "textWebView.getContentHeight() :" + textWebView.getContentHeight());
            float positionInWV = webviewsize * scrollPosition;
            final int positionY = Math.round(textWebView.getTop() + positionInWV);
            final int positionY2 = webPosition;


            if (b.containsKey("bookMark"))
                bookMark = b.getBoolean("bookMark", true);
            else
                bookMark = false;

            saveChapter = childPosition;

            chapter = lstModel.get(groupPosition).getChapter();
            section = lstModel.get(groupPosition).getItems().get(childPosition).getSection();
            //scrollPos = b.getInt("scroll");
            //bookMark = b.getBoolean("bookmark");
            Log.d(TAG, "chapter:" + chapter);
            Log.d(TAG, "section:" + section);
            Log.d(TAG, "scrollPosition:" + scrollPosition);

            saveChapter = groupPosition;

            if (!chapter.equalsIgnoreCase("introduction"))
                chapter = "Chapter" + chapter;


            //Log.d(TAG, "bookMark:"+Boolean.toString(bookMark));
            fileName = chapter.substring(0, 7);
            Log.d(TAG, "fileName:" + fileName);


            if (fileName.equalsIgnoreCase("Chapter")) {
                int iffenPos = chapter.indexOf('-');
                String No = chapter.substring(8, iffenPos - 1);
                fileName = fileName.toLowerCase() + No;
            } else { // in case is the introduction Chapter
                fileName = chapter.toLowerCase();
            }

            if (!fileName.equalsIgnoreCase("introduction"))
                if (section.contains("'")) {
                    section = section.replace("'", "");
                }

            final String sectionMod = section;

            getActivity().setTitle(chapter);

            final String fileName2 = fileName;

            textWebView.setWebViewClient(new HelloWebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    //textWebView.loadUrl("javascript:changeColor('" + color + "')");
                    if (bookMark) {
                        view.postDelayed(new Runnable() {
                            public void run() {
                                //textWebView.scrollTo(0, positionY);
                                textWebView.scrollTo(0, positionY2);
                                //Log.d(TAG, "scrollToPos:"+Integer.toString(scrollPosition));
                            }
                            // Delay the scrollTo to make it work
                        }, 300);
                    } else if (searchText != null && searchText != "") {
                        Log.d(TAG, "searchText in :" + searchText);
                        textWebView.findAllAsync(" " + searchText + " ");
                        nextButton.setVisibility(View.VISIBLE);
                        try {
                            Method m = WebView.class.getMethod("setFindIsUp", Boolean.TYPE);
                            m.invoke(textWebView, true);
                        } catch (Throwable ignored) {
                        }
                    }

                    if (!fileName2.equalsIgnoreCase("introduction")) {
                        textWebView.loadUrl("javascript:scrollToElement('" + sectionMod + "')");
                    }
                    //selectAndCopyText(textWebView);
                }
            });
            textWebView.loadUrl("file:///android_res/raw/" + fileName + ".html");
            if (searchText != null && searchText != "")
                textWebView.findNext(true);


            nextButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    textWebView.findNext(true);
                }
            });




            return view;
        }

        private class HelloWebViewClient extends WebViewClient {
            WebView view;

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                this.view = view;
                view.loadUrl(url);
                return true;
            }
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Do something that differs the Activity's menu here
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.bookmark, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_add_bookmark:
                    Log.d(TAG, "Add bookmark! - chapter :" + saveChapter);
                    Log.d(TAG, "calculateProgression :" + calculateProgression(webView));
                    Log.d(TAG, "webView.getProgress() :" + webView.getProgress());
                    Log.d(TAG, "webView.getScrollY() :" + webView.getScrollY());

                    savePosition(Float.toString(calculateProgression(webView)), webView.getScrollY(), Integer.toString(saveChapter), "text");
                    Toast.makeText(getActivity(), "New bookmark added!", Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    }

    public static void savePosition(String position, int scrollY, String chapter, String place) {
        Time today = new Time(Time.getCurrentTimezone());
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm aa");
        today.setToNow();

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("position", position);
        map.put("scrollY", Integer.toString(scrollY));
        if (place.equals("text"))
            map.put("chapter", "Chapter " + chapter);
        else if (place.equals("manual"))
            map.put("chapter", "Manual " + chapter);
        else if (place.equals("lessons"))
            map.put("chapter", "Lesson " + chapter);
        map.put("place", place);
        map.put("line", "line 36");
        map.put("time", sdf2.format(new Date()));
        map.put("date", sdf1.format(new Date()));

        bookmarksList.add(map);

        bookmarkDetails = new BookmarkModel();
        bookmarkDetails.date = sdf1.format(new Date());
        bookmarkDetails.time = sdf2.format(new Date());
        if (place.equals("text"))
            bookmarkDetails.chapter = "Chapter " + chapter;
        else if (place.equals("manual"))
            bookmarkDetails.chapter = "Manual " + chapter;
        bookmarkDetails.line = "";
        bookmarkDetails.scrollY = Integer.toString(scrollY);
        bookmarkDetails.place = place;
        bookmarkDetails.position = position;

        dbHelper.createBookmark(bookmarkDetails);
    }

    public static class ManualFragment extends Fragment {
        WebView webView;
        String saveChapter;
        final List<String> chapList = new ArrayList<String>();
        ListView listView;
        //WebView textWebView;
        String fileName;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_display_text, container, false);

            setHasOptionsMenu(true);

            final WebView textWebView = (WebView) view.findViewById(R.id.webView);
            webView = textWebView;
            final TextView nextButton = (TextView) view.findViewById(R.id.nextButton);
            nextButton.setVisibility(View.GONE);
            textWebView.getSettings().setSaveFormData(true);
            textWebView.getSettings().setBuiltInZoomControls(true);
            textWebView.getSettings().setJavaScriptEnabled(true);
            //textWebView.setBackgroundColor(0);
            textWebView.setBackgroundColor(Color.argb(1, 0, 0, 0));

            Bundle b = getArguments();
            int webPosition;
            //final String searchText = b.getString("search");
            String chapter = b.getString("chapter");
            webPosition = b.getInt("positionY");
            final float scrollPosition = b.getFloat("scrollPosition", 0);
            saveChapter = chapter;
            Log.d(TAG, "chapter :" + chapter);

            float webviewsize = textWebView.getContentHeight() - textWebView.getTop();
            Log.d(TAG, "textWebView.getTop() :" + textWebView.getTop());
            Log.d(TAG, "textWebView.getContentHeight() :" + textWebView.getContentHeight());
            float positionInWV = webviewsize * scrollPosition;
            final int positionY = Math.round(textWebView.getTop() + positionInWV);
            final int positionY2 = webPosition;

            Log.d(TAG, "chapter :" + chapter);
            Log.d(TAG, "webviewsize :" + webviewsize);
            Log.d(TAG, "scrollPosition :" + scrollPosition);

            final Boolean bookMark;
            if (b.containsKey("bookMark"))
                bookMark = b.getBoolean("bookMark", true);
            else
                bookMark = false;

            Log.d(TAG, "bookMark :" + bookMark);

            if (chapter.equals("Introduction"))
                fileName = "manual_introduction";
            else {
                String No = "";
                if (chapter.contains("-")) {
                    int iffenPos = chapter.indexOf('-');
                    No = chapter.substring(0, iffenPos - 1);
                } else {
                    No = chapter;

                    final ArrayList<String> chapList = new ArrayList<String>();

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
                    chapter = chapList.get(Integer.parseInt(No));

                    Log.d(TAG, "chapList(" + No + "): " + chapList.get(Integer.parseInt(No)));
                }
                fileName = "manual" + No;
                saveChapter = No;
            }

            final String sectionMod = "";

            getActivity().setTitle(chapter);

            final String fileName2 = fileName;


            textWebView.setWebViewClient(new HelloWebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    Log.d(TAG, "textWebView.getTop() :" + textWebView.getTop());
                    Log.d(TAG, "textWebView.getContentHeight() :" + textWebView.getContentHeight());
                    //textWebView.loadUrl("javascript:changeColor('" + color + "')");
                    if (bookMark) {
                        view.postDelayed(new Runnable() {
                            public void run() {
                                textWebView.scrollTo(0, positionY2);
                                Log.d(TAG, "scrollToPos :" + Integer.toString(positionY));
                            }
                            // Delay the scrollTo to make it work
                        }, 300);
                    } else if (searchText != null && searchText != "") {
                        //String searchText = "love";
                        textWebView.findAllAsync(searchText);
                        nextButton.setVisibility(View.VISIBLE);
                        try {
                            Method m = WebView.class.getMethod("setFindIsUp", Boolean.TYPE);
                            m.invoke(textWebView, true);
                        } catch (Throwable ignored) {
                        }
                    }

                    if (!fileName2.equalsIgnoreCase("introduction")) {
                        textWebView.loadUrl("javascript:scrollToElement('" + sectionMod + "')");
                    }
                    //selectAndCopyText(textWebView);
                }
            });
            textWebView.loadUrl("file:///android_res/raw/" + fileName + ".html");
            if (searchText != null && searchText != "")
                textWebView.findNext(true);

            nextButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    textWebView.findNext(true);
                }
            });

            return view;
        }

        private class HelloWebViewClient extends WebViewClient {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Do something that differs the Activity's menu here
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.bookmark, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_add_bookmark:
                    Log.d(TAG, "Add bookmark! - chapter :" + saveChapter);
                    Log.d(TAG, "calculateProgression :" + calculateProgression(webView) * 100 + "%");
                    Log.d(TAG, "webView.getProgress() :" + webView.getProgress());
                    Log.d(TAG, "webView.getScrollY() :" + webView.getScrollY());

                    savePosition(Float.toString(calculateProgression(webView)), webView.getScrollY(), saveChapter, "manual");
                    Toast.makeText(getActivity(), "New bookmark added!", Toast.LENGTH_SHORT).show();
                    break;
            }

            return false;
        }
    }









    /*@Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
    {
        if (resultCode == Activity.RESULT_OK && requestCode == 5)
        {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null)
            {
                this.chosenRingtone = uri.toString();
            }
            else
            {
                this.chosenRingtone = null;
            }
        }
        Log.d(TAG, "chosenRingtone :"+chosenRingtone);
    }*/


    public static class SearchFragment extends Fragment {
        ArrayList<String> list = new ArrayList<String>();
        static ListView listView;
        String query;
        String chapRef[];
        String result;
        static ArrayAdapter adapter;
        static TextView listTitle;
        static Context context;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_search, container, false);

            context = getActivity();

            Log.d(TAG, "SearchFragment onCreateView()");

            //Bundle b = getArguments();
            //list = b.getStringArrayList("list");
            //query = b.getString("query");

            listTitle = (TextView) view.findViewById(R.id.list_title);

            listView = (ListView) view.findViewById(R.id.list);
            listView.setDividerHeight(0);

            setmAdapter();


            listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {

                    if (searchList.get(pos).contains("Text")) {

                        result = searchList.get(pos).replace("Text -", "");
                        Log.d(TAG, "result :" + result);
                        chapRef = result.split("\\.");

                        //Log.d(TAG, "chapRef :" + chapRef[0]);
                        //Log.d(TAG, "chapRef :" + chapRef[1]);
                        //Log.d(TAG, "chapRef :" + chapRef[2]);


                        //Log.d(TAG, "getSectionPos(Integer.parseInt(chapRef[0]), chapRef[1]) :" + getSectionPos(Integer.parseInt(chapRef[0]), chapRef[1]));

                        Bundle bundle = new Bundle();
                        bundle.putString("search", query);
                        bundle.putInt("groupPosition", Integer.parseInt(chapRef[0]));
                        bundle.putInt("childPosition", getSectionPos(Integer.parseInt(chapRef[0]), chapRef[1]));
                        TextFragment fragment = new TextFragment();
                        fragment.setArguments(bundle);
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                    } else if (searchList.get(pos).contains("Manual")) {
                        result = searchList.get(pos).replace("Manual ", "");
                        Log.d(TAG, "result :" + result);
                        chapRef = result.split("\\.");

                        Bundle bundle = new Bundle();
                        bundle.putString("search", query);
                        bundle.putString("chapter", (chapRef[0]));
                        ManualFragment fragment = new ManualFragment();
                        fragment.setArguments(bundle);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                    } else if (searchList.get(pos).contains("Lesson")) {
                        result = searchList.get(pos).replace("Lesson ", "");
                        Log.d(TAG, "result :" + result);

                        if (result.equals("INTRODUCTION"))
                            result = "1";

                        Bundle bundle = new Bundle();
                        bundle.putInt("lessonNo", Integer.parseInt(result));
                        bundle.putString("search", query);
                        LessonFragment fragment = new LessonFragment();
                        fragment.setArguments(bundle);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                    }
                }
            });

            return view;
        }

        public static void setmAdapter() {

            Log.d(TAG, "searchList.size() :" + searchList.size());
            Log.d(TAG, "myQuery :" + myQuery);
            listTitle.setText(" " + Integer.toString(searchList.size()) + " results found for \'" + myQuery + "\' :");
            adapter = new ArrayAdapter(context, R.layout.list_item3, searchList);
            listView.setAdapter(adapter);
        }
    }


    public static class SettingsFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Boolean advanceLesson = false;
            final Calendar calendar = Calendar.getInstance();
            final MainActivity mActivity = (MainActivity) this.getActivity();
            final SharedPreferences acimSettings = getActivity().getSharedPreferences(ACIM_PREFS, Context.MODE_PRIVATE);
            View view = inflater.inflate(R.layout.fragment_settings, container, false);

            final CheckBox advanceLessonCheck = (CheckBox) view.findViewById(R.id.checkBox1);
            final EditText editLesson = (EditText) view.findViewById(R.id.editText);
            Button okButton = (Button) view.findViewById(R.id.okButton);
            final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
            final ImageView dividerView = (ImageView) view.findViewById(R.id.dividerView);

            editLesson.setText(Integer.toString(acimSettings.getInt(TODAYS_LESSON, 1)+1));

            okButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Date currentDate = null;
                    try {
                        currentDate = sdf.parse(sdf.format(new Date()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String strLessonNo = editLesson.getText().toString();
                    if (!strLessonNo.equals("")) {
                        if (Integer.parseInt(strLessonNo) < 1 ||
                                Integer.parseInt(strLessonNo) > 365) {
                            Toast.makeText(getActivity(), "Please enter a valid lesson number", Toast.LENGTH_SHORT).show();
                            editLesson.setText("");
                        } else {
                            Log.d(TAG, "saving LAST_DATE :" + sdf.format(currentDate));
                            acimSettings.edit().putInt(TODAYS_LESSON, Integer.parseInt(editLesson.getText().toString())-1).commit();
                            acimSettings.edit().putString(LAST_DATE, sdf.format(currentDate)).commit();
                            acimSettings.edit().putBoolean(ADVANCE_LESSON, advanceLessonCheck.isChecked()).commit();
                            LessonFragment fragment = new LessonFragment();
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                            mActivity.hideKeyboard();
                        }
                    } else
                        Toast.makeText(getActivity(), "Please enter a valid lesson number", Toast.LENGTH_SHORT).show();
                }
            });


            if (acimSettings.contains(ADVANCE_LESSON))
                advanceLesson = acimSettings.getBoolean(ADVANCE_LESSON, false);
            if (advanceLesson) {
                advanceLessonCheck.setChecked(true);
                linearLayout.setVisibility(View.VISIBLE);
                dividerView.setVisibility(View.VISIBLE);
            } else {
                advanceLessonCheck.setChecked(false);
                linearLayout.setVisibility(View.GONE);
                dividerView.setVisibility(View.GONE);
            }

            advanceLessonCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Date currentDate = null;
                    try {
                        currentDate = sdf.parse(sdf.format(new Date()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    acimSettings.edit().putBoolean(ADVANCE_LESSON, isChecked).commit();
                    acimSettings.edit().putString(LAST_DATE, sdf.format(currentDate)).commit();
                    if (isChecked) {
                        linearLayout.setVisibility(View.VISIBLE);
                        dividerView.setVisibility(View.VISIBLE);
                    } else {
                        linearLayout.setVisibility(View.GONE);
                        dividerView.setVisibility(View.GONE);
                    }
                }
            });

            getActivity().setTitle("Settings");
            return view;
        }
    }

    public static class AboutFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            WebView webView;
            View view = inflater.inflate(R.layout.fragment_display_text, container, false);

            webView = (WebView) view.findViewById(R.id.webView);
            webView.loadUrl("file:///android_res/raw/about.html");
            getActivity().setTitle("About us");
            return view;
        }
    }

    public static class HelpFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            WebView webView;
            View view = inflater.inflate(R.layout.fragment_display_text, container, false);

            webView = (WebView) view.findViewById(R.id.webView);
            webView.loadUrl("file:///android_res/raw/about.html");
            getActivity().setTitle("About us");
            return view;
        }
    }

    private void loadChapters() {

        lstModel = new ArrayList<SampleModel>();

        SampleModel model = new SampleModel();
        List<DetailsModel> lstdm = new ArrayList<DetailsModel>();
        DetailsModel dm1 = new DetailsModel();


        dm1.setSection("Introduction");
        lstdm.add(dm1);

        model.setChapter("Introduction");
        model.setItems(lstdm);
        lstModel.add(model);


        try {
            InputStream inputStream = getResources().openRawResource(R.raw.acimchaptersections);
            //InputStreamReader streamReader = new InputStreamReader(inputStream);
            Scanner bufferedReader = new Scanner(new InputStreamReader(inputStream));
            String strLine = null;
            while (bufferedReader.hasNext()) {
                strLine = bufferedReader.nextLine();
                //Log.d(TAG, "strLine:"+strLine+"-");
                //Log.d(TAG, "strLine.length():"+strLine.length());
                if (strLine.length() != 0) {
                    if (strLine.contains("Chapter")) {
                        model = new SampleModel();
                        model.setChapter(strLine);
                        lstdm = new ArrayList<DetailsModel>();
                        //Log.d(TAG, "add Chapter");
                    } else {
                        dm1 = new DetailsModel();
                        dm1.setSection(strLine);
                        lstdm.add(dm1);
                        //Log.d(TAG, "add Section");
                    }
                } else {
                    model.setItems(lstdm);
                    lstModel.add(model);
                    //Log.d(TAG, "close Chapter");
                }
            }
            model.setItems(lstdm);
            lstModel.add(model);
            inputStream.close(); //close the file
        } catch (Exception e) {
            Log.e(TAG, "InputStreamToString failure2", e);
        }

    }


    public ArrayList<String> searchText(String searchString) {
        ArrayList<String> list = new ArrayList<String>();
        String[] searchWords = searchString.split(" ");
        Log.d(TAG, "searchWords.length: "+searchWords.length);

        Log.d(TAG, "searchString: "+searchString);

        // Searchs in text1
        try {
            InputStream iFile1 = getResources().openRawResource(R.raw.text1);
            Scanner bufferedReader = new Scanner(new InputStreamReader(iFile1));
            String strLine = null, strRef = null, prevLine = null;

            while (bufferedReader.hasNext()) {
                strLine = bufferedReader.nextLine();
                String[] words = strLine.split(" ");




                if (searchWords.length > 1) {
                    if (strLine.toLowerCase().contains(searchString.toLowerCase())) {
                        if (strLine.substring(0, 2).equals("T-")) { //If it is a section
                            int pos = strLine.indexOf(' ');
                            strRef = "Text " + strLine.substring(1, pos - 1);
                        } else {
                            if (strLine.startsWith("Chapter")) { //If it is a Chapter title
                                int pos = strLine.indexOf('.');
                                strRef = strLine;
                            } else {
                                // If it is Section title
                                if (strLine.startsWith("I") || strLine.startsWith("V") || strLine.startsWith("X")) {
                                    int pos = strLine.indexOf('.');
                                    //strRef = strLine.substring(0, pos+1);
                                    strRef = strLine;
                                    Log.d(TAG, "result: "+strRef);
                                    Log.d(TAG, "prevLine: "+prevLine);
                                } else { // If quotes or prayers needs to reference the previous line

                                    //Log.d(DEBUG_TAG,strLine.toLowerCase() +"contains");
                                    if (prevLine.substring(0, 2).equals("T-")) { //If it is a section
                                        int pos = prevLine.indexOf(' ');
                                        strRef = "Text " + prevLine.substring(1, pos - 1);
                                    }

                                    //strRef = strLine;
                                    //strRef = prevLine;
                                }
                            }
                        }
                        list.add(strRef);
                        prevLine = strLine;
                    }
                }
                else
                    for (String word : words) {
                        if (word.toLowerCase().equals(searchString.toLowerCase())) {
                            if (strLine.substring(0, 2).equals("T-")) { //If it is a section
                                int pos = strLine.indexOf(' ');
                                strRef = "Text " + strLine.substring(1, pos - 1);
                            } else {
                                if (strLine.startsWith("Chapter")) { //If it is a Chapter title
                                    int pos = strLine.indexOf('.');
                                    strRef = strLine;
                                } else {
                                    // If it is Section title
                                    if (strLine.startsWith("I") || strLine.startsWith("V") || strLine.startsWith("X")) {
                                        int pos = strLine.indexOf('.');
                                        //strRef = strLine.substring(0, pos+1);
                                        strRef = strLine;
                                        Log.d(TAG, "result: "+strRef);
                                        Log.d(TAG, "prevLine: "+prevLine);
                                    } else { // If quotes or prayers needs to reference the previous line

                                        //Log.d(DEBUG_TAG,strLine.toLowerCase() +"contains");
                                        if (prevLine.substring(0, 2).equals("T-")) { //If it is a section
                                            int pos = prevLine.indexOf(' ');
                                            strRef = "Text " + prevLine.substring(1, pos - 1);
                                        }

                                        //strRef = strLine;
                                        //strRef = prevLine;
                                    }
                                }
                            }
                            list.add(strRef);
                        prevLine = strLine;
                        }
                    }








                // ex: T-1.I.2. Miracles as such do not matter. The only
                // ex: Chapter 2. THE SEPARATION AND THE ATONEMENT
                // ex: II. The Atonement as Defense
                /*if (strLine.toLowerCase().contains(searchString.toLowerCase())) {
                    //Log.d(TAG, "strLine: "+strLine);
                    //Log.d(DEBUG_TAG,strLine.toLowerCase() +"contains");
                    if (strLine.substring(0, 2).equals("T-")) { //If it is a section
                        int pos = strLine.indexOf(' ');
                        strRef = "Text " + strLine.substring(1, pos - 1);
                    } else {
                        if (strLine.startsWith("Chapter")) { //If it is a Chapter title
                            int pos = strLine.indexOf('.');
                            //strRef = strLine.substring(0, pos+1);
                            strRef = strLine;
                            //Log.d(DEBUG_TAG, "result: "+strRef);
                        } else {
                            // If it is Section title
                            if (strLine.startsWith("I") || strLine.startsWith("V") || strLine.startsWith("X")) {
                                int pos = strLine.indexOf('.');
                                //strRef = strLine.substring(0, pos+1);
                                strRef = strLine;
                                Log.d(TAG, "result: "+strRef);
                                Log.d(TAG, "prevLine: "+prevLine);
                            } else { // If quotes or prayers needs to reference the previous line
                                strRef = strLine;
                                //strRef = prevLine;
                            }
                        }
                    }
                    list.add(strRef);
                }
                prevLine = strLine;*/
            }
            iFile1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }





        // Searchs in text2
        try {
            InputStream iFile2 = getResources().openRawResource(R.raw.text2);
            Scanner bufferedReader = new Scanner(new InputStreamReader(iFile2));
            String strLine = null, strRef = null, prevLine = null;

            while (bufferedReader.hasNext()) {
                strLine = bufferedReader.nextLine();
                String[] words = strLine.split(" ");

                if (searchWords.length > 1) {
                    if (strLine.toLowerCase().contains(searchString.toLowerCase())) {
                        if (strLine.substring(0, 2).equals("T-")) { //If it is a section
                            int pos = strLine.indexOf(' ');
                            strRef = "Text " + strLine.substring(1, pos - 1);
                        } else {
                            if (strLine.startsWith("Chapter")) { //If it is a Chapter title
                                int pos = strLine.indexOf('.');
                                strRef = strLine;
                            } else {
                                // If it is Section title
                                if (strLine.startsWith("I") || strLine.startsWith("V") || strLine.startsWith("X")) {
                                    int pos = strLine.indexOf('.');
                                    //strRef = strLine.substring(0, pos+1);
                                    strRef = strLine;
                                    Log.d(TAG, "result: "+strRef);
                                    Log.d(TAG, "prevLine: "+prevLine);
                                } else { // If quotes or prayers needs to reference the previous line

                                    //Log.d(DEBUG_TAG,strLine.toLowerCase() +"contains");
                                    if (prevLine.substring(0, 2).equals("T-")) { //If it is a section
                                        int pos = prevLine.indexOf(' ');
                                        strRef = "Text " + prevLine.substring(1, pos - 1);
                                    }

                                    //strRef = strLine;
                                    //strRef = prevLine;
                                }
                            }
                        }
                        list.add(strRef);
                        prevLine = strLine;
                    }
                }
                else
                    for (String word : words) {
                        if (word.toLowerCase().equals(searchString.toLowerCase())) {
                            if (strLine.substring(0, 2).equals("T-")) { //If it is a section
                                int pos = strLine.indexOf(' ');
                                strRef = "Text " + strLine.substring(1, pos - 1);
                            } else {
                                if (strLine.startsWith("Chapter")) { //If it is a Chapter title
                                    int pos = strLine.indexOf('.');
                                    strRef = strLine;
                                } else {
                                    // If it is Section title
                                    if (strLine.startsWith("I") || strLine.startsWith("V") || strLine.startsWith("X")) {
                                        int pos = strLine.indexOf('.');
                                        //strRef = strLine.substring(0, pos+1);
                                        strRef = strLine;
                                        Log.d(TAG, "result: "+strRef);
                                        Log.d(TAG, "prevLine: "+prevLine);
                                    } else { // If quotes or prayers needs to reference the previous line

                                        //Log.d(DEBUG_TAG,strLine.toLowerCase() +"contains");
                                        if (prevLine.substring(0, 2).equals("T-")) { //If it is a section
                                            int pos = prevLine.indexOf(' ');
                                            strRef = "Text " + prevLine.substring(1, pos - 1);
                                        }
                                    }
                                }
                            }
                            list.add(strRef);
                            prevLine = strLine;
                        }
                    }
            }
            iFile2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }




        // Searchs in text3
        try {
            InputStream iFile3 = getResources().openRawResource(R.raw.text3);
            Scanner bufferedReader = new Scanner(new InputStreamReader(iFile3));
            String strLine = null, strRef = null, prevLine = null;

            while (bufferedReader.hasNext()) {
                strLine = bufferedReader.nextLine();
                String[] words = strLine.split(" ");

                if (searchWords.length > 1) {
                    if (strLine.toLowerCase().contains(searchString.toLowerCase())) {
                        if (strLine.substring(0, 2).equals("T-")) { //If it is a section
                            int pos = strLine.indexOf(' ');
                            strRef = "Text " + strLine.substring(1, pos - 1);
                        } else {
                            if (strLine.startsWith("Chapter")) { //If it is a Chapter title
                                int pos = strLine.indexOf('.');
                                strRef = strLine;
                            } else {
                                // If it is Section title
                                if (strLine.startsWith("I") || strLine.startsWith("V") || strLine.startsWith("X")) {
                                    int pos = strLine.indexOf('.');
                                    //strRef = strLine.substring(0, pos+1);
                                    strRef = strLine;
                                    Log.d(TAG, "result: "+strRef);
                                    Log.d(TAG, "prevLine: "+prevLine);
                                } else { // If quotes or prayers needs to reference the previous line

                                    //Log.d(DEBUG_TAG,strLine.toLowerCase() +"contains");
                                    if (prevLine.substring(0, 2).equals("T-")) { //If it is a section
                                        int pos = prevLine.indexOf(' ');
                                        strRef = "Text " + prevLine.substring(1, pos - 1);
                                    }

                                    //strRef = strLine;
                                    //strRef = prevLine;
                                }
                            }
                        }
                        list.add(strRef);
                        prevLine = strLine;
                    }
                }
                else
                    for (String word : words) {
                        if (word.toLowerCase().equals(searchString.toLowerCase())) {
                            if (strLine.substring(0, 2).equals("T-")) { //If it is a section
                                int pos = strLine.indexOf(' ');
                                strRef = "Text " + strLine.substring(1, pos - 1);
                            } else {
                                if (strLine.startsWith("Chapter")) { //If it is a Chapter title
                                    int pos = strLine.indexOf('.');
                                    strRef = strLine;
                                } else {
                                    // If it is Section title
                                    if (strLine.startsWith("I") || strLine.startsWith("V") || strLine.startsWith("X")) {
                                        int pos = strLine.indexOf('.');
                                        //strRef = strLine.substring(0, pos+1);
                                        strRef = strLine;
                                        Log.d(TAG, "result: "+strRef);
                                        Log.d(TAG, "prevLine: "+prevLine);
                                    } else { // If quotes or prayers needs to reference the previous line

                                        //Log.d(DEBUG_TAG,strLine.toLowerCase() +"contains");
                                        if (prevLine.substring(0, 2).equals("T-")) { //If it is a section
                                            int pos = prevLine.indexOf(' ');
                                            strRef = "Text " + prevLine.substring(1, pos - 1);
                                        }
                                    }
                                }
                            }
                            list.add(strRef);
                            prevLine = strLine;
                        }
                    }
            }
            iFile3.close();
        } catch (IOException e) {
            e.printStackTrace();
        }





        /*try {
            Log.d(TAG, "searchString :" + searchString + "|");
            DataInputStream dataIO = new DataInputStream(iFile1);
            String strLine = null, strRef = null, prevLine = null;

            while ((strLine = dataIO.readLine()) != null) {
                String[] words = strLine.split(" ");

                // ex: T-1.I.2. Miracles as such do not matter. The only
                // ex: Chapter 2. THE SEPARATION AND THE ATONEMENT
                // ex: II. The Atonement as Defense
                if (strLine.toLowerCase().contains(searchString.toLowerCase())) {
                    Log.d(TAG, "strLine: "+strLine);
                    //Log.d(DEBUG_TAG,strLine.toLowerCase() +"contains");
                    if (strLine.substring(0, 2).equals("T-")) { //If it is a section
                        int pos = strLine.indexOf(' ');
                        strRef = "Text " + strLine.substring(1, pos - 1);

                    } else {
                        if (strLine.startsWith("Chapter")) { //If it is a Chapter title
                            int pos = strLine.indexOf('.');
                            //strRef = strLine.substring(0, pos+1);
                            strRef = strLine;
                            //Log.d(DEBUG_TAG, "result: "+strRef);
                        } else {
                            // If it is Section title
                            if (strLine.startsWith("I") || strLine.startsWith("V") || strLine.startsWith("X")) {
                                int pos = strLine.indexOf('.');
                                //strRef = strLine.substring(0, pos+1);
                                strRef = strLine;
                                //Log.d(DEBUG_TAG, "result: "+strRef);
                            } else { // If quotes or prayers needs to reference the previous line
                                //strRef = strLine;
                                strRef = prevLine;
                            }
                        }
                    }
                    list.add(strRef);
                }
                prevLine = strLine;
            }
            dataIO.close();
            iFile1.close();
        } catch (Exception e) {
            Log.e(TAG, "InputStreamToString failure1", e);
        }*/

        // Searchs in text2
        /*InputStream iFile2 = getResources().openRawResource(R.raw.text2);
        try {
            Log.d(TAG, "searchString: " + searchString);
            DataInputStream dataIO = new DataInputStream(iFile2);
            String strLine = null, strRef = null;
            while ((strLine = dataIO.readLine()) != null) {

                // ex: T-1.I.2. Miracles as such do not matter. The only
                // ex: Chapter 2. THE SEPARATION AND THE ATONEMENT
                // ex: II. The Atonement as Defense
                if (strLine.toLowerCase().contains(searchString.toLowerCase())) {
                    //Log.d(DEBUG_TAG,strLine.toLowerCase() +"contains");
                    if (strLine.substring(0, 2).equals("T-")) { //If it is a section
                        int pos = strLine.indexOf(' ');
                        strRef = "Text " + strLine.substring(1, pos - 1);
                        //Log.d(DEBUG_TAG, "result: "+strRef);
                    } else {
                        if (strLine.startsWith("Chapter")) { //If it is a Chapter title
                            int pos = strLine.indexOf('.');
                            //strRef = strLine.substring(0, pos+1);
                            strRef = strLine;
                            //Log.d(DEBUG_TAG, "result: "+strRef);
                        } else {
                            // If it is Section title
                            if (strLine.startsWith("I") || strLine.startsWith("V") || strLine.startsWith("X")) {
                                int pos = strLine.indexOf('.');
                                //strRef = strLine.substring(0, pos+1);
                                strRef = strLine;
                                //Log.d(DEBUG_TAG, "result: "+strRef);
                            } else { // If quotes or prayers needs to reference the previous line
                                strRef = strLine;
                            }
                        }
                    }
                    list.add(strRef);
                }
            }
            dataIO.close();
            iFile2.close();
        } catch (Exception e) {
            Log.e(TAG, "InputStreamToString failure1", e);
        }*/

        // Searchs in text3
        /*InputStream iFile3 = getResources().openRawResource(R.raw.text3);
        try {
            Log.d(TAG, "searchString: " + searchString);
            DataInputStream dataIO = new DataInputStream(iFile3);
            String strLine = null, strRef = null;
            while ((strLine = dataIO.readLine()) != null) {

                // ex: T-1.I.2. Miracles as such do not matter. The only
                // ex: Chapter 2. THE SEPARATION AND THE ATONEMENT
                // ex: II. The Atonement as Defense
                if (strLine.toLowerCase().contains(searchString.toLowerCase())) {
                    //Log.d(DEBUG_TAG,strLine.toLowerCase() +"contains");
                    if (strLine.substring(0, 2).equals("T-")) { //If it is a section
                        int pos = strLine.indexOf(' ');
                        strRef = "Text " + strLine.substring(1, pos - 1);
                        //Log.d(DEBUG_TAG, "result: "+strRef);
                    } else {
                        if (strLine.startsWith("Chapter")) { //If it is a Chapter title
                            int pos = strLine.indexOf('.');
                            //strRef = strLine.substring(0, pos+1);
                            strRef = strLine;
                            //Log.d(DEBUG_TAG, "result: "+strRef);
                        } else {
                            // If it is Section title
                            if (strLine.startsWith("I") || strLine.startsWith("V") || strLine.startsWith("X")) {
                                int pos = strLine.indexOf('.');
                                //strRef = strLine.substring(0, pos+1);
                                strRef = strLine;
                                //Log.d(DEBUG_TAG, "result: "+strRef);
                            } else { // If quotes or prayers needs to reference the previous line
                                strRef = strLine;
                            }
                        }
                    }
                    list.add(strRef);
                }
            }
            dataIO.close();
            iFile3.close();
        } catch (Exception e) {
            Log.e(TAG, "InputStreamToString failure1", e);
        }*/

        // Searchs in manual
        /*InputStream iFile4 = getResources().openRawResource(R.raw.manualforteachers);
        try {
            //Log.d(TAG, "searchString: " + searchString);
            DataInputStream dataIO = new DataInputStream(iFile4);
            String strLine = null, strRef = null;
            while ((strLine = dataIO.readLine()) != null) {

                // ex: T-1.I.2. Miracles as such do not matter. The only
                // ex: Chapter 2. THE SEPARATION AND THE ATONEMENT
                // ex: II. The Atonement as Defense
                if (strLine.toLowerCase().contains(searchString.toLowerCase())) {
                    //Log.d(DEBUG_TAG,strLine.toLowerCase() +"contains");
                    if (strLine.substring(0, 2).equals("M-")) { //If it is a section
                        int pos = strLine.indexOf(' ');
                        strRef = "Manual " + strLine.substring(2, pos - 1);
                        //Log.d(TAG, "pos: " + pos);
                        //Log.d(TAG, "result: " + strRef);
                    } else {
                        if (Character.isDigit(strLine.charAt(0))) { //If it is a Chapter title
                            int pos = strLine.indexOf('.');
                            //strRef = strLine.substring(0, pos+1);
                            strRef = "Manual " + strLine;
                            //Log.d(DEBUG_TAG, "result: "+strRef);
                        } else {
                            // If it is Section title
                            if (strLine.startsWith("I") || strLine.startsWith("V") || strLine.startsWith("X")) {
                                int pos = strLine.indexOf('.');
                                //strRef = strLine.substring(0, pos+1);
                                strRef = strLine;
                                //Log.d(DEBUG_TAG, "result: "+strRef);
                            } else { // If quotes or prayers needs to reference the previous line
                                strRef = strLine;
                            }
                        }
                    }
                    list.add(strRef);
                }
            }
            dataIO.close();
            iFile4.close();
        } catch (Exception e) {
            Log.e(TAG, "InputStreamToString failure1", e);
        }*/


        // Searchs in lessons
        /*InputStream iFile5 = getResources().openRawResource(R.raw.workbook);
        try {
            Log.d(TAG, "searchString: " + searchString);
            DataInputStream dataIO = new DataInputStream(iFile5);
            String strLine = null, strRef = null;
            String currentLesson = "INTRODUCTION";
            while ((strLine = dataIO.readLine()) != null) {
                if (strLine.contains("LESSON"))
                    currentLesson = strLine.substring(7, strLine.indexOf('.'));
                if (strLine.toLowerCase().contains(searchString.toLowerCase())) {
                    Log.d(TAG, strLine);
                    if (!currentLesson.equals("INTRODUCTION"))
                        strRef = "Lesson " + currentLesson;
                    else
                        strRef = currentLesson;
                    Log.d(TAG, "result: " + strRef);
                    if (!list.contains(strRef))
                        list.add(strRef);
                }
            }
            dataIO.close();
            iFile5.close();
        } catch (Exception e) {
            Log.e(TAG, "InputStreamToString failure1", e);
        }*/
        return list;
    }


    public static int[] getChapterPos(int number) {
        int[] pos = new int[2];
        int i = 0;
        int chapter = 0;
        int section = 0;

        for (SampleModel model : lstModel) {
            section = 0;
            for (DetailsModel model2 : lstModel.get(chapter).getItems()) {
                if (i == number) {
                    pos[0] = chapter;
                    pos[1] = section;
                    return pos;
                }
                section++;
                i++;
            }
            chapter++;
        }
        return pos;
    }

    private static int getSectionPos(int chap, String ref) {
        int section = 0;

        //for(SampleModel model : lstModel) {
        section = 0;
        //if (lstModel.get(chap).getChapter().substring(0, ref.length()).equals(ref))
        for (DetailsModel model2 : lstModel.get(chap).getItems()) {
            if (model2.getSection().substring(0, ref.length()).equals(ref))
                return section;
            section++;
        }
        //}
        return section;
    }


    // ------------------------------------------------------------  LISTENER FOR CHANGE PAGE -----------------------------------------//

    public class PageListener extends ViewPager.SimpleOnPageChangeListener {
        public void onPageSelected(int position) {
            Log.d(TAG, "onPageSelected -> position :" + position);
            setTitle("Lesson " + (position + 1));
            getActionBar().setSubtitle(getResources().getStringArray(R.array.lesson_titles)[position]);
        }
    }


    public class ImagePagerAdapter extends PagerAdapter {
        Activity activity;

        public ImagePagerAdapter(Activity act) {
            activity = act;
        }

        public int getCount() {
            return 365;
        }

        public float getPageWidth() {
            return 0.8f;
        }



        public Object instantiateItem(View collection, int position) {
            final WebView webView;
            LayoutInflater inflater = (LayoutInflater)collection.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.fragment_lesson, null);

            webView = (WebView)layout.findViewById(R.id.webView);
            webView.getSettings().setSaveFormData(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setBackgroundColor(Color.argb(1, 0, 0, 0));
            //webView.setBackgroundColor(0);

            webView.setWebViewClient(new HelloWebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    if (searchText != null && searchText != "") {
                        webView.findAllAsync(searchText);
                        try {
                            Method m = WebView.class.getMethod("setFindIsUp", Boolean.TYPE);
                            m.invoke(webView, true);
                        } catch (Throwable ignored) {
                        }
                    }
                }
            });

            Log.d(TAG, "position :" + position);
            webView.loadUrl("file:///android_res/raw/lesson" + Integer.toString(position + 1) + ".html");
            if (searchText != null && searchText != "")
                webView.findNext(true);


            Log.d(TAG, "position :" + position);
            Log.d(TAG, "length :" + getResources().getStringArray(R.array.lesson_titles).length);




            ((ViewPager) collection).addView(layout, 0);
            return layout;
        }

        private class HelloWebViewClient extends WebViewClient {
            WebView view;

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                this.view = view;
                view.loadUrl(url);
                return true;
            }
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }

    public class MyViewPager extends ViewPager {

        public MyViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
            setMyScroller();
        }

        private void setMyScroller() {
            try {
                Class<?> viewpager = ViewPager.class;
                Field scroller = viewpager.getDeclaredField("mScroller");
                scroller.setAccessible(true);
                scroller.set(this, new MyScroller(getContext()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public class MyScroller extends Scroller {
            public MyScroller(Context context) {
                super(context, new DecelerateInterpolator());
            }

            @Override
            public void startScroll(int startX, int startY, int dx, int dy, int duration) {
                super.startScroll(startX, startY, dx, dy, 1000 /*1 secs*/);
            }
        }
    }


    /*public class InfinitePagerAdapter extends PagerAdapter {

        //private static final String TAG = "InfinitePagerAdapter";
        private static final boolean DEBUG = true;

        private PagerAdapter adapter;

        public InfinitePagerAdapter(PagerAdapter adapter) {
            this.adapter = adapter;

        }

        @Override
        public int getCount() {
            // warning: scrolling to very high values (1,000,000+) results in
            // strange drawing behaviour
            return Integer.MAX_VALUE;
        }


         //* @return the {@link #getCount()} result of the wrapped adapter

        public int getRealCount() {
            return adapter.getCount();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int virtualPosition = position % getRealCount();
            //debug("instantiateItem: real position: " + position);
            //debug("instantiateItem: virtual position: " + virtualPosition);
            ////Log.d(TAG, "instantiateItem: real position: " + position);
            ////Log.d(TAG, "instantiateItem: virtual position: " + virtualPosition);

            //testingPosition = position;
            //if (position == 0 && position == virtualPosition && currentPage == 0)
            //debug("---- This is the first page ----");

            //currentAcc = virtualPosition;
            ////Log.d(TAG, "currentAcc :"+currentAcc);
            ////Log.d(TAG, "viewPager.getCurrentItem() :"+viewPager.getCurrentItem());
            // only expose virtual position to the inner adapter
            return adapter.instantiateItem(container, virtualPosition);
        }



        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            int virtualPosition = position % getRealCount();
            //debug("destroyItem: real position: " + position);
            //debug("destroyItem: virtual position: " + virtualPosition);

            // only expose virtual position to the inner adapter
            adapter.destroyItem(container, virtualPosition, object);
        }

	  *//*
	   * Delegate rest of methods directly to the inner adapter.
	   *//*

        @Override
        public void finishUpdate(ViewGroup container) {
            adapter.finishUpdate(container);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return adapter.isViewFromObject(view, object);
        }

        @Override
        public void restoreState(Parcelable bundle, ClassLoader classLoader) {
            adapter.restoreState(bundle, classLoader);
        }

        @Override
        public Parcelable saveState() {
            return adapter.saveState();
        }

        @Override
        public void startUpdate(ViewGroup container) {
            adapter.startUpdate(container);
        }

    }*/


    /*public ArrayList<AlarmDetails> getAlarms() {
        ArrayList<AlarmDetails> results = new ArrayList<AlarmDetails>();

        db = new DataBaseHelper(MainActivity.this);


        //If data base doesn't exist yet creates it 
        try {
            db.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        //Opens the data base
        try {
            db.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }


        results = db.getAlarms();

        // closes the database
        if (db != null) {
            db.close();
        }
        return results;
    }*/

    @Override
    public void onBackPressed() {

        if (state == SEARCH) {
            Log.d(TAG, "state == SEARCH");
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("list", searchList);
            bundle.putString("query", myQuery);

            SearchFragment fragment = new SearchFragment();
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            //super.onBackPressed();
            SearchFragment.setmAdapter();
        } else if (state == ALARMS) {

        } else {
            int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
            String currentFrag = getSupportFragmentManager().findFragmentById(R.id.content_frame).toString();
            Log.d(TAG, "backStackEntryCount :" + backStackEntryCount);
            Log.d(TAG, "getSupportFragmentManager()" + getSupportFragmentManager().findFragmentById(R.id.content_frame));
            if (currentFrag.contains("TextFragment"))
                selectItem(1);
            else if (currentFrag.contains("ManualFragment"))
                selectItem(2);
            else {
                Log.d(TAG, "exit???");
                LayoutInflater factory = LayoutInflater.from(this);
                final View dialogView = factory.inflate(R.layout.custom_dialog3, null);

                final AlertDialog dialog = new AlertDialog.Builder(this).create();
                dialog.setView(dialogView);
                dialogView.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                });
                dialogView.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                ((TextView) dialogView.findViewById(R.id.title)).setText("Exit");
                ((TextView) dialogView.findViewById(R.id.message)).setText("You are about to exit the acim app, are you sure?");

                dialog.show();
                //super.onBackPressed();
            }
        }
    }

    public static float calculateProgression(WebView content) {


        float positionTopView = content.getTop();
        float positionBottomView = content.getBottom();
        float contentHeight = content.getContentHeight();
        float currentScrollPosition = content.getScrollY();
        float percentWebview = (currentScrollPosition - positionTopView) / contentHeight;

        Log.d(TAG, "getProgress :" + content.getProgress());
        Log.d(TAG, "positionBottomView :" + positionBottomView);
        Log.d(TAG, "contentHeight :" + contentHeight);

        return percentWebview;
    }

    @Override
    protected void onResume() {

        /*View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);*/

        super.onResume();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {


            selectItem(3);


            //mAdapter.setAlarms(dbHelper.getAlarms());
            //mAdapter.notifyDataSetChanged();
        }
    }

    public void setAlarmEnabled(long id, boolean isEnabled) {
        AlarmManagerHelper.cancelAlarms(this);

        AlarmModel model = dbHelper.getAlarm(id);
        model.isEnabled = isEnabled;
        dbHelper.updateAlarm(model);

        AlarmManagerHelper.setAlarms(this);
    }

    public void startAlarmDetailsActivity(long id) {
        Log.d(TAG, "startAlarmDetailsActivity");
        Intent intent = new Intent(this, AlarmDetailsActivity.class);
        intent.putExtra("id", id);
        startActivityForResult(intent, 0);
    }

    public void deleteAlarm(long id) {
        final long alarmId = id;
        //AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme_Dialog));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please confirm")
                .setTitle("Delete alarm?")
                .setCancelable(true)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Cancel Alarms
                        AlarmManagerHelper.cancelAlarms(MainActivity.this);
                        //Delete alarm from DB by id
                        dbHelper.deleteAlarm(alarmId);


                        Log.d(TAG, "dbHelper.getAlarms() :" + dbHelper.getAlarms());

                        MainFragment.updateAlarmAdapter(dbHelper.getAlarms());

                        //Refresh the list of the alarms in the adaptor
                        //mAdapter.setAlarms(dbHelper.getAlarms());
                        //Notify the adapter the data has changed
                        //mAdapter.notifyDataSetChanged();
                        //Set the alarms
                        AlarmManagerHelper.setAlarms(MainActivity.this);
                    }
                }).show();
    }


    private void showDialog(String msg) {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.custom_dialog);
        final TextView msgView = (TextView) dialog.findViewById(R.id.textView);
        msgView.setText(msg);
        Button button = (Button) dialog.findViewById(R.id.okButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void showDialog2(String msg) {
        LayoutInflater factory = LayoutInflater.from(MainActivity.this);
        final View dialogView = factory.inflate(R.layout.custom_dialog, null);

        final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
        dialog.setView(dialogView);
        dialogView.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearch(null, false, null, false);
                dialog.dismiss();
            }
        });

        ((TextView) dialogView.findViewById(R.id.textView)).setText(msg);

        dialog.show();
    }


    private void showDialog3(String msg) {
        LayoutInflater factory = LayoutInflater.from(MainActivity.this);
        final View dialogView = factory.inflate(R.layout.custom_dialog3, null);

        final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
        dialog.setView(dialogView);
        dialogView.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ((TextView) dialogView.findViewById(R.id.title)).setText("Cancel Appointment");
        ((TextView) dialogView.findViewById(R.id.message)).setText("Are you sure you want to cancel this appointment?");

        dialog.show();
    }

    private void showLikeItOrNot() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View dialogView = factory.inflate(R.layout.custom_dialog6, null);

        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(dialogView);
        dialogView.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.option1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick option1");
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName + "&reviewId=0")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                dialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.option2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showSendFeedback();
                showDialogForm("Send Feedback");
                dialog.dismiss();
            }
        });
        ((TextView) dialogView.findViewById(R.id.title)).setText("What do you think about the acim app?");
        ((TextView) dialogView.findViewById(R.id.option1)).setText("I love it");
        ((TextView) dialogView.findViewById(R.id.option2)).setText("It could be better");
        dialog.show();
    }

    private void showDialogForm(final String title) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View promptView = layoutInflater.inflate(R.layout.form_dialog2, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

        // set prompts.xml to be the layout file of the alertdialog builder
        alertDialogBuilder.setView(promptView);

        ((TextView) promptView.findViewById(R.id.title)).setText(title);
        final EditText subjectText = (EditText) promptView.findViewById(R.id.subjectText);
        final EditText detailsText = (EditText) promptView.findViewById(R.id.detailsText);

        // setup a dialog window
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (title.equals("Request Support")) {
                            if (subjectText.getText().toString().isEmpty() && detailsText.getText().toString().isEmpty())
                                Toast.makeText(MainActivity.this, "Can't send empty message.", Toast.LENGTH_LONG).show();
                            /*else
                                sendRequestSupport(subjectText.getText().toString(), detailsText.getText().toString(), title);*/
                        } else if (title.equals("Send Feedback")) {
                            if (subjectText.getText().toString().isEmpty() && detailsText.getText().toString().isEmpty())
                                Toast.makeText(MainActivity.this, "Can't send empty message.", Toast.LENGTH_LONG).show();
                            else {
                                String versionName = "";
                                try {
                                    versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }
                                String s = "\n\nDebug-infos:";
                                s += "\n\n User email: " + getEmail(MainActivity.this);
                                s += "\n App version: " + versionName;
                                s += "\n OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
                                s += "\n OS API Level: " + android.os.Build.VERSION.SDK;
                                s += "\n Device: " + android.os.Build.DEVICE;
                                s += "\n Model (and Product): " + android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")\n\n";

                                //Log.d(TAG, "s :" + s);


                                String emailTo = "appsolvez@gmail.com";
                                String emailSubject = subjectText.getText().toString();
                                String emailContent = detailsText.getText().toString() + s;

                                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailTo});
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
                                emailIntent.putExtra(Intent.EXTRA_TEXT, emailContent);

                                //need this to prompts email client only
                                emailIntent.setType("message/rfc822");

                                startActivity(Intent.createChooser(emailIntent, "Select an Email Client:"));
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog dialog = alertDialogBuilder.show();
    }

    static String getEmail(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account account = getAccount(accountManager);
        if (account == null) {
            return null;
        } else {
            return account.name;
        }
    }
    private static Account getAccount(AccountManager accountManager) {
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Account account;
        if (accounts.length > 0) {
            account = accounts[0];
        } else {
            account = null;
        } return account;
    }


    class SearchTask extends AsyncTask<Object, Void, Integer> {
        @Override
        protected Integer doInBackground(Object... arg0) {
            String query = (String) arg0[0];
            myQuery = query;
            SearchFragment fragment = new SearchFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            searchList = searchText(query);

            if (progressdialog != null)
                progressdialog.cancel();
            if (searchList.size() > 0) {
                state = SEARCH;
                setSearchText(query);


                //Bundle bundle = new Bundle();
                //bundle.putStringArrayList("list", searchList);
                //bundle.putString("query", query);
                //SearchFragment fragment = new SearchFragment();
                //fragment.setArguments(bundle);
                //FragmentManager fragmentManager = getSupportFragmentManager();
                //fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();


                return 1;
            } else {
                return 0;
            }
        }

        protected void onPostExecute(Integer result) {
            Log.d(TAG, "onPostExecute - result :" + result);
            if (progressdialog != null)
                progressdialog.cancel();
            if (result == 0)
                showDialog2("No matches for your search.");
            else
                SearchFragment.setmAdapter();
        }
    }
}