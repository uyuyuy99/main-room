package me.uyuyuy99.mainroom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import me.uyuyuy99.mainroom.motiondetection.MotionDetectionActivity;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
public class CalendarActivity extends Activity {
	
	private TextView eventText = null;
	
	private int screenWidth = 0;
	private int textWidth = 0;
	
	private long started = 0;
	private long nextBlank = 0;
	private long nextEvent = 0;
	
	private static final int endWait = 1500;
	private static final int startWait = 1500;
	private static final int blankTime = 1000;
	
	private Set<CalEvent> calendar;
	private Iterator<CalEvent> calEvents;
	private CalEvent currentEvent = null;
	private boolean firstCycle = true;
	
	public static List<CalEvent> skipped = new ArrayList<CalEvent>();
	public static long nextCalCheck = 0;
	
	private Thread updateScreenThread;
	private volatile boolean running = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar);
		
		eventText = (TextView) findViewById(R.id.event_text);
		
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);
		screenWidth = size.x;
		
		CalendarContentResolver ccr = new CalendarContentResolver(getBaseContext());
		calendar = ccr.getEvents();
		calEvents = calendar.iterator();
		
		Button done = (Button) findViewById(R.id.done);
		done.setOnClickListener(new OnClickListener() {
			long lastClicked = 0;
			
			@Override
			public void onClick(View view) {
				long now = System.currentTimeMillis();
				
				if (now - lastClicked > 300) {
					lastClicked = now;
					
					if (currentEvent != null) skipped.add(currentEvent);
					Toast.makeText(getApplicationContext(), "Skipped", Toast.LENGTH_SHORT).show();
					
					started = -1;
					nextBlank = 0;
					
					if (nextEvent == 0) {
						nextEvent = System.currentTimeMillis() + blankTime;
						
						eventText.setLeft(0);
						eventText.setText("");
					}
				}
			}
		});
		
		Runnable myRunnableThread = new UpdateTimeTask();
		updateScreenThread = new Thread(myRunnableThread);
		updateScreenThread.start();
	}
	
	public void updateScreen() {
		runOnUiThread(new Runnable() {
			public void run() {
				try {
					if (started == 0) {
						if (!calEvents.hasNext() && firstCycle) {
							firstCycle = false;
							calEvents = calendar.iterator();
						}
						boolean finished = true;
						while (calEvents.hasNext()) {
							started = System.currentTimeMillis();
							
							CalEvent next = calEvents.next();
							if (skipped.contains(next)) continue;
							
							currentEvent = next;
							eventText.setText(next.title);
							textWidth = getWidth(eventText) + 20;
							
							finished = false;
							break;
						}
						if (finished) {
							updateScreenThread.interrupt();
							setCalCooldown();
							finish();
						}
					}
					else {
						long now = System.currentTimeMillis();
						
						if (nextEvent > 0) {
							if (now > nextEvent) {
								nextEvent = 0;
								started = 0;
							}
						} else {
							if (now - started > startWait) {
								if ((-eventText.getLeft()) >= (textWidth - screenWidth)) {
									if (nextBlank == 0) nextBlank = now + endWait;
									
									if (now > nextBlank) {
										nextBlank = 0;
										nextEvent = now + blankTime;
										
										eventText.setLeft(0);
										eventText.setText("");
									}
								} else {
									eventText = (TextView) findViewById(R.id.event_text);
									eventText.setLeft(eventText.getLeft() - 10);
								}
							}
						}
					}
				} catch (Exception e) {
				}
			}
		});
	}
	
	class UpdateTimeTask implements Runnable {
		// @Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					if (running) updateScreen();
					Thread.sleep(50); // Pause of 1 Second
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} catch (Exception e) {
				}
			}
		}
	}
	
	private int getWidth(TextView tv) {
		Rect bounds = new Rect();
		tv.getPaint().getTextBounds(tv.getText().toString(), 0, tv.getText().length(), bounds);
		return bounds.width();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		moveTaskToBack(true);
		running = false;
		return false;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		running = true;
	}
	
	//Don't show calendar events again for another 10 minutes
	private void setCalCooldown() {
		nextCalCheck = System.currentTimeMillis() + (1000 * 60 * 10);
	}
	
}
