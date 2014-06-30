package com.creaty.photonwar.entity;

import java.util.Random;

import android.util.Log;

import com.badlogic.androidgames.framework.math.Circle;
import com.badlogic.androidgames.framework.math.Vector2;
import com.badlogic.androidgames.framework.model.DynamicGameObject;

public class Ammunition extends DynamicGameObject{

	public static final int STATE_MOVE = 1;
	public static final int STATE_EXPLODE = 2;
	/** 航行基础速率 */
	public int state;
	public float basicSpeed;
	public Random random = new Random();
	public Ammunition(float x, float y, float radius) {
		super(x, y, radius);
		// TODO Auto-generated constructor stub
		state = STATE_MOVE;
	}
	
	/** 执行航行指令 */
	public void MoveTo(Vector2 destination) {
		state = STATE_MOVE;
		// Log.d(tag, "position " + position + " target " + targetPosition);
		this.velocity.set(destination).sub(position).nor()
				.mul(basicSpeed);
		//Log.d(tag, "change destination to not null");
	}
	protected void updateMove(float deltaTime) {
		position.add(velocity.x * deltaTime, velocity.y * deltaTime);
		((Circle)bounds).center.set(position);
	}
	private void Explode(){
		
	}
	public void update(float deltaTime) {
		switch (state) {
		case STATE_MOVE:
			updateMove(deltaTime);
			break;
		case STATE_EXPLODE:
			break;
		default:
			break;
		}
	}

}
