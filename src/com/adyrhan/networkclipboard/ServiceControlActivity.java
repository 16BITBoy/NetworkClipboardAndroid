package com.adyrhan.networkclipboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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
		
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifiManager.getConnectionInfo();
		int intip = info.getIpAddress();
		String ip;
		
		ip = (intip & 0xFF) + "." +
	         ((intip >> 8 ) & 0xFF) + "." +
	         ((intip >> 16 ) & 0xFF) + "." +
	         ((intip >> 24 ) & 0xFF);
		
		TextView ipTv = (TextView) findViewById(R.id.ipAddr);
		ipTv.setText(ip);
		TextView portTv = (TextView) findViewById(R.id.portNum);
		portTv.setText(Integer.toString(NetworkClipboardService.LISTENING_PORT));
		
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
