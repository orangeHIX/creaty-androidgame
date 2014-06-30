package com.creaty.photonwar.entity;

import java.util.ArrayList;
import java.util.Random;

import android.util.FloatMath;
import android.util.Log;

import com.badlogic.androidgames.framework.math.Circle;
import com.badlogic.androidgames.framework.math.Vector2;
import com.badlogic.androidgames.framework.model.DynamicGameObject;


/**战舰集群定义*/
public class ShipGroup extends DynamicGameObject{
	public static final float ANGLE = 60;
	/**这个状态标注出这个GROUP暂时不用，但是不回收*/
	public static final int NOTHING = -3;
	public static final int FORMATION = -2;
	public static final int GATHERING = -1;
	public static final int VOYAGEING = 0;
	public static final int ATTACKING = 1;
	/**这个状态标注出这个GROUP应当被回收，暂时弃用*/
	public static final int RECYCLEING = 2;
	public static final int DEFAULT_SIZE = 100;
	public ArrayList<Ship> shipList;
	public int shipLevel;
	/**绘图用半径*/
	public float drawRadius;
	/**战舰的探测半径（作战半径）*/
	public float detectRadius;
	/** 生命 */
	public int hp;
	/** 攻击力 */
	public int atk;
	/** 防御力 */
	public int def;
	/** 耗能 */
	public int encon;
	/** 所有者 */
	Race owner;
	/** 航行基础速率 */
	public float basicSpeed;
	public int state;
	/**目前集合战舰数量*/
	public int shipNumStand = 0;
//	/** 航行目的地(非据点)*/
//	public Vector2 spaceDestination;
	/**航行目的地（据点目标）*/
	public StrongHold destination;
	public StrongHold belongHold;
	public ShipGroup(){
		this(0,0,0,DEFAULT_SIZE);
		state = GATHERING;
	}
	/**
	 * @param radius
	 *            碰撞测试用半径
	 */
	public ShipGroup(float x,float y,float radius,int shipNum) {
		// TODO Auto-generated constructor stub
		super(x,y,radius);
		shipList = new ArrayList<Ship>(shipNum);
		state = GATHERING;
	}
	/**清除旧的残留信息*/
	public void ClearOldInformation(){
		shipList.clear();
		this.state = NOTHING;
		shipNumStand = 0;
	}
	
	/**设置Group预定容纳多少飞船
	 * @deprecated shipList应该重用，不需要新的对象*/
	public void setGroupScale(int scale){
		shipList = new ArrayList<Ship>(scale);
	}
	/**初始化作战单位的具体信息*/
	public void InitGroupInformation(StrongHold from, StrongHold to)
	{
		float x1 = from.position.x;
		float y1 = from.position.y;
		float x2 = to.position.x;
		float y2 = to.position.y;
		/** r代表的是决定小队规模的半径，他由所组成飞船的等级决定，飞船越大。这个半径越大 */
		float r = 0;
		int shiplevel = from.governShips.get(0).shipLevel;
		if (shiplevel == Ship.MINITYPE)
			r = 0.5f;
		else if(shiplevel == Ship.MIDDLETYPE)
		{
			
		}
		else if(shiplevel == Ship.LIGHTTYPE)
		{
			
		}
		else if(shiplevel == Ship.BIGTYPE)
		{
			
		}
		float ratio = (from.orbitBound + r)
				/ (FloatMath
						.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1)));
		float groupY = ratio * (y2 - y1) + y1;
		float groupX = ratio * (x2 - x1) + x1;
		Ship tempShip = from.governShips.get(0);
		InitGroupInformation(groupX, groupY, r, tempShip);
		SetGroupDestination(to);
	}
	/**初始化作战单位的具体信息(初始化位置)*/
	public void InitGroupInformation(float x,float y,float radius,Ship tempShip)
	{
		this.position.x = x;
		this.position.y = y;
		this.drawRadius = radius;
		InitGroupInformation(tempShip);
		((Circle)this.bounds).center.set(position);
		((Circle)this.bounds).radius = radius + tempShip.detectRadius;			
		
	}
	/**初始化作战单位的具体信息（默认已经有半径和位置信息）*/
	public void InitGroupInformation(Ship tempShip)
	{
//		tempShip = shipList.get(0);
		this.detectRadius = tempShip.detectRadius;
		this.atk = tempShip.atk;
		this.basicSpeed = tempShip.basicSpeed;
		this.def = tempShip.def;
		this.hp = tempShip.hp;
		this.owner = tempShip.owner;
		this.shipLevel = tempShip.shipLevel;
		
	}
	/**当集合完毕以后或者当前命令已经完成的时候才调用，用来给麾下的战舰下达新的命令*/
	private void InitOrderToShips(){
		if(state == GATHERING)
		{
			state = VOYAGEING;
			this.velocity.set(destination.position).sub(position).nor().mul(basicSpeed);
			updateShipListDestination();
		}
		else if(state == VOYAGEING)
		{
			this.velocity.set(0, 0);
			shipNumStand = 0;
			state = FORMATION;
			ShowFormation();
		}
		else if(state == FORMATION)
		{
			for(int i = 0;i < shipList.size();i++){
				Ship shipTemp = shipList.get(i);
				shipTemp.isClockwise = true;
				shipTemp.circle(destination);
				
			}
			state = ATTACKING;
		}
		
	}
	public void AddShipToGroup(Ship ship)
	{
		this.shipList.add(ship);
	}
	/**当单个战舰抵达集合地点时调用，使Group知道他已经就绪，同时正式加入编制*/
	public void SingleShipAwairOrder(Ship ship)
	{
		shipNumStand++;
		if(shipList.size() == shipNumStand)
		{
			InitOrderToShips();
		}
	}
	/**设定作战单位前进目标*/
	public void SetGroupDestination(StrongHold hold){
		this.destination = hold;
	}
	/**更新作战单位所控制的所有战舰*/
	public void update(float deltatime)
	{
		if(state == GATHERING || state == FORMATION){
			for(int i = 0;i < shipList.size();i++)
			{
				shipList.get(i).update(deltatime);
			}
		}
		else if(state == VOYAGEING)
		{	
			for(int i = 0;i < shipList.size();i++)
			{
				shipList.get(i).update(deltatime);
			}
			position.add(velocity.x * deltatime, velocity.y * deltatime);
			((Circle)bounds).center.set(position);
		}
		else if(state == ATTACKING)
		{
			for(int i = 0;i < shipList.size();i++)
			{
				shipList.get(i).update(deltatime);
			}

		}
		else if(state == RECYCLEING)
		{
			
		}
	}
	//切记回归正常状态以后清空目的地，所属组等状态
	public void ShowDefenceFormation(float groupX,float groupY,float holdX,float holdY
			,float distance){
		float tempY = groupY - holdY;
		float tempX = groupX - holdX;
		float degree = (float) ((float) (Math.atan((groupY - holdY)/(groupX - holdX)))*180/Math.PI);
		float minDegree = 0f;
		float maxDegree = 0f;
		if(tempY >= 0 && tempX < 0)
		{	
			minDegree = (degree + 90) - ANGLE/2;
			maxDegree = (degree + 90) + ANGLE/2;
		}
		if(tempY >=0 && tempX >= 0)
		{
			minDegree = degree - ANGLE/2;
			maxDegree = degree + ANGLE/2;
		}
		else if(tempY < 0 && tempX <= 0)
		{
			minDegree = degree + 180 - ANGLE/2;
			maxDegree = degree + 180 + ANGLE/2;
		}
		else if(tempY < 0 && tempX > 0)
		{
			minDegree = degree + 360 - ANGLE/2;
			maxDegree = degree + 360 + ANGLE/2;
		}
		int sizeTemp = shipList.size();
		float ratio = (maxDegree - minDegree)/(sizeTemp - 1);
		for(int i = 0;i < sizeTemp;i++)
		{
			float tempRatio = minDegree + ratio*(i);
			float sinValue = FloatMath.sin((float) (tempRatio/180*Math.PI));
			float cosValue = FloatMath.cos((float) (tempRatio/180*Math.PI));
			float x = holdX + distance * cosValue;
			float y = holdY + distance * sinValue;
			shipList.get(i).MoveToShowFormation(x,y);
		}
	}
	/**战舰展开进攻队形*/
	public void ShowFormation()
	{
		float groupY = position.y;
		float groupX = position.x;
		float holdX = destination.position.x;
		float holdY = destination.position.y;
		float tempY = groupY - holdY;
		float tempX = groupX - holdX;
		float radius  = FloatMath.sqrt((groupY - holdY)*(groupY - holdY) + (groupX - holdX)*(groupX - holdX));
		float degree = (float) ((float) (Math.atan((groupY - holdY)/(groupX - holdX)))*180/Math.PI);
		float minDegree = 0f;
		float maxDegree = 0f;
		if(tempY >= 0 && tempX < 0)
		{	
			minDegree = (degree + 90) - ANGLE/2;
			maxDegree = (degree + 90) + ANGLE/2;
		}
		if(tempY >=0 && tempX >= 0)
		{
			minDegree = degree - ANGLE/2;
			maxDegree = degree + ANGLE/2;
		}
		else if(tempY < 0 && tempX <= 0)
		{
			minDegree = degree + 180 - ANGLE/2;
			maxDegree = degree + 180 + ANGLE/2;
		}
		else if(tempY < 0 && tempX > 0)
		{
			minDegree = degree + 360 - ANGLE/2;
			maxDegree = degree + 360 + ANGLE/2;
		}
		int sizeTemp = shipList.size();
		float distance = FloatMath.sqrt((groupX - holdX)*(groupX - holdX)
				+ (groupY - holdY)*(groupY - holdY));
		float ratio = (maxDegree - minDegree)/(sizeTemp - 1);
		Random rand = new Random();
		for(int i = 0;i < sizeTemp;i++)
		{
			float tempRatio = minDegree + ratio*(i);
			float sinValue = FloatMath.sin((float) (tempRatio/180*Math.PI));
			float cosValue = FloatMath.cos((float) (tempRatio/180*Math.PI));
			float sign = rand.nextFloat();
			float x = 0;
			float y = 0;
			if(sign <= (1.0f/3.0f))
			{	
				x = holdX + distance * cosValue;
				y = holdY + distance * sinValue;
			}
			else if(sign <= (2.0f/3.0f))
			{
				x = holdX + (distance + 0.2f) * cosValue;
				y = holdY + (distance + 0.2f) * sinValue;
			}
			else
			{
				x = holdX + (distance + 0.4f) * cosValue;
				y = holdY + (distance + 0.4f) * sinValue;
			}
			shipList.get(i).MoveToShowFormation(x,y);
		}
	}
	/**集群发起进攻*/
	public void ReadyToAttack()
	{
		InitOrderToShips();
		
	}
	/**依据目的地位置更新所有战舰的速度*/
	private void updateShipListDestination(){
		if(state != VOYAGEING)
			return;
		for(int i = 0;i < shipList.size();i++)
		{
			shipList.get(i).moveTo(destination);
		}
	}

}
