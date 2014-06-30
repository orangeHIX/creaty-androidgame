package com.creaty.photonwar.entity;

import java.util.ArrayList;
import java.util.Random;

import android.util.FloatMath;
import android.util.Log;

import com.badlogic.androidgames.framework.math.Circle;
import com.badlogic.androidgames.framework.math.Vector2;
import com.badlogic.androidgames.framework.model.DynamicGameObject;


/**ս����Ⱥ����*/
public class ShipGroup extends DynamicGameObject{
	public static final float ANGLE = 60;
	/**���״̬��ע�����GROUP��ʱ���ã����ǲ�����*/
	public static final int NOTHING = -3;
	public static final int FORMATION = -2;
	public static final int GATHERING = -1;
	public static final int VOYAGEING = 0;
	public static final int ATTACKING = 1;
	/**���״̬��ע�����GROUPӦ�������գ���ʱ����*/
	public static final int RECYCLEING = 2;
	public static final int DEFAULT_SIZE = 100;
	public ArrayList<Ship> shipList;
	public int shipLevel;
	/**��ͼ�ð뾶*/
	public float drawRadius;
	/**ս����̽��뾶����ս�뾶��*/
	public float detectRadius;
	/** ���� */
	public int hp;
	/** ������ */
	public int atk;
	/** ������ */
	public int def;
	/** ���� */
	public int encon;
	/** ������ */
	Race owner;
	/** ���л������� */
	public float basicSpeed;
	public int state;
	/**Ŀǰ����ս������*/
	public int shipNumStand = 0;
//	/** ����Ŀ�ĵ�(�Ǿݵ�)*/
//	public Vector2 spaceDestination;
	/**����Ŀ�ĵأ��ݵ�Ŀ�꣩*/
	public StrongHold destination;
	public StrongHold belongHold;
	public ShipGroup(){
		this(0,0,0,DEFAULT_SIZE);
		state = GATHERING;
	}
	/**
	 * @param radius
	 *            ��ײ�����ð뾶
	 */
	public ShipGroup(float x,float y,float radius,int shipNum) {
		// TODO Auto-generated constructor stub
		super(x,y,radius);
		shipList = new ArrayList<Ship>(shipNum);
		state = GATHERING;
	}
	/**����ɵĲ�����Ϣ*/
	public void ClearOldInformation(){
		shipList.clear();
		this.state = NOTHING;
		shipNumStand = 0;
	}
	
	/**����GroupԤ�����ɶ��ٷɴ�
	 * @deprecated shipListӦ�����ã�����Ҫ�µĶ���*/
	public void setGroupScale(int scale){
		shipList = new ArrayList<Ship>(scale);
	}
	/**��ʼ����ս��λ�ľ�����Ϣ*/
	public void InitGroupInformation(StrongHold from, StrongHold to)
	{
		float x1 = from.position.x;
		float y1 = from.position.y;
		float x2 = to.position.x;
		float y2 = to.position.y;
		/** r������Ǿ���С�ӹ�ģ�İ뾶����������ɷɴ��ĵȼ��������ɴ�Խ������뾶Խ�� */
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
	/**��ʼ����ս��λ�ľ�����Ϣ(��ʼ��λ��)*/
	public void InitGroupInformation(float x,float y,float radius,Ship tempShip)
	{
		this.position.x = x;
		this.position.y = y;
		this.drawRadius = radius;
		InitGroupInformation(tempShip);
		((Circle)this.bounds).center.set(position);
		((Circle)this.bounds).radius = radius + tempShip.detectRadius;			
		
	}
	/**��ʼ����ս��λ�ľ�����Ϣ��Ĭ���Ѿ��а뾶��λ����Ϣ��*/
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
	/**����������Ժ���ߵ�ǰ�����Ѿ���ɵ�ʱ��ŵ��ã����������µ�ս���´��µ�����*/
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
	/**������ս���ִＯ�ϵص�ʱ���ã�ʹGroup֪�����Ѿ�������ͬʱ��ʽ�������*/
	public void SingleShipAwairOrder(Ship ship)
	{
		shipNumStand++;
		if(shipList.size() == shipNumStand)
		{
			InitOrderToShips();
		}
	}
	/**�趨��ս��λǰ��Ŀ��*/
	public void SetGroupDestination(StrongHold hold){
		this.destination = hold;
	}
	/**������ս��λ�����Ƶ�����ս��*/
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
	//�мǻع�����״̬�Ժ����Ŀ�ĵأ��������״̬
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
	/**ս��չ����������*/
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
	/**��Ⱥ�������*/
	public void ReadyToAttack()
	{
		InitOrderToShips();
		
	}
	/**����Ŀ�ĵ�λ�ø�������ս�����ٶ�*/
	private void updateShipListDestination(){
		if(state != VOYAGEING)
			return;
		for(int i = 0;i < shipList.size();i++)
		{
			shipList.get(i).moveTo(destination);
		}
	}

}
