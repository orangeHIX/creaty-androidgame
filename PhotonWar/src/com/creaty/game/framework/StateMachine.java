package com.creaty.game.framework;

import java.util.ArrayList;

import android.util.Log;

import com.creaty.game.framework.State.StateUpdater;

/** �洢�ſ��õ�״̬�б�͵�ǰ��״̬�Լ�״̬ת���Ľӿ� */
public class StateMachine {

	public static final String tag = "StateMachine";

	/** �ж�״̬ת�������Ƿ�����Ľӿ� */
	public interface StateTransitionTester {
		public boolean tryChange(State from, State to);
	}

	/** ���и�״̬���Ķ��� */
	Object objectWithState;
	ArrayList<State> stateList;
	State currentState;
	StateTransitionTester tester;

	public StateMachine(Object obj, StateTransitionTester tester) {
		this.objectWithState = obj;
		stateList = new ArrayList<State>();
		this.tester = tester;
	}

	/**
	 * �Ǽ�״̬����Ӧ�ð�����״̬������Ǽǵ�״̬�ǳ�ʼ״̬�����Զ������״̬
	 * 
	 * @param state
	 *            ״̬
	 * @param isInitial
	 *            ֪���ǳ�ʼ״̬
	 * @return �Ƿ�ɹ��ǼǴ�״̬�����֮ǰ��״̬��Ҫ�Ǽǵ�״̬��ͬ�᷵��false
	 */
	public boolean registerState(State state, boolean isInitial) {
		if (!stateList.contains(state)) {
			if (isInitial) {
				currentState = state;
				currentState.stateUpdater.enter(objectWithState);
			}
			stateList.add(state);
			return true;
		} else {
			return false;
		}
	}

	public boolean registerState(int code, String name,
			StateUpdater stateUpdater, boolean isInitial) {
		return registerState(new State(code, name, stateUpdater), isInitial);
	}

	/** ���Ա任״̬�������е�����״̬�뵱ǰ״̬����ת����һ���ɹ����˳�ѭ�����أ� */
	public void updateState() {
		int size = stateList.size();
		State state;
		for (int i = 0; i < size; i++) {
			state = stateList.get(i);
			if (tester.tryChange(currentState, state)) {
				Log.d(tag, "update state from " + currentState.name + " to "
						+ state.name);
				if (currentState.equals(state)) {
					currentState.stateUpdater.hold(objectWithState);
				} else {
					currentState.stateUpdater.exit(objectWithState);
					currentState = state;
					currentState.stateUpdater.enter(objectWithState);
				}
				break;
			}
		}
	}

	public int getCurrentStateCode() {
		return currentState.code;
	}

	public String getCurrentStateName() {
		return currentState.name;
	}

}
