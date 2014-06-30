package com.badlogic.androidgames.framework.math;

public class Rectangle implements Shape {
	public final Vector2 lowerLeft;
	public float width, height;

	public Rectangle(float x, float y, float width, float height) {
		this.lowerLeft = new Vector2(x, y);
		this.width = width;
		this.height = height;
	}

	@Override
	public int getShape() {
		// TODO Auto-generated method stub
		return Shape.RECTANGLE;
	}
}
