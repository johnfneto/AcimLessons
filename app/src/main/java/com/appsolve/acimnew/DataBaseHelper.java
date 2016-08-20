package com.appsolve.acimnew;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.appsolve.acimnew.AlarmContract.Alarm;
import com.appsolve.acimnew.BookmarkContract.Bookmark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
	private static final String TAG = "DataBaseHelper";


	public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "acim.db";
	
	private static final String SQL_CREATE_ALARM = "CREATE TABLE " + Alarm.TABLE_NAME + " (" +
			Alarm._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
			Alarm.COLUMN_NAME_ALARM_NAME + " TEXT," +
			Alarm.COLUMN_NAME_ALARM_TIME_HOUR + " INTEGER," +
			Alarm.COLUMN_NAME_ALARM_TIME_MINUTE + " INTEGER," +
			Alarm.COLUMN_NAME_ALARM_REPEAT_DAYS + " TEXT," +
			Alarm.COLUMN_NAME_ALARM_REPEAT_WEEKLY + " BOOLEAN," +
			Alarm.COLUMN_NAME_ALARM_TONE + " TEXT," +
			Alarm.COLUMN_NAME_ALARM_VIBRATE + " BOOLEAN," +
			Alarm.COLUMN_NAME_ALARM_ENABLED + " BOOLEAN" +
	    " )";

	private static final String SQL_CREATE_BOOKMARK = "CREATE TABLE " + Bookmark.TABLE_NAME + " (" +
			Bookmark._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
			Bookmark.COLUMN_NAME_BBOOKMARK_DATE + " TEXT," +
			Bookmark.COLUMN_NAME_BBOOKMARK_CHAPTER + " TEXT," +
			Bookmark.COLUMN_NAME_BBOOKMARK_LINE + " TEXT," +
			Bookmark.COLUMN_NAME_BBOOKMARK_SCROLL_Y + " TEXT," +
			Bookmark.COLUMN_NAME_BBOOKMARK_TIME + " TEXT," +
			Bookmark.COLUMN_NAME_BBOOKMARK_PLACE + " TEXT," +
			Bookmark.COLUMN_NAME_BBOOKMARK_POSITION + " TEXT" +
			" )";

	
	private static final String SQL_DELETE_ALARM =
		    "DROP TABLE IF EXISTS " + Alarm.TABLE_NAME;

	private static final String SQL_DELETE_BOOKMARK =
			"DROP TABLE IF EXISTS " + Bookmark.TABLE_NAME;
    
	public DataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ALARM);
		db.execSQL(SQL_CREATE_BOOKMARK);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_ALARM);
		db.execSQL(SQL_DELETE_BOOKMARK);
        onCreate(db);
	}
	
	private AlarmModel populateAlarmModel(Cursor c) {
		AlarmModel alarmModel = new AlarmModel();
		alarmModel.id = c.getLong(c.getColumnIndex(Alarm._ID));
		alarmModel.name = c.getString(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_NAME));
		alarmModel.timeHour = c.getInt(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_TIME_HOUR));
		alarmModel.timeMinute = c.getInt(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_TIME_MINUTE));
		alarmModel.repeatWeekly = c.getInt(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_REPEAT_WEEKLY)) == 0 ? false : true;
		alarmModel.alarmTone = c.getString(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_TONE)) != "" ? Uri.parse(c.getString(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_TONE))) : null;
		alarmModel.alarmVibrate = c.getInt(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_VIBRATE)) == 0 ? false : true;
		alarmModel.isEnabled = c.getInt(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_ENABLED)) == 0 ? false : true;
		
		String[] repeatingDays = c.getString(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_REPEAT_DAYS)).split(",");
		for (int i = 0; i < repeatingDays.length; ++i) {
			alarmModel.setRepeatingDay(i, repeatingDays[i].equals("false") ? false : true);
		}
		
		return alarmModel;
	}

	private BookmarkModel populateBookmarkModel(Cursor c) {
		BookmarkModel bookmarkModel = new BookmarkModel();
		bookmarkModel.id = c.getLong(c.getColumnIndex(Bookmark._ID));
		bookmarkModel.date = c.getString(c.getColumnIndex(Bookmark.COLUMN_NAME_BBOOKMARK_DATE));
		bookmarkModel.chapter = c.getString(c.getColumnIndex(Bookmark.COLUMN_NAME_BBOOKMARK_CHAPTER));
		bookmarkModel.line = c.getString(c.getColumnIndex(Bookmark.COLUMN_NAME_BBOOKMARK_LINE));
		bookmarkModel.scrollY = c.getString(c.getColumnIndex(Bookmark.COLUMN_NAME_BBOOKMARK_SCROLL_Y));
		bookmarkModel.time = c.getString(c.getColumnIndex(Bookmark.COLUMN_NAME_BBOOKMARK_TIME));
		bookmarkModel.place = c.getString(c.getColumnIndex(Bookmark.COLUMN_NAME_BBOOKMARK_PLACE));
		bookmarkModel.position = c.getString(c.getColumnIndex(Bookmark.COLUMN_NAME_BBOOKMARK_POSITION));

		return bookmarkModel;
	}

	private HashMap<String, String> populateBookmarkItem(Cursor c) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("id", Long.toString(c.getLong(c.getColumnIndex(Bookmark._ID))));
		map.put("date", c.getString(c.getColumnIndex(Bookmark.COLUMN_NAME_BBOOKMARK_DATE)));
		map.put("chapter", c.getString(c.getColumnIndex(Bookmark.COLUMN_NAME_BBOOKMARK_CHAPTER)));
		map.put("line", c.getString(c.getColumnIndex(Bookmark.COLUMN_NAME_BBOOKMARK_LINE)));
		map.put("scrollY", c.getString(c.getColumnIndex(Bookmark.COLUMN_NAME_BBOOKMARK_SCROLL_Y)));
		map.put("time", c.getString(c.getColumnIndex(Bookmark.COLUMN_NAME_BBOOKMARK_TIME)));
		map.put("place", c.getString(c.getColumnIndex(Bookmark.COLUMN_NAME_BBOOKMARK_PLACE)));
		map.put("position", c.getString(c.getColumnIndex(Bookmark.COLUMN_NAME_BBOOKMARK_POSITION)));

		return map;
	}
	
	private ContentValues populateAlarmContent(AlarmModel model) {
		ContentValues values = new ContentValues();
        values.put(Alarm.COLUMN_NAME_ALARM_NAME, model.name);
        values.put(Alarm.COLUMN_NAME_ALARM_TIME_HOUR, model.timeHour);
        values.put(Alarm.COLUMN_NAME_ALARM_TIME_MINUTE, model.timeMinute);
        values.put(Alarm.COLUMN_NAME_ALARM_REPEAT_WEEKLY, model.repeatWeekly);
        values.put(Alarm.COLUMN_NAME_ALARM_TONE, model.alarmTone != null ? model.alarmTone.toString() : "");
        values.put(Alarm.COLUMN_NAME_ALARM_VIBRATE, model.alarmVibrate);
        values.put(Alarm.COLUMN_NAME_ALARM_ENABLED, model.isEnabled);
        
        String repeatingDays = "";
        for (int i = 0; i < 7; ++i) {
        	repeatingDays += model.getRepeatingDay(i) + ",";
        }
        values.put(Alarm.COLUMN_NAME_ALARM_REPEAT_DAYS, repeatingDays);
        
        return values;
	}

	private ContentValues populateBookmarkContent(BookmarkModel model) {
		ContentValues values = new ContentValues();
		values.put(Bookmark.COLUMN_NAME_BBOOKMARK_DATE, model.date);
		values.put(Bookmark.COLUMN_NAME_BBOOKMARK_CHAPTER, model.chapter);
		values.put(Bookmark.COLUMN_NAME_BBOOKMARK_LINE, model.line);
		values.put(Bookmark.COLUMN_NAME_BBOOKMARK_SCROLL_Y, model.scrollY);
		values.put(Bookmark.COLUMN_NAME_BBOOKMARK_TIME, model.time);
		values.put(Bookmark.COLUMN_NAME_BBOOKMARK_PLACE, model.place);
		values.put(Bookmark.COLUMN_NAME_BBOOKMARK_POSITION, model.position);

		return values;
	}
	
	public long createAlarm(AlarmModel model) {
		ContentValues values = populateAlarmContent(model);
        return getWritableDatabase().insert(Alarm.TABLE_NAME, null, values);
	}

	public long createBookmark(BookmarkModel model) {
		ContentValues values = populateBookmarkContent(model);
		return getWritableDatabase().insert(Bookmark.TABLE_NAME, null, values);
	}
	
	public long updateAlarm(AlarmModel model) {
		ContentValues values = populateAlarmContent(model);
        return getWritableDatabase().update(Alarm.TABLE_NAME, values, Alarm._ID + " = ?", new String[]{String.valueOf(model.id)});
	}

	public long updateBookmark(BookmarkModel model) {
		ContentValues values = populateBookmarkContent(model);
		return getWritableDatabase().update(Bookmark.TABLE_NAME, values, Bookmark._ID + " = ?", new String[]{String.valueOf(model.id)});
	}
	
	public AlarmModel getAlarm(long id) {
		SQLiteDatabase db = this.getReadableDatabase();

		String select = "SELECT * FROM " + Alarm.TABLE_NAME + " WHERE " + Alarm._ID + " = " + id;

		Cursor c = db.rawQuery(select, null);

		if (c.moveToNext()) {
			return populateAlarmModel(c);
		}

		return null;
	}
	
	public List<AlarmModel> getAlarms() {
		SQLiteDatabase db = this.getReadableDatabase();
		
        String select = "SELECT * FROM " + Alarm.TABLE_NAME;
		
		Cursor c = db.rawQuery(select, null);
		
		List<AlarmModel> alarmList = new ArrayList<AlarmModel>();
		
		while (c.moveToNext()) {
			alarmList.add(populateAlarmModel(c));
		}

		return alarmList;
		
		/*if (!alarmList.isEmpty()) {
			return alarmList;
		}
		
		return null;*/
	}

	public ArrayList<HashMap<String, String>> getBookmarks() {
		SQLiteDatabase db = this.getReadableDatabase();

		String select = "SELECT * FROM " + Bookmark.TABLE_NAME;

		Cursor c = db.rawQuery(select, null);

		Log.d(TAG, "c.getCount() :" + c.getCount());

		//List<BookmarkModel> bookmarkList = new ArrayList<BookmarkModel>();
		ArrayList<HashMap<String, String>> bookmarkList = new ArrayList<HashMap<String, String>>();


		while (c.moveToNext()) {
			bookmarkList.add(populateBookmarkItem(c));
		}



		return bookmarkList;
	}
	
	public int deleteAlarm(long id) {
		return getWritableDatabase().delete(Alarm.TABLE_NAME, Alarm._ID + " = ?", new String[] { String.valueOf(id) });
	}

	public int deleteBookmark(long id) {
		return getWritableDatabase().delete(Bookmark.TABLE_NAME, Bookmark._ID + " = ?", new String[] { String.valueOf(id) });
	}
}
