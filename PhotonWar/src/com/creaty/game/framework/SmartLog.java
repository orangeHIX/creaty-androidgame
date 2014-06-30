package com.creaty.game.framework;

import android.util.Log;

public class SmartLog {

	long startTime = System.nanoTime();
	
	public void logIntPS( String tag, int data ){

		if (System.nanoTime() - startTime >= 1000000000) {
			Log.d("SmartLog", tag + ": " + data);

			startTime = System.nanoTime();
		}
	}
	public void logFloatPS( String tag, float data ){

		if (System.nanoTime() - startTime >= 1000000000) {
			Log.d("SmartLog", tag + "fps: " + data);

			startTime = System.nanoTime();
		}
	}

}
