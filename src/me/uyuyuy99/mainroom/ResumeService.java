package me.uyuyuy99.mainroom;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import me.uyuyuy99.mainroom.motiondetection.MotionDetectionActivity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class ResumeService extends Service {
	
	private static final String TAG = "ResumeService";
	private final Context baseContext = getBaseContext();
	private static boolean firstTimeBooting = true;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
//		Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");
		
		ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

		// This schedule a runnable task every minute
		scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
			public void run() {
//				Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("me.uyuyuy99.mainroom");
//				startActivity(LaunchIntent);
			}
		}, 5, 5, TimeUnit.MINUTES);
		
//		Log.e("ResumeService", "dumbbbbbbbbbbbbbbbbbbbbbbbbb");
		
		 final IntentFilter filter = new IntentFilter();
		 filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        final BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		boolean screenOn = intent.getBooleanExtra("screen_state", false);
        if (!screenOn) {
//        	Log.e("ResumeService", "SCREEN TURNED OFF");
            // YOUR CODE
//        	MotionDetectionActivity.testing = new Random().nextInt(1000);
        	if (!firstTimeBooting) {
        		Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("me.uyuyuy99.mainroom");
    			startActivity(LaunchIntent);
        	} else {
        		firstTimeBooting = false;
        	}
        } else {
//        	Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("me.uyuyuy99.mainroom");
//			startActivity(LaunchIntent);
//        	MotionDetectionActivity.testing = new Random().nextInt(1000) + 1000;
            // YOUR CODE
        }
	}
	
}
