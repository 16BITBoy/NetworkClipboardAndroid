package com.adyrhan.networkclipboard.test;


import com.adyrhan.networkclipboard.NetworkClipboardService;
import com.adyrhan.networkclipboard.ServiceControlActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.widget.ToggleButton;

public class ServiceControlActivityTests extends ActivityUnitTestCase<ServiceControlActivity> {

	private Intent mIntent;
	private Context mContext;

	public ServiceControlActivityTests() {
		super(ServiceControlActivity.class);
		
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		mContext = getInstrumentation().getTargetContext();
		mIntent = new Intent(mContext, ServiceControlActivity.class);
		
	}
	
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		mContext.stopService(new Intent(mContext, com.adyrhan.networkclipboard.NetworkClipboardService.class));
		while(NetworkClipboardService.isRunning) {
			Thread.sleep(10);
		}
	}
	
	public void testDisabledServiceStatusCoherence() throws Throwable {
		startActivity(mIntent , null, null);
		
		Activity activity = getActivity();
		
		mContext.stopService(new Intent(mContext, com.adyrhan.networkclipboard.NetworkClipboardService.class));
		while(NetworkClipboardService.isRunning) {
			Thread.sleep(10);
		}
		
		ToggleButton toggle = (ToggleButton) activity.findViewById(com.adyrhan.networkclipboard.R.id.toggle);
		assertEquals(NetworkClipboardService.isRunning, toggle.isChecked());
		
	}
	
	public void testEnabledStatusCoherenceServiceOn() throws Throwable {
		

		mContext.startService(new Intent(mContext, com.adyrhan.networkclipboard.NetworkClipboardService.class));
		while(!NetworkClipboardService.isRunning) {
			Thread.sleep(10);
		}
		startActivity(mIntent , null, null);
		Activity activity = getActivity();
		
		ToggleButton toggle = (ToggleButton) activity.findViewById(com.adyrhan.networkclipboard.R.id.toggle);
		assertEquals(NetworkClipboardService.isRunning, toggle.isChecked());
	}
	
	public void testToggleService() throws Throwable {
		startActivity(mIntent , null, null);

		Activity activity = getActivity();
		ToggleButton toggle = (ToggleButton) activity.findViewById(com.adyrhan.networkclipboard.R.id.toggle);
		
		toggle.performClick();
		while(!NetworkClipboardService.isRunning) {
			Thread.sleep(10);
		}
		assertEquals(true, NetworkClipboardService.isRunning);
		
		toggle.performClick();
		while(NetworkClipboardService.isRunning) {
			Thread.sleep(10);
		}
		assertEquals(false, NetworkClipboardService.isRunning);
	}
}
