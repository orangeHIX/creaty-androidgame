package com.creaty.photonwar.entity;

import java.util.ArrayList;
import java.util.Random;

import android.os.IBinder;
import android.util.FloatMath;
import android.util.Log;

import com.badlogic.androidgames.framework.Pool;
import com.badlogic.androidgames.framework.Pool.PoolObjectFactory;
import com.badlogic.androidgames.framework.math.Circle;
import com.badlogic.androidgames.framework.math.Vector2;
import com.badlogic.androidgames.framework.model.GameObject;
import com.creaty.game.framework.SmartLog;
import com.creaty.game.framework.State;
import com.creaty.game.framework.State.StateUpdater;
import com.creaty.game.framework.StateMachine2;
import com.creaty.game.framework.StateMachine2.StateTransitionTester;
import com.creaty.photonwar.entity.Race.ShipBlueprint;
import com.creaty.photonwar.inferface.EntityManager;

public class StrongHold extends GameObject {
	public static final String tag = "StrongHold";

	public static final int DEFAULT_MAX_POPULATION_FACTOR = 10;
	/** ����뾶ϵ�������ڼ������뾶 */
	public static final float ORBIT_FACTOR1 = 1.4f;
	/** ����뾶ϵ�������ڼ������뾶 */
	public static final float ORBIT_FACTOR2 = 0.5f;
	/** ����뾶ϵ�������ڼ������뾶 */
	public static final float ORBIT_FACTOR3 = 0.25f;
	/**�����Ƚ��������*/
	public static final float DEVELOP_RING_WIDTH = 0.15f;
	/** ��ֵϵ������ֹͣ������ֵ=�ȼ�*��ֵϵ�����ο���ʽ�� */
	public static final int THRESHOLD_FACTOR = 10;
	/** �����ٶ�ϵ������������=��ǰ������+�����ٶ�ϵ��/�ȼ����ο���ʽ�� */
	public static final float DEVELOP_RATE = 0.5f;
	/** ��������ϵ�� */
	public static final float PRODUCTIVITY_GROUTH_RATE = .05f;
	/** ս���������ÿ��ս�������¼���ʱ���� */
	public static final float FIGHT_INTERVAL = 0.5f;
	/** �ݵ�������ÿ����ǲ������Ŀ��ݵ��������ڸþݵ�פ���Ľ��������ı��� */
	public static final float DISPATCH_PROPOTION = 0.5f;
	/** �ݵ�������ÿ����ǲ������Ŀ���� */
	public static final int DISPATCH_MIN = 3;
	/** ����������� */
	public static final Random random = new Random();

	/** ��ʾ�ݵ�δ��ռ�죬��ʱ�ݵ㴦��ԭʼ��̬��������Ϊ�ޣ�������Ϊ0������Ϊ0 */
	public static final State STATE_BLANK = new State(0, "Blank",
			new StateUpdater() {
				@Override
				public void enter(Object obj) {
					StrongHold hold = (StrongHold) obj;
					hold.owner = null;
					hold.development = 0;
					hold.productivity = 0;
				};
			});
	/**
	 * ��ʾ�ݵ����ڱ�ռ��, �������״̬ʱ�������������ͣ���ڴ˴����������߲����������߱��Ϊ���������ߣ�����������Ϊ0��
	 * ���ڴ�״̬�����Ȼ���ʱ��������
	 */
	public static final State STATE_OCCUPY = new State(1, "Occupy",
			new StateUpdater() {
				@Override
				public void enter(Object obj) {
					StrongHold hold = (StrongHold) obj;
					if (hold.governShips.isEmpty() && 
							(hold.invadeGroup.shipList.size() > 0)) {
						// ת�ƽ���2������Ⱥ1��
						hold.governShips.addAll(hold.invadeGroup.shipList);
						hold.invadeGroup.shipList.clear();
						hold.invadeGroup.state = ShipGroup.NOTHING;
					}
					if (hold.governShips.get(0).owner != hold.owner) {
						hold.changeOwner(hold.governShips.get(0).owner);
						hold.development = 0; // ����������Ϊ0
					}
				};

				@Override
				public void hold(Object obj) {
					StrongHold hold = (StrongHold) obj;
					hold.development += hold.deltaTime
							* StrongHold.DEVELOP_RATE * hold.governShips.size();
					if (hold.development > 100.0f)
						hold.development = 100.0f;
				};
			});
	/** ��ʾ�ݵ����������������������У��������״̬ʱ�����ȱ��ֲ��� */
	public static final State STATE_FIGHT = new State(2, "Fight",
			new StateUpdater() {
				@Override
				public void hold(Object obj) {

				}
			});
	/**
	 * ��ʾ�ݵ�Ϊһ��������ȫ��������ʱ������Ӧ��Ϊ100%�������߱�Ϊ��ʱ�ھݵ�ͣ�������������ߣ����ڴ�״̬���ܻ���������ͬʱ���ܻ�����µĽ�����
	 * �����������ߡ�
	 */
	public static final State STATE_HOLD = new State(3, "Hold",
			new StateUpdater() {
				@Override
				public void hold(Object obj) {
					StrongHold hold = (StrongHold) obj;
					hold.productivity += hold.deltaTime
							* StrongHold.PRODUCTIVITY_GROUTH_RATE;
					if (hold.productivity > hold.maxProductivity) {
						hold.productivity = hold.maxProductivity;
					}
					// Ԥ���ۻ�����
					float expectP = hold.accumulatedProductivity
							+ hold.deltaTime
							* FloatMath.floor(hold.productivity);
					// ���������Ľ�����Ŀ
					int proShip = (int) Math.floor(expectP
							/ hold.owner.shipBlueprint.encon);
					for (int i = 0; i < proShip; i++) {
						hold.produceNewShip();
					}
				}
			});

	public static class HoldStateTransitionTester implements
			StateTransitionTester {

		@Override
		public boolean tryChange(Object objectWithState, State from, State to) {
			StrongHold hold = (StrongHold) objectWithState;
			if (from.equals(STATE_BLANK)) {
				if (to.equals(STATE_OCCUPY)) {
					if (!hold.governShips.isEmpty())
						return true;
				}else if((to.equals(STATE_BLANK))){
					if(hold.governShips.isEmpty()){
						return true;
					}
				}
			} else if (from.equals(STATE_OCCUPY)) {
				if (to.equals(STATE_FIGHT)) {
					if ((hold.defenseGroup.state == ShipGroup.ATTACKING) && 
							(hold.invadeGroup.state == ShipGroup.ATTACKING))
						return true;
				} else if (to.equals(STATE_HOLD)) {
					if (hold.development >= 100.0f)
						return true;
				}else if (to.equals(STATE_OCCUPY)){
					if((hold.defenseGroup.state != ShipGroup.ATTACKING) || 
							(hold.invadeGroup.state != ShipGroup.ATTACKING)
							&& hold.development < 100.0f)
						return true;
				}
			} else if (from.equals(STATE_FIGHT)) {
				if (to.equals(STATE_OCCUPY)) {
					if ((!hold.governShips.isEmpty()) &&
							hold.invadeGroup.state == ShipGroup.NOTHING)
						return true;
				}
			} else if (from.equals(STATE_HOLD)) {
				if (to.equals(STATE_OCCUPY)) {
					if ((!hold.governShips.isEmpty()) &&
							hold.invadeGroup.state == ShipGroup.NOTHING)
						return true;
				} else if (to.equals(STATE_FIGHT)) {
					if ((!hold.governShips.isEmpty()) && 
							(hold.invadeGroup.state == ShipGroup.ATTACKING))
						return true;
				}else if(to.equals(STATE_HOLD)){
					return true;
				}
			}
			return false;
		};

	}

	/** �ݵ�״̬�� */
	static StateMachine2 STATE_MACHINE;
	static {
		STATE_MACHINE = new StateMachine2(new HoldStateTransitionTester());
		STATE_MACHINE.logSwitch = true;
		STATE_MACHINE.registerState(STATE_BLANK);
		STATE_MACHINE.registerState(STATE_OCCUPY);
		STATE_MACHINE.registerState(STATE_FIGHT);
		STATE_MACHINE.registerState(STATE_HOLD);
	}

	/** �þݵ㵱ǰ״̬ */
	public State state;
	/** ������ */
	public Race owner;
	/** ��������߽�뾶��С */
	public final float orbitBound;
	/** ������ڲ�߽�뾶��С */
	public final float orbitInsideBound;
	/** �����ȡ���������Ϊ100%���Ա�һ������ռ�� */
	public float development;
	/** ���ܡ�����λʱ���ڻ��۵Ĳ�����Ŀ */
	public float productivity;
	/** ��߲��ܡ����������� */
	public final float maxProductivity;
	/** ���۲��ܡ����Ѿ����۵Ĳ��� ���۲���=���۲���+����*���ʱ�䣨�ο���ʽ�� */
	public float accumulatedProductivity;
	/** �ȼ�������ݵ���������ʱ��ֹͣ�����ķ�ֵ����أ���ݵ㿪���ٶȸ���� */
	public final int level;

	/** ����Ⱥ1����ռ��þݵ������Ľ����������ȵ�½�þݵ������Ľ��� */
	public ArrayList<Ship> governShips;
	/** ����ս������ */
	public ShipGroup defenseGroup;
	/** ����ս������*/
	public ShipGroup invadeGroup;
	/** ս����ʱ��������¼ս����ʼ���Ѿ�������ʱ�� */
	public float fightTimer;
	/** ��ȡ�����ʵ�����Դ�������������Դ */
	public EntityManager entityManager;
	
	/**����bug��*/
	public SmartLog log;

	public StrongHold(float x, float y, float radius, int maxProductivity,
			int level, EntityManager world, Race owner) {
		super(x, y, radius);
		// TODO Auto-generated constructor stub
		state = STATE_BLANK;
		STATE_MACHINE.start(this, STATE_BLANK);
		this.orbitBound = radius * ORBIT_FACTOR1 + ORBIT_FACTOR2;
		this.orbitInsideBound = radius + ORBIT_FACTOR3;
		this.maxProductivity = maxProductivity;
		this.accumulatedProductivity = 0;
		this.level = level;
		this.entityManager = world;
		this.owner = owner;
		governShips = new ArrayList<Ship>(10);
		defenseGroup = world.GetGroupInstance(this);
		invadeGroup = world.GetGroupInstance(this);
		defenseGroup.state = ShipGroup.NOTHING;
		invadeGroup.state = ShipGroup.NOTHING;
		fightTimer = 0;
		
		log = new SmartLog();
	}

	/**
	 * @return ���ؾݵ�ı߽磨�����ڹ���߽磩
	 */
	public float getBoundsRadius() {
		return ((Circle) bounds).radius;
	}

	/** ���¼��ʱ�� */
	float deltaTime;

	/** ���¾ݵ� */
	public void update(float deltaTime) {
		this.deltaTime = deltaTime;
		state = STATE_MACHINE.updateState(this, state);
		this.deltaTime = 0;
		for (int i = 0; i < governShips.size(); i++) {
			governShips.get(i).update(deltaTime);
		}
	}

	/** �����µĽ���,�ݵ��ۻ����ܽ��ᱻ���� */
	public void produceNewShip() {
		accumulatedProductivity -= owner.shipBlueprint.encon;
		Ship ship = entityManager.GetShipInstance();
		ship = owner.degsinNewShip(ship);
		ship.position.set(position.x + orbitInsideBound + random.nextFloat()
				* (orbitBound - orbitInsideBound), position.y + 0);
		ship.circle(this);
		ship.update(0);
		governShips.add(ship);
	}

	/** ��ǲ��Ϊ��ִ�к��� */
	public void sendShipsTo(StrongHold hold) {
		if (governShips.size() == 0)
			return;
		ShipGroup groupItem = entityManager.GetGroupInstance(this);
		groupItem.state = ShipGroup.GATHERING; 
		int size = governShips.size();
		int dispatch = getDispatchNum();
		groupItem.InitGroupInformation(this, hold);
		for (int i = size - 1; i >= size - dispatch; i--) {
			groupItem.AddShipToGroup(governShips.get(i));
			governShips.get(i).belongGroup = groupItem;
			governShips.remove(i).moveToGather(groupItem);
		}

	}

	/** ��ȡӦ����ǲ�Ľ�����Ŀ */
	protected int getDispatchNum() {
		int dispatch = (int) (governShips.size() * DISPATCH_PROPOTION);
		if (dispatch < DISPATCH_MIN) {
			dispatch = governShips.size();
		}
		return dispatch;
	}

	/** �ӹ������Ľ��� */
	public void takeOverShip(ShipGroup shipGroup) {
		Log.d(tag, "takeOverShip");
		ArrayList<Ship> tempList = shipGroup.shipList;
		int listSize = tempList.size();
		//float boundRadius = getBoundsRadius();
		float movetime;
		for (int i = 0; i < listSize; i++) {
			Ship ship = tempList.get(i);
			ship.velocity.set(this.position).sub(ship.position).nor()
				.mul(ship.basicSpeed);
			movetime = (ship.position.dist(this.position) - orbitInsideBound + random
					.nextFloat() * (orbitBound - orbitInsideBound))
					/ ship.basicSpeed;
			ship.position.add(ship.velocity.x * movetime, ship.velocity.y
					* movetime);
			governShips.add(ship);
			ship.circle(this);
		}
		shipGroup.state = ShipGroup.RECYCLEING;
	}

	public void changeOwner(Race newOwner) {
		owner.RemoveStrongHold(this);
		this.owner = newOwner;
		this.owner.AddStrongHold(this);
	}
	public void ReadyToDenfense(ShipGroup groupItem)
	{
//		int shipSize = governShips.size();
//		if(shipSize == 0)
//			return;
//		defenseGroup.InitGroupInformation(governShips.get(0));
//		for(int i = 0;i < shipSize;i++)
//		{
//			governShips.get(i).belongGroup = defenseGroup;
//			defenseGroup.AddShipToGroup(governShips.get(i));
//		}
//		invadeGroup = groupItem;
//		defenseGroup.state = ShipGroup.FORMATION;
//		defenseGroup.ShowDefenceFormation(invadeGroup.position.x,invadeGroup.position.y,
//				invadeGroup.destination.position.x,
//				invadeGroup.destination.position.y, orbitBound);
//		defenseGroup.destination = invadeGroup.belongHold;
	}
	/** ��ȡ�þݵ���˿����� */
	public int getPopulationCapacity() {
		return level * DEFAULT_MAX_POPULATION_FACTOR;
	}
}
