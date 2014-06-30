package com.badlogic.androidgames.framework.math;

public class Circle implements Shape {
	public final Vector2 center = new Vector2();
	public float radius;

	public Circle(float x, float y, float radius) {
		this.center.set(x, y);
		this.radius = radius;
	}

	@Override
	public int getShape() {
		// TODO Auto-generated method stub
		return Shape.CIRCLE;
	}
}
