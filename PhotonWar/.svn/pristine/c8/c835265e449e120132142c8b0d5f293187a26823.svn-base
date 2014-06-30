package com.badlogic.androidgames.framework.gl;

public class Animation {
	public static final int ANIMATION_LOOPING = 0;
	public static final int ANIMATION_NONLOOPING = 1;

	final TextureRegion[] keyFrames;
	final float frameDuration;

	/**
	 * @param frameDuration
	 *            动画帧间隔
	 * @param keyFrames
	 *            排列好的动画帧
	 */
	public Animation(float frameDuration, TextureRegion... keyFrames) {
		this.frameDuration = frameDuration;
		this.keyFrames = keyFrames;
	}

	/**
	 * 获得动画的某一帧
	 * 
	 * @param stateTime
	 *            与该动画关联实体的状态时间，这关系到返回哪一个动画帧
	 * @param mode
	 *            动画是否循环 ANIMATION_LOOPING-循环，ANIMATION_NONLOOPING-非循环
	 */
	public TextureRegion getKeyFrame(float stateTime, int mode) {
		int frameNumber = (int) (stateTime / frameDuration);

		if (mode == ANIMATION_NONLOOPING) {
			frameNumber = Math.min(keyFrames.length - 1, frameNumber);
		} else {
			frameNumber = frameNumber % keyFrames.length;
		}
		return keyFrames[frameNumber];
	}
}
