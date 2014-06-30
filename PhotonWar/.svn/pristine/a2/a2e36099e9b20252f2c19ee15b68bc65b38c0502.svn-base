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
	/** 轨道半径系数，用于计算轨道半径 */
	public static final float ORBIT_FACTOR1 = 1.4f;
	/** 轨道半径系数，用于计算轨道半径 */
	public static final float ORBIT_FACTOR2 = 0.5f;
	/** 轨道半径系数，用于计算轨道半径 */
	public static final float ORBIT_FACTOR3 = 0.25f;
	/**开发度进度条宽度*/
	public static final float DEVELOP_RING_WIDTH = 0.15f;
	/** 阀值系数——停止生产阀值=等级*阀值系数（参考公式） */
	public static final int THRESHOLD_FACTOR = 10;
	/** 开发速度系数——开发度=当前开发度+开发速度系数/等级（参考公式） */
	public static final float DEVELOP_RATE = 0.5f;
	/** 产能增长系数 */
	public static final float PRODUCTIVITY_GROUTH_RATE = .05f;
	/** 战斗间隔——每次战斗结算事件的时间间隔 */
	public static final float FIGHT_INTERVAL = 0.5f;
	/** 据点所有者每次派遣舰船数目与据点所有者在该据点驻扎的舰船总数的比例 */
	public static final float DISPATCH_PROPOTION = 0.5f;
	/** 据点所有者每次派遣舰船数目下限 */
	public static final int DISPATCH_MIN = 3;
	/** 随机数生成器 */
	public static final Random random = new Random();

	/** 表示据点未被占领，此时据点处于原始形态，所有者为无，开发度为0，产能为0 */
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
	 * 表示据点正在被占据, 进入这个状态时，如果所有者与停靠在此处舰船所有者不符，所有者变更为舰船所有者，开发度重置为0；
	 * 处于此状态开发度会随时间逐渐提升
	 */
	public static final State STATE_OCCUPY = new State(1, "Occupy",
			new StateUpdater() {
				@Override
				public void enter(Object obj) {
					StrongHold hold = (StrongHold) obj;
					if (hold.governShips.isEmpty() && 
							(hold.invadeGroup.shipList.size() > 0)) {
						// 转移舰船2到舰船群1中
						hold.governShips.addAll(hold.invadeGroup.shipList);
						hold.invadeGroup.shipList.clear();
						hold.invadeGroup.state = ShipGroup.NOTHING;
					}
					if (hold.governShips.get(0).owner != hold.owner) {
						hold.changeOwner(hold.governShips.get(0).owner);
						hold.development = 0; // 开发度重置为0
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
	/** 表示据点正处于两方势力的争夺中，处于这个状态时开发度保持不变 */
	public static final State STATE_FIGHT = new State(2, "Fight",
			new StateUpdater() {
				@Override
				public void hold(Object obj) {

				}
			});
	/**
	 * 表示据点为一方势力完全开发，此时开发度应该为100%，所有者变为此时在据点停靠舰船的所有者；处于此状态产能会逐渐提升，同时可能会产生新的舰船，
	 * 舰船归所有者。
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
					// 预计累积产能
					float expectP = hold.accumulatedProductivity
							+ hold.deltaTime
							* FloatMath.floor(hold.productivity);
					// 可以生产的舰船数目
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

	/** 据点状态机 */
	static StateMachine2 STATE_MACHINE;
	static {
		STATE_MACHINE = new StateMachine2(new HoldStateTransitionTester());
		STATE_MACHINE.logSwitch = true;
		STATE_MACHINE.registerState(STATE_BLANK);
		STATE_MACHINE.registerState(STATE_OCCUPY);
		STATE_MACHINE.registerState(STATE_FIGHT);
		STATE_MACHINE.registerState(STATE_HOLD);
	}

	/** 该据点当前状态 */
	public State state;
	/** 所有者 */
	public Race owner;
	/** 轨道最外侧边界半径大小 */
	public final float orbitBound;
	/** 轨道最内侧边界半径大小 */
	public final float orbitInsideBound;
	/** 开发度——开发度为100%可以被一方势力占领 */
	public float development;
	/** 产能——单位时间内积累的产能数目 */
	public float productivity;
	/** 最高产能——产能上限 */
	public final float maxProductivity;
	/** 积累产能——已经积累的产能 积累产能=积累产能+产能*间隔时间（参考公式） */
	public float accumulatedProductivity;
	/** 等级——与据点生产舰船时，停止生产的阀值正相关；与据点开发速度负相关 */
	public final int level;

	/** 舰船群1——占领该据点势力的舰船或者首先登陆该据点势力的舰船 */
	public ArrayList<Ship> governShips;
	/** 防御战舰队伍 */
	public ShipGroup defenseGroup;
	/** 入侵战舰队伍*/
	public ShipGroup invadeGroup;
	/** 战斗计时器——记录战斗开始后已经经历的时间 */
	public float fightTimer;
	/** 获取世界的实例并以此向世界申请资源 */
	public EntityManager entityManager;
	
	/**调试bug用*/
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
	 * @return 返回据点的边界（区别于轨道边界）
	 */
	public float getBoundsRadius() {
		return ((Circle) bounds).radius;
	}

	/** 更新间隔时间 */
	float deltaTime;

	/** 更新据点 */
	public void update(float deltaTime) {
		this.deltaTime = deltaTime;
		state = STATE_MACHINE.updateState(this, state);
		this.deltaTime = 0;
		for (int i = 0; i < governShips.size(); i++) {
			governShips.get(i).update(deltaTime);
		}
	}

	/** 生成新的舰船,据点累积产能将会被消耗 */
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

	/** 派遣行为的执行函数 */
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

	/** 获取应该派遣的舰船数目 */
	protected int getDispatchNum() {
		int dispatch = (int) (governShips.size() * DISPATCH_PROPOTION);
		if (dispatch < DISPATCH_MIN) {
			dispatch = governShips.size();
		}
		return dispatch;
	}

	/** 接管新来的舰船 */
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
	/** 获取该据点的人口容量 */
	public int getPopulationCapacity() {
		return level * DEFAULT_MAX_POPULATION_FACTOR;
	}
}
