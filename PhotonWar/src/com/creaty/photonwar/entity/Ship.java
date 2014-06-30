package com.creaty.photonwar.entity;

import java.util.Random;

import android.R.integer;
import android.util.FloatMath;
import android.util.Log;

import com.badlogic.androidgames.framework.math.Circle;
import com.badlogic.androidgames.framework.math.OverlapTester;
import com.badlogic.androidgames.framework.math.Rectangle;
import com.badlogic.androidgames.framework.math.Vector2;
import com.badlogic.androidgames.framework.model.DynamicGameObject;

public class Ship extends DynamicGameObject {
	public static final String tag = "Ship";
	/**战舰处于停止状态*/
	public boolean isClockwise = false;
	public static final int STATE_FORMATION = -3;
	public static final int STATE_ATTACK = -2;
	public static final int STATE_STOP = -1;
	public static final int STATE_MOVE = 0;
	public static final int STATE_CIRCLE = 1;
	/**另一种飞行状态，飞行到目的地以后停止*/
	public static final int STATE_MOVE_TO_GATHER = 2;
	/**战舰级别为微型（战斗机*/
	public static final int MINITYPE = 3;
	/**战舰级别为轻型（护卫舰级）*/
	public static final int LIGHTTYPE = 4;
	/**战舰级别为中型（驱逐舰级）*/
	public static final int MIDDLETYPE = 5;
	/**战舰级别为大型（巡洋舰级）*/
	public static final int BIGTYPE = 6;
	/**战舰级别为重型（战列舰级）*/
	public static final int WEIGHTTYPE = 7;	

	protected int state = 0;

	/** 环绕角速度 单位：角度/秒；顺时针方向为正方向 */
	public float angularVelocity;
	/** 环绕中心 */
	public Vector2 rotationCenter;
	/**战舰级别*/
	public int shipLevel;
	/**战舰的探测半径（作战半径）*/
	public float detectRadius;
	/** 生命 */
	public int hp;
	/** 攻击力 */
	public int atk;
	/** 防御力 */
	public int def;
	/** 所有者 */
	Race owner;
	/** 航行基础速率 */
	public float basicSpeed;

	/** 航行目的地(非据点)*/
	public Vector2 spaceDestination;
	/**航行目的地（据点目标）*/
	public StrongHold destination;
	public Random random = new Random();
	/**所属的作战单位*/
	public ShipGroup belongGroup = null;

	public Ship() {
		this(0, 0, 0,null, .0f, STATE_MOVE,0,MINITYPE);
	}

	public Ship(float x, float y, float radius, Race owner, float speed,
			int state, int detectRadius, int shipLevel) {
		super(x, y, radius);
		// TODO Auto-generated constructor stub
		angularVelocity = 0;
		rotationCenter = new Vector2();
		this.spaceDestination = new Vector2();
		this.owner = owner;
		this.basicSpeed = speed;
		this.state = state;
		this.detectRadius = detectRadius;
		this.shipLevel = shipLevel;
	}
		
	/**
	 * 重置舰船
	 * 
	 * @param angularVelocity
	 *            舰船角速度
	 * @param rotationCenter
	 *            舰船环绕中心点
	 * @param shipLevel
	 *            战舰级别
	 * @param detectRadius
	 *            战舰的探测半径（作战半径）
	 * @param hp
	 *            生命
	 * @param atk
	 *            攻击
	 * @param def
	 *            防御
	 * @param owner
	 *            所有者
	 * @param basicSpeed
	 *            基础速度
	 * @param spaceDestination
	 *            航行目的地(非据点)
	 * @param destination
	 *            航行目的地（据点目标）
	 * @param belongGroup
	 *            所属的作战单位
	 */
	public void resetShip(float angularVelocity, Vector2 rotationCenter,
			int shipLevel, float detectRadius, int hp, int atk, int def,
			Race owner, float basicSpeed, Vector2 spaceDestination,
			StrongHold destination, ShipGroup belongGroup) {
		this.angularVelocity = angularVelocity;
		this.rotationCenter.set(rotationCenter);
		this.shipLevel = shipLevel;
		this.detectRadius = detectRadius;
		this.hp = hp;
		this.atk = atk;
		this.def = def;
		this.owner = owner;
		this.basicSpeed = basicSpeed;
		this.spaceDestination.set(spaceDestination);
		this.destination = destination;
		this.belongGroup = belongGroup;
	}

	/**
	 * 清除残留信息
	 */
	public void ClearOldInformation() {
		//not implemented
	}

	/** 执行航行指令 */
	public void moveTo(StrongHold stronghold) {
		state = STATE_MOVE;
		// Log.d(tag, "position " + position + " target " + targetPosition);
		this.velocity.set(stronghold.position).sub(position).nor()
				.mul(basicSpeed);
		destination = stronghold;
		//Log.d(tag, "change destination to not null");
	}
	
	public void MoveToShowFormation(float x,float y){
		state = STATE_FORMATION;
		spaceDestination.x = x;
		spaceDestination.y = y;
		this.velocity.set(spaceDestination).sub(position).nor().mul(basicSpeed);
	}
	/**去集合点所确定的作战单位附近集合*/
	public void moveToGather(ShipGroup groupItem) {

		Circle circle = ((Circle)groupItem.bounds);
		float drawRadius = groupItem.drawRadius;
		state = STATE_MOVE_TO_GATHER;
		spaceDestination.x = circle.center.x - drawRadius + random.nextFloat()
				* drawRadius * 2;
		float upperY = FloatMath.sqrt(drawRadius * drawRadius
				- (spaceDestination.x - circle.center.x)
				* (spaceDestination.x - circle.center.x))
				+ circle.center.y;
		float bottomY = circle.center.y
				- FloatMath.sqrt(drawRadius * drawRadius
						- (spaceDestination.x - circle.center.x)
				*(spaceDestination.x - circle.center.x));
		spaceDestination.y = bottomY + (upperY - bottomY)*random.nextFloat();
		
		this.velocity.set(spaceDestination).sub(position).nor().mul(basicSpeed);
	}

	/**飞船停止在原地*/
	public void StopMove(){
		//this.velocity.set(0, 0);
		if (state == STATE_MOVE_TO_GATHER || state == STATE_FORMATION) {
			this.state = STATE_STOP;
			this.belongGroup.SingleShipAwairOrder(this);
		}
		// 行进中停止？发生战斗？
		else if (state == STATE_MOVE) {
			this.state = STATE_STOP;
		}
	}

	/** 执行环绕指令指令 */
	public void circle(StrongHold stronghold) {
		state = STATE_CIRCLE;
		destination = null;
		//Log.d(tag, "change destination to null");
		float distance = this.position.dist(stronghold.position);
		angularVelocity = (float) ((basicSpeed / distance) / Math.PI / distance * 180.f);
		rotationCenter.set(stronghold.position);
	}
	public void SetOrientation(float targetX,float targetY)
	{
		this.velocity.set(targetX,targetY).sub(position).nor();
	}
	public void update(float deltaTime) {
		switch (state) {
		case STATE_MOVE:
			updateMove(deltaTime);
			break;
		case STATE_CIRCLE:
			updateCircle(deltaTime);
			break;
		case STATE_MOVE_TO_GATHER:
			UpdateMoveToGather(deltaTime);
			break;
		case STATE_FORMATION:
			UpdateMoveToGather(deltaTime);
			break;
		case STATE_STOP:
			updateStop(deltaTime);
			break;
		default:
			Log.d("Ship update", "never should be here!");
		}
	}

	protected  void  UpdateMoveToGather(float deltaTime) {
		position.add(velocity.x * deltaTime, velocity.y * deltaTime);
		((Circle)bounds).center.set(position);
		if (Math.abs(position.x - spaceDestination.x) <= 0.1
				&& Math.abs(position.y - spaceDestination.y) <= 0.1) {
			position.x = spaceDestination.x;
			position.y = spaceDestination.y;
			StopMove();
		}
	}
	protected void updateMove(float deltaTime) {
		position.add(velocity.x * deltaTime, velocity.y * deltaTime);
		((Circle)bounds).center.set(position);

	}

	protected void updateCircle(float deltaTime) {
		if(isClockwise == false)
		{
			position.rotate(angularVelocity * deltaTime, rotationCenter);
			((Circle)bounds).center.set(position);
		}
		else 
		{
			position.rotate(-angularVelocity * deltaTime, rotationCenter);
			((Circle)bounds).center.set(position);
		}
	}
	protected void updateStop(float deltaTime){
		//right now, do nothing here
	}

	public final int getState() {
		return state;
	}
}
