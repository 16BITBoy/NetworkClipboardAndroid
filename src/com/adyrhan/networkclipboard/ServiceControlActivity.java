package com.adyrhan.networkclipboard;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ToggleButton;

public class ServiceControlActivity extends Activity {
	private static final String TAG = "ServiceControlActivity";
	public Intent mServiceIntent;
	public ToggleButton mToggle;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_control_activity);
		mServiceIntent = new Intent(this, com.adyrhan.networkclipboard.NetworkClipboardService.class);
		mToggle = (ToggleButton) findViewById(R.id.toggle);
		mToggle.setChecked(NetworkClipboardService.isRunning);
		
	}
	
	public void onServiceToggleStateClicked(View view) {
		Log.d(TAG, "Toggle button pressed");
		ToggleButton toggle = (ToggleButton) view;
		boolean lastState = NetworkClipboardService.isRunning;
		if(lastState) {
			stopService(mServiceIntent);
			BootReceiver.bootStartup = false;
		} else {
			startService(mServiceIntent);
			BootReceiver.bootStartup = true;
		}
		toggle.setEnabled(false);
		CheckServerStatusTask task = new CheckServerStatusTask();
		task.execute(lastState);
		
	}
	
	private class CheckServerStatusTask extends AsyncTask<Boolean, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Boolean...arg0) {
        	try {
	            while(NetworkClipboardService.isRunning == arg0[0]){
					Thread.sleep(10);
	            }
	            return NetworkClipboardService.isRunning;
        	} catch (InterruptedException e) {
				Log.w(TAG, null, e);
				return NetworkClipboardService.isRunning;
			}
        }

        @Override
        protected void onPostExecute(Boolean result) {
        	mToggle.setEnabled(true);
        	mToggle.setChecked(result);
        }

    }
	
}
