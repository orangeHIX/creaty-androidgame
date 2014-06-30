package com.creaty.photonwar.entity;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.androidgames.framework.math.Vector2;

/*����*/
public class Race {

	public static class ShipBlueprint {
		/** ս������ */
		public int shipLevel;
		/** ս����̽��뾶����ս�뾶�� */
		public float detectRadius;
		/** ���� */
		public int hp;
		/** ������ */
		public int atk;
		/** ������ */
		public int def;
		/** ���л������� */
		public float basicSpeed;

		/** ���� */
		public float encon;

		public ShipBlueprint(int shipLevel, float detectRadius, int hp, int atk,
				int def, float basicSpeed, float encon) {
			super();
			this.shipLevel = shipLevel;
			this.detectRadius = detectRadius;
			this.hp = hp;
			this.atk = atk;
			this.def = def;
			this.basicSpeed = basicSpeed;
			this.encon = encon;
		}
	}

	public static final int AI_PLAYER = 0;
	public static final int HU_PLAYER = 1;
	private static final int INIT_NUM_STRONGHOLD = 5;
	// /**����ӵ�е����˿�����*/
	// protected int maxPopulation = 0;
	// /**����ӵ�е����˿�*/
	// public int population = 0;
	/** ����ӵ�еĽ��� */
	private List<StrongHold> allStrongHold;
	/** ����ID��Ψһ���� */
	public final int raceId;
	/** �жϲ�������AI����Human */
	public final int master;
	/** ���������ͼ��ÿ���������ݸ��ԵĽ�����ͼ�������� */
	public final ShipBlueprint shipBlueprint;

	public Race(int id, int master, ShipBlueprint shipBlueprint) {
		this.raceId = id;
		this.master = master;
		this.shipBlueprint = shipBlueprint;
		allStrongHold = new ArrayList<StrongHold>(INIT_NUM_STRONGHOLD);
	}

	// public Race(int id, ArrayList<StrongHold> strongHoldList, boolean aiOrHu)
	// {
	// this.raceId = id;
	// this.master = aiOrHu ? HU_PLAYER : AI_PLAYER;
	// this.allStrongHold = strongHoldList;
	// }

	public boolean AddStrongHold(StrongHold strongHoldItem) {
		if (strongHoldItem == null || allStrongHold.contains(strongHoldItem))
			return false;
		allStrongHold.add(strongHoldItem);
		// maxPopulation += strongHoldItem.getPopulationCapacity();
		return true;
	}

	public boolean RemoveStrongHold(StrongHold strongHoldItem) {
		if (strongHoldItem == null || !allStrongHold.contains(strongHoldItem))
			return false;
		allStrongHold.remove(strongHoldItem);
		// maxPopulation -= strongHoldItem.getPopulationCapacity();
		return true;
	}

	public boolean SetStrongHoldList(ArrayList<StrongHold> strongHoldList) {
		if (strongHoldList == null)
			return false;
		this.allStrongHold = strongHoldList;
		return true;
	}

	/**����������װ������*/
	private Vector2 emp = new Vector2();
	/**
	 * ���������Ľ�����ͼ��װ����.<br>
	 * ���ﲻʹ��ship��ClearOldInformation()������
	 * ��Ϊ������������Զ�������
	 * @param ship
	 *            ��Ҫ��װ�ķɴ�
	 * @param ��װ��ķɴ�
	 */
	public Ship degsinNewShip(Ship ship) {
		ship.resetShip(0, emp, shipBlueprint.shipLevel,
				shipBlueprint.detectRadius, shipBlueprint.hp,
				shipBlueprint.atk, shipBlueprint.def, this,
				shipBlueprint.basicSpeed, emp, null, null);
		return ship;
	}

	// /**�������ӵ�е����˿�*/
	// public int getPopulation() {
	// int size = allStrongHold.size();
	// int population = 0;
	// for(int i = 0; i < size; i++){
	// StrongHold hold = allStrongHold.get(i);
	// population += hold.ships1.size();
	// }
	// return population;
	// }

}
// public class Role {
//
// /**�ս�ɫ��ʾ*/
// public static final int ROLE_BLANK = 0;
// public static final int ROLE_PLAYER = 1;
// public final int identity;
// public Role( int identity )
// {
// this.identity = identity;
// }
//
// }
//
//
