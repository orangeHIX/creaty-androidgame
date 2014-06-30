package com.badlogic.androidgames.framework.math;

public interface Shape {

	public static final int RECTANGLE = 0;
	public static final int CIRCLE = 1;
	public static final int FAN_SHAPE = 2;
	/** 返回形状的代码，例如形状是方形 返回Shape.RECTANGLE */
	public int getShape();

}
