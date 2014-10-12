package me.uyuyuy99.mainroom;

import java.util.HashSet;
import java.util.Set;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class CalendarContentResolver {
	
	public static final String[] FIELDS = {
		CalendarContract.Events.TITLE,
		CalendarContract.Events.DTSTART
	};
	public static final Uri CALENDAR_URI = Uri
			.parse("content://com.android.calendar/events");
	public static final long timeLimit = 1000 * 60 * 60 * 36;
	
	ContentResolver contentResolver;
	Set<CalEvent> calendars = new HashSet<CalEvent>();
	
	public CalendarContentResolver(Context ctx) {
		contentResolver = ctx.getContentResolver();
	}
	
	public Set<CalEvent> getEvents() {
		// Fetch a list of all calendars sync'd with the device and their display names
		Cursor cursor = contentResolver.query(CALENDAR_URI, FIELDS, null, null,
				null);
	
		try {
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					String title = cursor.getString(0);
					long time = cursor.getLong(1);
					
					long now = System.currentTimeMillis();
					if (time > now && (time - now) < timeLimit) {
						CalEvent calEvent = new CalEvent(title, time);
						
						if (!CalendarActivity.skipped.contains(calEvent)) {
							calendars.add(calEvent);
						}
					}
				}
			}
		} catch (AssertionError ex) {}
		return calendars;
	}
	
	/*
	public static final String[] FIELDS = { CalendarContract.Calendars.NAME,
			CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
			CalendarContract.Calendars.CALENDAR_COLOR,
			CalendarContract.Calendars.VISIBLE };

	public static final Uri CALENDAR_URI = Uri
			.parse("content://com.android.calendar/calendars");

	ContentResolver contentResolver;
	Set<String> calendars = new HashSet<String>();

	public CalendarContentResolver(Context ctx) {
		contentResolver = ctx.getContentResolver();
	}

	public Set<String> getCalendars() {
		// Fetch a list of all calendars sync'd with the device and their
		// display names
		Cursor cursor = contentResolver.query(CALENDAR_URI, FIELDS, null, null,
				null);

		try {
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					String name = cursor.getString(0);
					String displayName = cursor.getString(1);
					// This is actually a better pattern:
					String color = cursor
							.getString(cursor
									.getColumnIndex(CalendarContract.Calendars.CALENDAR_COLOR));
					Boolean selected = !cursor.getString(3).equals("0");
					calendars.add(displayName);
				}
			}
		} catch (AssertionError ex) {
			// TODO: log exception and bail
		}
		return calendars;
	}
	*/
	
}
