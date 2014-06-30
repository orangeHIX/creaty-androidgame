package com.creaty.game.framework;

import android.util.Log;

/** ״̬�Ķ��� */
public class State {
	/** �������룬�뿪������״̬���ַ����ľ�̬�� */
	public static class StateUpdater {
		public static final String tag = "StateUpdater";

		/**
		 * �����״̬���õķ���
		 * 
		 * @param obj
		 *            ���д�״̬��ʵ�����
		 */
		public void enter(Object obj) {
			// Log.d(tag, "enter");
		};

		/**
		 * �뿪��״̬���õķ���
		 * 
		 * @param obj
		 *            ���д�״̬��ʵ�����
		 */
		public void hold(Object obj) {
			// Log.d(tag, "hold");
		};

		/**
		 * ���ִ�״̬���õķ���
		 * 
		 * @param obj
		 *            ���д�״̬��ʵ�����
		 */
		public void exit(Object obj) {
			// Log.d(tag, "exit");
		};
	}

	/** ״̬���� */
	public final int code;
	/** ״̬�� */
	public final String name;
	/** ״̬������������ʹ��ʱ��Ҫ�̳�������ಢ��д��Ҫ���õķ��� */
	public StateUpdater stateUpdater;

	/**
	 * @param code
	 *            ״̬����
	 * @param name
	 *            ״̬��
	 * @param stateUpdater
	 *            ״̬������������ʹ��ʱ��Ҫ�̳�������ಢ��д��Ҫ���õķ���
	 */
	public State(int code, String name, StateUpdater stateUpdater) {
		this.code = code;
		this.name = name;
		this.stateUpdater = stateUpdater;
	}

	/** ��ʱû���� */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + code;
		return result;
	}

	@Override
	/**�Ƚ�2��״̬�Ƿ���һ��״̬*/
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		if (code != other.code)
			return false;
		return true;
	}

}