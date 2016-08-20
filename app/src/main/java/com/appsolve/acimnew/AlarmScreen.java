package com.appsolve.acimnew;

/**
 * Created by johnn on 12/07/15.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;

public class AlarmScreen extends Activity {

    public final String TAG = this.getClass().getSimpleName();
    public static final String ACIM_PREFS = "Acim_Prefs";
    public static final String TODAYS_LESSON = "Todays_lesson";

    private WakeLock mWakeLock;
    private MediaPlayer mPlayer;

    private static final int WAKELOCK_TIMEOUT = 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setup layout
        this.setContentView(R.layout.alarm_screen);

        SharedPreferences acimSettings = getSharedPreferences(ACIM_PREFS, Context.MODE_PRIVATE);


        Log.d(TAG, "getIntent().getStringExtra(AlarmManagerHelper.TONE) :" + getIntent().getStringExtra(AlarmManagerHelper.TONE));
        Log.d(TAG, "getIntent().getStringExtra(AlarmManagerHelper.VIBRATE) :" + getIntent().getBooleanExtra(AlarmManagerHelper.VIBRATE, false));

        Log.d(TAG, "Lesson :" + acimSettings.getInt(TODAYS_LESSON, 1));



         String name = getIntent().getStringExtra(AlarmManagerHelper.NAME);
        int timeHour = getIntent().getIntExtra(AlarmManagerHelper.TIME_HOUR, 0);
        int timeMinute = getIntent().getIntExtra(AlarmManagerHelper.TIME_MINUTE, 0);
        String tone = getIntent().getStringExtra(AlarmManagerHelper.TONE);
        Boolean vibrate = getIntent().getBooleanExtra(AlarmManagerHelper.VIBRATE, false);

        TextView lessonNumber = (TextView) findViewById(R.id.lesson_number);
        TextView lessonName = (TextView) findViewById(R.id.lesson_name);
        lessonNumber.setText("Lesson " + Integer.toString(acimSettings.getInt(TODAYS_LESSON, 1)));
        lessonName.setText(getResources().getStringArray(R.array.lesson_titles)[acimSettings.getInt(TODAYS_LESSON, 1)]);

        TextView tvTime = (TextView) findViewById(R.id.alarm_screen_time);
        tvTime.setText(String.format("%02d : %02d", timeHour, timeMinute));

        Button startLessonButton = (Button) findViewById(R.id.start_lesson_button);
        final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
        startLessonButton.startAnimation(animation);
        startLessonButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                view.clearAnimation();
                mPlayer.stop();
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button snoozeButton = (Button) findViewById(R.id.snooze_button);
        snoozeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayer.stop();
                AlertDialog.Builder builder = new AlertDialog.Builder(AlarmScreen.this);
                builder.setTitle("Warn me again in:")
                        .setSingleChoiceItems(R.array.repeat_times, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "Warn me again in :" + getResources().getStringArray(R.array.repeat_times)[which]);
                                finish();
                                dialog.dismiss();
                            }
                        });
                builder.create().show();

            }
        });

        Button dismissButton = (Button) findViewById(R.id.dismiss_button);
        dismissButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayer.stop();
                finish();
            }
        });

        if (vibrate) {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(500);
        }

        //Play alarm tone
        mPlayer = new MediaPlayer();
        try {
            if (tone != null && !tone.equals("")) {
                Uri toneUri = Uri.parse(tone);
                if (toneUri != null) {
                    mPlayer.setDataSource(this, toneUri);
                    mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                    mPlayer.setLooping(true);
                    mPlayer.prepare();
                    mPlayer.start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        MediaPlayer.OnCompletionListener listener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "Finished!");
            }
        };
        if (listener != null) {
            mPlayer.setOnCompletionListener(listener);
        }

        //Ensure wakelock release
        Runnable releaseWakelock = new Runnable() {

            @Override
            public void run() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

                if (mWakeLock != null && mWakeLock.isHeld()) {
                    mWakeLock.release();
                }
            }
        };

        new Handler().postDelayed(releaseWakelock, WAKELOCK_TIMEOUT);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();

        // Set the window to keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        // Acquire wakelock
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (mWakeLock == null) {
            mWakeLock = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK | PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), TAG);
        }

        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
            Log.i(TAG, "Wakelock aquired!!");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }
}
