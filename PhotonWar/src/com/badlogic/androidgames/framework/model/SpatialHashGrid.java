package com.badlogic.androidgames.framework.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.androidgames.framework.math.Circle;
import com.badlogic.androidgames.framework.math.Rectangle;
import com.badlogic.androidgames.framework.math.Shape;
import android.util.FloatMath;
import android.util.Log;

public class SpatialHashGrid {
	public static final String tag = "SpatialHashGrid";
	/** 用于记录动态游戏实体的网格 */
	protected List<GameObject>[] dynamicCells;
	/** 用于记录静态游戏实体的网格 */
	protected List<GameObject>[] staticCells;
	int cellsPerRow;
	int cellsPerCol;
	float cellSize;
	int[] cellIds = new int[4];
	/** 用于记录某个游戏实体可能碰撞到的实体 */
	protected List<GameObject> foundObjects;

	@Override
	public String toString() {
		return "SpatialHashGrid [cellsPerRow=" + cellsPerRow + ", cellsPerCol="
				+ cellsPerCol + ", cellSize=" + cellSize + "]";
	}

	@SuppressWarnings("unchecked")
	public SpatialHashGrid(float worldWidth, float worldHeight, float cellSize) {
		this.cellSize = cellSize;
		this.cellsPerRow = (int) FloatMath.ceil(worldWidth / cellSize);
		this.cellsPerCol = (int) FloatMath.ceil(worldHeight / cellSize);
		int numCells = cellsPerRow * cellsPerCol;
		dynamicCells = new List[numCells];
		staticCells = new List[numCells];
		for (int i = 0; i < numCells; i++) {
			dynamicCells[i] = new ArrayList<GameObject>(10);
			staticCells[i] = new ArrayList<GameObject>(10);
		}
		foundObjects = new ArrayList<GameObject>(10);
	}

	public void insertStaticObject(GameObject obj) {
		int[] cellIds = getCellIds(obj);
		int i = 0;
		int cellId = -1;
		// Log.d("insertStaticObject", cellIds[0] + "," + cellIds[1] + ","
		// + cellIds[2] + "," + cellIds[3]);
		while (i <= 3 && (cellId = cellIds[i++]) != -1) {
			staticCells[cellId].add(obj);
		}
	}

	public void insertDynamicObject(GameObject obj) {
		int[] cellIds = getCellIds(obj);
		int i = 0;
		int cellId = -1;
		while (i <= 3 && (cellId = cellIds[i++]) != -1) {
			dynamicCells[cellId].add(obj);
		}
	}

	public void removeObject(GameObject obj) {
		int[] cellIds = getCellIds(obj);
		int i = 0;
		int cellId = -1;
		while (i <= 3 && (cellId = cellIds[i++]) != -1) {
			dynamicCells[cellId].remove(obj);
			staticCells[cellId].remove(obj);
		}
	}

	public void clearDynamicCells() {
		int len = dynamicCells.length;
		for (int i = 0; i < len; i++) {
			dynamicCells[i].clear();
		}
	}

	/**
	 * 返回可能与输入实体碰撞的实体列表 输入实体所处的网格会重新观测，但其他的游戏实体对象不会， 即便是动态游戏实体也会看作是处于最初插入时的网格
	 */
	public List<GameObject> getPotentialColliders(GameObject obj) {
		foundObjects.clear();
		int[] cellIds = getCellIds(obj);
		int i = 0;
		int cellId = -1;
		while (i <= 3 && (cellId = cellIds[i++]) != -1) {
			int len = dynamicCells[cellId].size();
			for (int j = 0; j < len; j++) {
				GameObject collider = dynamicCells[cellId].get(j);
				if (!foundObjects.contains(collider))
					foundObjects.add(collider);
			}

			len = staticCells[cellId].size();
			for (int j = 0; j < len; j++) {
				GameObject collider = staticCells[cellId].get(j);
				if (!foundObjects.contains(collider))
					foundObjects.add(collider);
			}
		}
		return foundObjects;
	}

	/**
	 * 返回一个游戏实体占据了几个格子（cell），最多是四个
	 * 
	 * @param obj需要检测的游戏实体
	 */
	public int[] getCellIds(GameObject obj) {

		cellIds[0] = -1;
		cellIds[1] = -1;
		cellIds[2] = -1;
		cellIds[3] = -1;

		// 赋予不可能出现的初值
		/** 游戏实体的左边界 */
		int x1 = 1;
		/** 游戏实体的右边界 */
		int x2 = -1;
		/** 游戏实体的下边界 */
		int y1 = 1;
		/** 游戏实体的上边界 */
		int y2 = -1;

		if (obj.bounds instanceof Rectangle)// 当游戏实体边界使用矩形表示
		{
			Rectangle rec = (Rectangle) obj.bounds;
			x1 = (int) FloatMath.floor((obj.position.x - rec.width / 2)
					/ cellSize);
			y1 = (int) FloatMath.floor((obj.position.y - rec.height / 2)
					/ cellSize);
			x2 = (int) FloatMath.floor((obj.position.x + rec.width / 2)
					/ cellSize);
			y2 = (int) FloatMath.floor((obj.position.y + rec.height / 2)
					/ cellSize);
		} else if (obj.bounds instanceof Circle) {
			Circle cir = (Circle) obj.bounds;
			x1 = (int) FloatMath
					.floor((obj.position.x - cir.radius) / cellSize);
			y1 = (int) FloatMath
					.floor((obj.position.y - cir.radius) / cellSize);
			x2 = (int) FloatMath
					.floor((obj.position.x + cir.radius) / cellSize);
			y2 = (int) FloatMath
					.floor((obj.position.y + cir.radius) / cellSize);
		} else {
			Log.d(tag, "shouldn't be here");
		}

		if (x1 == x2 && y1 == y2) { // 游戏实体处于一个格子内部
			if (x1 >= 0 && x1 < cellsPerRow && y1 >= 0 && y1 < cellsPerCol)
				cellIds[0] = x1 + y1 * cellsPerRow;
			else
				cellIds[0] = -1;
			cellIds[1] = -1;
			cellIds[2] = -1;
			cellIds[3] = -1;
			// Log.d(tag, x1 + "," + y1 + ": " + cellIds[0] + "   1");
		} else if (x1 == x2) { // 游戏实体占据了上下相邻的两个格子
			int i = 0;
			if (x1 >= 0 && x1 < cellsPerRow) {
				if (y1 >= 0 && y1 < cellsPerCol)
					cellIds[i++] = x1 + y1 * cellsPerRow;
				if (y2 >= 0 && y2 < cellsPerCol)
					cellIds[i++] = x1 + y2 * cellsPerRow;
			}
			while (i <= 3)
				cellIds[i++] = -1;
			// Log.d(tag, "2");
		} else if (y1 == y2) { // 游戏实体占据了左右相邻的两个格子
			int i = 0;
			if (y1 >= 0 && y1 < cellsPerCol) {
				if (x1 >= 0 && x1 < cellsPerRow)
					cellIds[i++] = x1 + y1 * cellsPerRow;
				if (x2 >= 0 && x2 < cellsPerRow)
					cellIds[i++] = x2 + y1 * cellsPerRow;
			}
			while (i <= 3)
				cellIds[i++] = -1;
			// Log.d(tag, "3");
		} else { // 游戏实体占据了四个格子，且处于这四个格子组成的矩形内部
			int i = 0;
			int y1CellsPerRow = y1 * cellsPerRow;
			int y2CellsPerRow = y2 * cellsPerRow;
			if (x1 >= 0 && x1 < cellsPerRow && y1 >= 0 && y1 < cellsPerCol)
				cellIds[i++] = x1 + y1CellsPerRow;
			if (x2 >= 0 && x2 < cellsPerRow && y1 >= 0 && y1 < cellsPerCol)
				cellIds[i++] = x2 + y1CellsPerRow;
			if (x2 >= 0 && x2 < cellsPerRow && y2 >= 0 && y2 < cellsPerCol)
				cellIds[i++] = x2 + y2CellsPerRow;
			if (x1 >= 0 && x1 < cellsPerRow && y2 >= 0 && y2 < cellsPerCol)
				cellIds[i++] = x1 + y2CellsPerRow;
			while (i <= 3)
				cellIds[i++] = -1;
			// Log.d(tag, "4");
		}
		return cellIds;
	}
}