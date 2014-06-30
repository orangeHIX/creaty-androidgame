package com.creaty.photonwar.entity;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.badlogic.androidgames.framework.Pool;
import com.badlogic.androidgames.framework.Pool.PoolObjectFactory;
import com.badlogic.androidgames.framework.math.Circle;
import com.badlogic.androidgames.framework.math.OverlapTester;
import com.creaty.photonwar.SpatialHashGridForWar;
import com.creaty.photonwar.entity.Race.ShipBlueprint;
import com.creaty.photonwar.inferface.EntityManager;

public class World implements EntityManager {
	public static final String tag = "World";

	public interface WorldListener {

	}

	public static final float WORLD_WIDTH = 20;
	public static final float WORLD_HEIGHT = 12;

	WorldListener listener;
	/** 世界掌管的游戏实体只有2个，据点和作战单位（作战群体） */
	public ArrayList<StrongHold> strongHolds;
	public ArrayList<ShipGroup> shipGroups;
	public Race race1;
	public Race race2;

	Pool<Ship> shipPool;
	Pool<ShipGroup> shipGroupPool;
	Pool<Ammunition> AmmunitionPool;
	public final int shipPoolSize;
	public final int shipGroupSize;

	SpatialHashGridForWar grid;

	public World(WorldListener worldListener) {

		listener = worldListener;
		grid = new SpatialHashGridForWar(WORLD_WIDTH, WORLD_HEIGHT, 4.0f);
		shipPoolSize = 100;
		shipGroupSize = 100;
		shipGroups = new ArrayList<ShipGroup>();
		shipPool = new Pool<Ship>(new PoolObjectFactory<Ship>() {

			@Override
			public Ship createObject() {
				// TODO Auto-generated method stub
				return new Ship();
			}
		}, shipPoolSize);
		shipGroupPool = new Pool<ShipGroup>(new PoolObjectFactory<ShipGroup>() {

			@Override
			public ShipGroup createObject() {
				// TODO Auto-generated method stub
				return new ShipGroup();
			}

		}, shipGroupSize);
		AmmunitionPool = new Pool<Ammunition>(new PoolObjectFactory<Ammunition>() {

			@Override
			public Ammunition createObject() {
				// TODO Auto-generated method stub
				
				return null;
			}
			
		}, maxSize)
		strongHolds = new ArrayList<StrongHold>();
		ShipBlueprint shipBlueprint = new ShipBlueprint(Ship.MINITYPE, 1.0f, 10, 1, 1, 2.0f, 1);
		race1 = new Race(0, Race.HU_PLAYER, shipBlueprint);
		race2 = new Race(1, Race.AI_PLAYER, shipBlueprint);
		strongHolds.add(new StrongHold(1f, 1f, 1.0f, 3, 1, this, race1));
		strongHolds.add(new StrongHold(10f, 5f, 1.0f, 3, 1, this, race2));
		strongHolds.add(new StrongHold(19f, 10f, 1.0f, 3, 1, this, race1 ));
		int holdsize = strongHolds.size();

		for (int j = 0; j < holdsize; j++) {
			grid.insertStaticObject(strongHolds.get(j));
			for (int i = 0; i < 50; i++) {
				strongHolds.get(j).produceNewShip();
				strongHolds.get(j).accumulatedProductivity = 0;
			}
		}
	}

	List<StrongHold> potentialTarget;

	// int count = 0;
	/** 更新据点和作战单位极其托管的战舰 */
	public void update(float deltaTime) {
		int holdsize = strongHolds.size();
		/** 更新据点的状态以及他托管的战舰 */
		for (int i = 0; i < holdsize; i++) {
			strongHolds.get(i).update(deltaTime);
		}
		int groupSize = shipGroups.size();
		/** 更新作战单位以及其托管的战舰 */
		for (int i = 0; i < groupSize; i++) {
			ShipGroup temp = shipGroups.get(i);
			if(temp.state == ShipGroup.RECYCLEING)
			{
				FreeGroupInstance(temp);
				groupSize--;
			}
			temp.update(deltaTime);
		}
		StrongHold hold;
		ShipGroup shipGroupItem;
		int count = 0;
		for (int i = 0; i < groupSize; i++) {
			shipGroupItem = shipGroups.get(i);
			/** 直接跳过没设定目的地的 */
			if (shipGroupItem.destination == null) {
				continue;
			}
			// SmartLog.infor.putString("i", ""+-1);
			// SmartLog.logPerStep(tag, 1);
			potentialTarget = grid.getPotentialTarget(shipGroupItem);
			int sizepo = potentialTarget.size();
			for (int j = 0; j < sizepo; j++) {
				hold = (StrongHold) potentialTarget.get(j);
				if (OverlapTester.overlapCircles((Circle) hold.bounds,
						(Circle) shipGroupItem.bounds)) {
					if (shipGroupItem.owner == hold.owner) {
						hold.takeOverShip(shipGroupItem);
						Log.d(tag, "接管开始");
					} else {
						if (shipGroupItem.state != ShipGroup.ATTACKING && 
								shipGroupItem.state != ShipGroup.FORMATION) {
							shipGroupItem.ReadyToAttack();
							hold.ReadyToDenfense(shipGroupItem);
							Log.d(tag, "攻击开始");
						}
					}
				}
			}
		}
	}

	@Override
	public ShipGroup GetGroupInstance(StrongHold requset) {
		// TODO Auto-generated method stub
		ShipGroup shipGroup = shipGroupPool.newObject();
		// ships.add(ship);
		grid.insertDynamicObject(shipGroup);
		shipGroup.belongHold = requset;
		shipGroups.add(shipGroup);
		return shipGroup;
	}

	@Override
	public void FreeGroupInstance(ShipGroup item) {
		// TODO Auto-generated method stub
		item.ClearOldInformation();	//清理是很重要的！
		shipGroupPool.free(item);
		shipGroups.remove(item);
		grid.removeObject(item);
	}

	@Override
	public Ship GetShipInstance() {
		// TODO Auto-generated method stub
		Ship shipItem = shipPool.newObject();
		grid.insertDynamicObject(shipItem);
		return shipItem;
	}

	@Override
	public void FreeShipInstance(Ship item) {
		// TODO Auto-generated method stub
		shipPool.free(item);
		grid.removeObject(item);
	}
}
