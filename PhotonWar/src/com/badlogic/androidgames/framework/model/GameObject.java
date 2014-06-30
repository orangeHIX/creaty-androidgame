package com.badlogic.androidgames.framework.model;

import com.badlogic.androidgames.framework.math.Circle;
import com.badlogic.androidgames.framework.math.Rectangle;
import com.badlogic.androidgames.framework.math.Shape;
import com.badlogic.androidgames.framework.math.Vector2;

/** ���з��α߽����Ϸʵ�� */
public class GameObject {

	/** ��Ϸʵ�������������е�λ�� */
	public final Vector2 position;
	/** ��Ϸʵ��ı߽� */
	public final Shape bounds;

	/**
	 * �����Ϸʵ��ģ���Ƿ��εģ����������������
	 * 
	 * @param x
	 *            ��Ϸʵ��ģ�͵���������Ϸ�����е�x��λ��
	 * @param y
	 *            ��Ϸʵ��ģ�͵���������Ϸ�����е�y��λ��
	 * @param width
	 *            ��Ϸʵ��ģ�͵Ŀ��
	 * @param height
	 *            ��Ϸʵ��ģ�͵ĸ߶�
	 */
	public GameObject(float x, float y, float width, float height) {
		this.position = new Vector2(x, y);
		this.bounds = new Rectangle(x - width / 2, y - height / 2, width,
				height);
	}

	/**
	 * �����Ϸʵ��ģ����Բ�εģ����������������
	 * 
	 * @param x
	 *            ��Ϸʵ��ģ�͵���������Ϸ�����е�x��λ��
	 * @param y
	 *            ��Ϸʵ��ģ�͵���������Ϸ�����е�y��λ��
	 * @param width
	 *            ��Ϸʵ��ģ�Ͱ뾶
	 */
	public GameObject(float x, float y, float radius) {
		this.position = new Vector2(x, y);
		this.bounds = new Circle(x, y, radius);
	}

	// public void setPosition(Vector2 other) {
	// position.set(other);
	// updateBounds();
	// }
	//
	// public void setPosition(float x, float y) {
	// position.set(x, y);
	// updateBounds();
	// }
	//
	// protected void updateBounds() {
	// if (bounds instanceof Circle) {
	// ((Circle) bounds).center.set(position);
	// } else if (bounds instanceof Rectangle) {
	// Rectangle rec = (Rectangle) bounds;
	// rec.lowerLeft.set(position.x - rec.width / 2, position.y
	// - rec.height / 2);
	// }
	// }
}
