package com.badlogic.androidgames.framework.math;

public class FanShape implements Shape {

	/**与此扇形对应的圆形*/
	public Circle c;
	/**顺时针起始角度（0~360），小于end（结束角度）*/
	protected float start;
	/**顺时针结束角度（0~360），大于start（起始角度）*/
	protected float end;
	
	public FanShape(float x, float y, float radius, float start, float end) {
		c = new Circle(x, y, radius);
		if( start >= 0f && start <= 360f){
			this.start = start;
		}else{
			this.start = 0;
		}
		if( end >= 0f && end <= 360f
				&& end >= start ){
			this.end = end;
		}else{
			this.end = start;
		}
	}
	
	public final float getStart() {
		return start;
	}

	public void setStart(float start) {
		if( start >= 0f && start <= 360f){
			this.start = start;
		}else{
			this.start = 0;
		}
	}

	public final float getEnd() {
		return end;
	}

	public void setEnd(float end) {
		if( end >= 0f && end <= 360f
				&& end >= start ){
			this.end = end;
		}else{
			this.end = start;
		}
	}

	@Override
	public int getShape() {
		// TODO Auto-generated method stub
		return FAN_SHAPE;
	}


}
