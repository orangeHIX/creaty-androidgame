package com.badlogic.androidgames.framework.model;

import android.util.Log;

public class FPSCounter {
	long startTime = System.nanoTime();
	int frames = 0;

	/**
	 * ������Ϸ֡�� frame/second, һ����Game��present����ĩβ�����Ի�ȡ׼ȷ��֡��
	 */
	public void logFrame() {
		frames++;
		if (System.nanoTime() - startTime >= 1000000000) {
			Log.d("FPSCounter", "fps: " + frames);
			frames = 0;
			startTime = System.nanoTime();
		}
	}

}
