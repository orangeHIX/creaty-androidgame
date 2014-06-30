package com.badlogic.androidgames.framework.math;

public class FanShape implements Shape {

	/**������ζ�Ӧ��Բ��*/
	public Circle c;
	/**˳ʱ����ʼ�Ƕȣ�0~360����С��end�������Ƕȣ�*/
	protected float start;
	/**˳ʱ������Ƕȣ�0~360��������start����ʼ�Ƕȣ�*/
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
