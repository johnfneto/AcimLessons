package com.appsolve.acimnew;

import android.provider.BaseColumns;

public final class BookmarkContract {

	public BookmarkContract() {}
	
	public static abstract class Bookmark implements BaseColumns {
		public static final String TABLE_NAME = "bookmark";
		public static final String COLUMN_NAME_BBOOKMARK_DATE = "date";
		public static final String COLUMN_NAME_BBOOKMARK_CHAPTER = "chapter";
		public static final String COLUMN_NAME_BBOOKMARK_LINE = "line";
		public static final String COLUMN_NAME_BBOOKMARK_SCROLL_Y = "scrollY";
		public static final String COLUMN_NAME_BBOOKMARK_TIME = "time";
		public static final String COLUMN_NAME_BBOOKMARK_PLACE = "place";
		public static final String COLUMN_NAME_BBOOKMARK_POSITION = "position";
	}
	
}