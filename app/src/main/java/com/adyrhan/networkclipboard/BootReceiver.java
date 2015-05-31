package com.adyrhan.networkclipboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
	public static boolean bootStartup;
	@Override
	public void onReceive(Context context, Intent intent) {
		if(bootStartup) {
			Intent serviceIntent = new Intent(context, com.adyrhan.networkclipboard.NetworkClipboardService.class);
			context.startService(serviceIntent);
		}

	}

}
