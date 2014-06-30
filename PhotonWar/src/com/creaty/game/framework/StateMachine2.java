package com.creaty.game.framework;

import java.util.ArrayList;

import android.util.Log;

public class StateMachine2 {

	public static final String tag = "StateMachine";

	/** �ж�״̬ת�������Ƿ�����Ľӿ� */
	public interface StateTransitionTester {
		/**�ж��Ƿ��ܴ�һ��״̬ת����һ��״̬<br>
		 * ����״̬��ӵ��state_1, state_2, ..., state_n��n��״̬���ж���Ӧ�������µĽṹ��<br>
		 * if(from.state == state_1){<br>
		 * &emsp;//�����state_1����ת����state_2,state_5������state_1��<br>
		 * &emsp;if(to.state == state_2){<br>
		 * &emsp;&emsp;if(��state_1ת����state_2������)<br>
		 * &emsp;&emsp;&emsp;return true;<br>
		 * &emsp;}else if(to.state == state_5){<br>
		 * &emsp;&emsp;if(��state_1ת����state_5������)<br>
		 * &emsp;&emsp;&emsp;return true;<br>
		 * &emsp;}else if(to.state == state_1){<br>
		 * &emsp;&emsp;if(��state_1ת����state_1������)<br>
		 * &emsp;&emsp;&emsp;return true;<br>
		 * &emsp;}<br>
		 * }<br>
		 * &emsp;...<br>
		 * }else if(from.state == state_2){<br>
		 * &emsp;...<br>
		 * }<br>
		 * ...<br>
		 * else if(from.state == state_n){<br>
		 * &emsp;...<br>
		 * }<br>
		 * return false;<br>
		 * */
		public boolean tryChange(Object objectWithState, State from, State to);
	}

	ArrayList<State> stateList;
	StateTransitionTester tester;
	/** ״̬���±��濪�� */
	public boolean logSwitch;

	public StateMachine2(StateTransitionTester tester) {
		stateList = new ArrayList<State>();
		this.tester = tester;
		logSwitch = false;
	}

	/**
	 * �Ǽ�״̬����Ӧ�ð�����״̬������Ǽǵ�״̬�ǳ�ʼ״̬�����Զ������״̬
	 * 
	 * @param state
	 *            ״̬
	 * @return �Ƿ�ɹ��ǼǴ�״̬�����֮ǰ��״̬��Ҫ�Ǽǵ�״̬��ͬ�᷵��false
	 */
	public boolean registerState(State state) {
		if (!stateList.contains(state)) {
			stateList.add(state);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ����״̬�������״̬�����иó�ʼ״̬������״̬�Ķ�������״̬��������״̬��ʧ�ܡ�
	 * 
	 * @param ����״̬�Ķ���
	 */
	public boolean start(Object objectWithState, State initialState) {
		if (stateList.contains(initialState)) {
			initialState.stateUpdater.enter(objectWithState);
			return true;
		} else {
			return false;
		}
	}

	public boolean Stop(Object objectWithState) {
		return false;
	}

	/**
	 * ����״̬�����״̬
	 * 
	 * @param ״̬����
	 * @param ״̬����ǰ��״̬
	 * @return ״̬�����µ�״̬
	 */
	public State updateState(Object objectWithState, State currState) {
		int size = stateList.size();
		State state = null;
		State currentState = currState;
		if (currentState == null)
			return currentState;
		for (int i = 0; i < size; i++) {
			state = stateList.get(i);
			if (tester.tryChange(objectWithState, currentState, state)) {
				if (logSwitch)
					Log.d(tag, "state change from " + currentState.name
							+ " to " + state.name);
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
		return currentState;
	}

	/** ���ݴ��뷵�ض�Ӧ��״̬�����û���ҵ���Ӧ��״̬������null */
	public State getState(int code) {
		State state = null;
		int size = stateList.size();

		for (int i = 0; i < size; i++) {
			state = stateList.get(i);
			if (state.code == code) {
				return state;
			}
		}
		return null;
	}

}
