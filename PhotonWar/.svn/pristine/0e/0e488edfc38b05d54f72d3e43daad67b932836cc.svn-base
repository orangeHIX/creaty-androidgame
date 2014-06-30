package com.creaty.photonwar;

import java.util.ArrayList;

import android.util.Log;

import com.badlogic.androidgames.framework.Input.TouchEvent;
import com.badlogic.androidgames.framework.math.Circle;
import com.badlogic.androidgames.framework.math.OverlapTester;
import com.badlogic.androidgames.framework.math.Vector2;
import com.creaty.game.framework.State;
import com.creaty.game.framework.State.StateUpdater;
import com.creaty.game.framework.StateMachine;
import com.creaty.game.framework.StateMachine2;
import com.creaty.game.framework.StateMachine2.StateTransitionTester;
import com.creaty.photonwar.entity.Race;
import com.creaty.photonwar.entity.StrongHold;
import com.creaty.photonwar.entity.World;

public class CommandHandler {
	public static final String tag = "CommandHandler";

	public static final State STATE_IDLE = new State(0, "Idle",
			new StateUpdater() {
				@Override
				public void enter(Object obj) {
					// TODO Auto-generated method stub
					((CommandHandler) obj).selectedHolds.clear();
					((CommandHandler) obj).target = null;
				}
			});
	public static final State STATE_SELECT = new State(1, "Select",
			new StateUpdater() {
				@Override
				public void enter(Object obj) {
					// TODO Auto-generated method stub
					CommandHandler h = (CommandHandler) obj;
					h.selectedHolds.add(h.location);
					h.target = null;
				}

				@Override
				public void hold(Object obj) {
					// TODO Auto-generated method stub
					CommandHandler h = (CommandHandler) obj;
					h.selectedHolds.add(h.location);
				}

			});
	public static final State STATE_LOCATE = new State(2, "Locate",
			new StateUpdater() {
				@Override
				public void enter(Object obj) {
					// TODO Auto-generated method stub
					((CommandHandler) obj).target = ((CommandHandler) obj).location;
				}

				@Override
				public void hold(Object obj) {
					// TODO Auto-generated method stub
					((CommandHandler) obj).target = ((CommandHandler) obj).location;
				}

			});
	public static final State STATE_EXECUTE = new State(3, "Execute",
			new StateUpdater() {
				@Override
				public void enter(Object obj) {
					// TODO Auto-generated method stub
					CommandHandler h = (CommandHandler) obj;
					int size = h.selectedHolds.size();
					StrongHold hold = null;
					if (h.target == null)
						h.target = h.location;
					for (int i = 0; i < size; i++) {
						hold = h.selectedHolds.get(i);
						if (hold != h.target)
							hold.sendShipsTo(h.target);
					}
					// 使得EXECUTE状态能够立即转换到idle状态
					h.clearCommand();
				}
			});

	public static class CommandStateTransitionTester implements
			StateTransitionTester {
		@Override
		public boolean tryChange(Object objectWithState, State from, State to) {
			// TODO Auto-generated method stub
			CommandHandler handler = (CommandHandler) objectWithState;
			if (from.code == STATE_IDLE.code) {
				if (to.code == STATE_SELECT.code) {
					if (handler.location != null
							&& handler.location.owner != null
							&& handler.location.owner.master == Race.HU_PLAYER
							&& handler.action == TouchEvent.TOUCH_DOWN)
						return true;
				}
			} else if (from.code == STATE_SELECT.code) {
				if (to.code == STATE_IDLE.code) {
					if (handler.location == null
							&& handler.action == TouchEvent.TOUCH_UP)
						return true;
				} else if (to.code == STATE_SELECT.code) {
					if (handler.location != null
							&& handler.location.owner != null
							&& handler.location.owner.master == Race.HU_PLAYER
							&& (!handler.selectedHolds
									.contains(handler.location))
							&& handler.action == TouchEvent.TOUCH_DRAGGED)
						return true;
				} else if (to.code == STATE_LOCATE.code) {
					if (handler.location != null
							&& (handler.location.owner == null || handler.location.owner.master != Race.HU_PLAYER)
							&& handler.action == TouchEvent.TOUCH_DRAGGED)
						return true;
				} else if (to.code == STATE_EXECUTE.code) {
					if (handler.location != null
							&& handler.location.owner != null
							&& handler.location.owner.master == Race.HU_PLAYER
							&& handler.action == TouchEvent.TOUCH_UP) {
						return true;
					}
				}
			} else if (from.code == STATE_LOCATE.code) {
				if (to.code == STATE_IDLE.code) {
					if (handler.location == null
							&& handler.action == TouchEvent.TOUCH_UP)
						return true;
				} else if (to.code == STATE_SELECT.code) {
					if (handler.location != null
							&& handler.location.owner != null
							&& handler.location.owner.master == Race.HU_PLAYER
							&& handler.action == TouchEvent.TOUCH_DRAGGED)
						return true;
				} else if (to.code == STATE_LOCATE.code) {
					if (handler.location != null
							&& (handler.location.owner == null || handler.location.owner.master != Race.HU_PLAYER)
							&& handler.action == TouchEvent.TOUCH_DRAGGED)
						return true;
				} else if (to.code == STATE_EXECUTE.code) {
					if (handler.location != null
							&& (handler.location.owner == null || handler.location.owner.master != Race.HU_PLAYER)
							&& handler.action == TouchEvent.TOUCH_UP)
						return true;
				}
			} else if (from.code == STATE_EXECUTE.code) {
				if (to.code == STATE_IDLE.code)
					return true;
			}
			return false;
		}
	}

	static StateMachine2 STATE_MACHINE;
	static {
		STATE_MACHINE = new StateMachine2(new CommandStateTransitionTester());
		STATE_MACHINE.registerState(STATE_IDLE);
		STATE_MACHINE.registerState(STATE_SELECT);
		STATE_MACHINE.registerState(STATE_LOCATE);
		STATE_MACHINE.registerState(STATE_EXECUTE);
	}

	State state;

	World world;
	/** 被选中的己方据点 */
	ArrayList<StrongHold> selectedHolds;
	/** 目标据点 */
	StrongHold target;
	/** 当前触摸到的据点，没有触摸到设置为null */
	StrongHold location;
	/**
	 * 当前触摸事件的类型，可能为TouchEvent.TOUCH_UP，
	 * TouchEvent.TOUCH_DOWN，TouchEvent.TOUCH_DRAGGED
	 */
	int action;

	public CommandHandler(World world) {

		this.world = world;
		selectedHolds = new ArrayList<StrongHold>();
		STATE_MACHINE.start(this, STATE_IDLE);
		state = STATE_IDLE;
	}

	public void update(int action, Vector2 touchPoint) {
		this.location = checkLoaction(touchPoint.x, touchPoint.y);
		this.action = action;
		state = STATE_MACHINE.updateState(this, state);
	}

	/** 清除命令，状态机将还原到原始状态 */
	protected void clearCommand() {
		this.location = null;
		this.action = TouchEvent.TOUCH_UP;
		// target = null; 没必要写这两句
		// selectedHolds.clear();
		state = STATE_MACHINE.updateState(this, state);
	}

	protected StrongHold checkLoaction(float x, float y) {
		ArrayList<StrongHold> strongholds = world.strongHolds;
		StrongHold location = null;
		int holdsize = strongholds.size();
		for (int i = 0; i < holdsize; i++) {
			StrongHold tmp = strongholds.get(i);
			if (OverlapTester.pointInCircle((Circle) tmp.bounds, x, y)) {
				location = tmp;
				break;
			}
		}
		return location;
	}

	/*
	 * protected void updateIdle(int action, StrongHold location) { if (location
	 * != null && location.owner.identity == Role.ROLE_PLAYER && action ==
	 * TouchEvent.TOUCH_DOWN) { state = STATE_SELECT;
	 * selectedHolds.add(location); } }
	 * 
	 * 
	 * 
	 * protected void updateLocate(int action, StrongHold location) {
	 * 
	 * }
	 * 
	 * protected void updateExecute(int action, StrongHold location) {
	 * 
	 * state = STATE_IDLE; selectedHolds.clear(); target = null; }
	 */
}
