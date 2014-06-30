package com.creaty.photonwar.view;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import com.badlogic.androidgames.framework.gl.Camera2D;
import com.badlogic.androidgames.framework.gl.FanShapeDrawer;
import com.badlogic.androidgames.framework.gl.NoTextureBatcher;
import com.badlogic.androidgames.framework.gl.SpriteBatcher;
import com.badlogic.androidgames.framework.impl.GLGraphics;
import com.badlogic.androidgames.framework.math.FanShape;
import com.badlogic.androidgames.framework.math.Vector2;
import com.creaty.photonwar.Assets;
import com.creaty.photonwar.entity.Ship;
import com.creaty.photonwar.entity.ShipGroup;
import com.creaty.photonwar.entity.StrongHold;
import com.creaty.photonwar.entity.World;

public class WorldRenderer {

	static final float FRUSTUM_WIDTH = 20;
	static final float FRUSTUM_HEIGHT = 12;
	GLGraphics glGraphics;
	World world;
	Camera2D cam;
	SpriteBatcher batcher;
	NoTextureBatcher noBatcher;
	FanShapeDrawer fanShapeDrawer;
	/** 绘制图形用的扇形 */
	FanShape fan;

	public WorldRenderer(GLGraphics glGraphics, SpriteBatcher batcher,
			NoTextureBatcher noBatcher,World world) {
			FanShapeDrawer fanShapeDrawer, World world) {
		this.glGraphics = glGraphics;
		this.world = world;
		this.cam = new Camera2D(glGraphics, FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
		this.batcher = batcher;
		this.noBatcher = noBatcher;
		this.fanShapeDrawer = fanShapeDrawer;
		tmp = new Vector2(); // 帮助计算的临时向量
		fan = new FanShape(5, 5, 5.0f, 0, 359);
	}

	public void render() {
		cam.setViewportAndMatrices();
		renderObjects();
	}

	public void renderObjects() {
		GL10 gl = glGraphics.getGL();
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		fanShapeDrawer.beginBatch();
		//fanShapeDrawer.drawFanShape(fan, 1,0,0,1);
		renderStrongHoldDevelop();
		fanShapeDrawer.endBatch();

		batcher.beginBatch(Assets.texture);
		renderStrongHolds();
		renderWorldShips();
		batcher.endBatch();
		
		// 下面这样做纯属权宜之计，因为材质位置不同
		batcher.beginBatch(Assets.items);
		renderStrongHoldsShipsNum();
		batcher.endBatch();
		noBatcher.beginBatch();
		noBatcher.endBatch();
		gl.glDisable(GL10.GL_BLEND);
	}

	private void renderStrongHolds() {
		int len = world.strongHolds.size();
		for (int i = 0; i < len; i++) {
			StrongHold strongHold = world.strongHolds.get(i);
			batcher.drawSprite(strongHold.position.x, strongHold.position.y,
					2.0f, 2.0f, Assets.bobRegion);
		}
	}

	private void renderStrongHoldDevelop() {
		int len = world.strongHolds.size();
		for (int i = 0; i < len; i++) {
			StrongHold strongHold = world.strongHolds.get(i);
			fan.c.center.set(strongHold.position);
			fan.c.radius = strongHold.orbitInsideBound;
			fan.setStart(0);
			fan.setEnd(strongHold.development/100*360);
			fanShapeDrawer.drawFanShapeRing(fan,
					strongHold.orbitInsideBound - StrongHold.DEVELOP_RING_WIDTH,
					0, 0, 1, 1, 0, 0, 0);
		}
	}

	private void renderStrongHoldsShipsNum() {
		int len = world.strongHolds.size();
		for (int i = 0; i < len; i++) {
			StrongHold strongHold = world.strongHolds.get(i);
			Assets.font.drawText(batcher,
					Integer.toString(strongHold.governShips.size()),
					strongHold.position.x, strongHold.position.y, 0.03f);
		}
	}

	/** 临时的帮助计算的向量 */
	private Vector2 tmp;

	/** 单独绘制一个战舰 */
	private void renderSingleShip(Ship ship) {
		tmp.set(ship.position.x - ship.rotationCenter.x, ship.position.y
				- ship.rotationCenter.y);
		if (ship.getState() == Ship.STATE_CIRCLE) {
			if (ship.isClockwise == true) {
				batcher.drawSprite(ship.position.x, ship.position.y, 0.4f,
						0.2f, tmp.angle() + 270.0f, Assets.cannonRegion);
			} else {
				batcher.drawSprite(ship.position.x, ship.position.y, 0.4f,
						0.2f, tmp.angle() + 90.0f, Assets.cannonRegion);
			}
		} else {
				batcher.drawSprite(ship.position.x, ship.position.y, 0.4f, 0.2f,
					ship.velocity, Assets.cannonRegion);
		}
	}

	public void renderSingleAmmnuition(){
		
	}
	private void renderWorldAmmunition() {
		
	}
	/** 绘制世界中所有的战舰 */
	private void renderWorldShips() {
		int holdSize = world.strongHolds.size();
		for (int i = 0; i < holdSize; i++) {
			StrongHold tempHold = world.strongHolds.get(i);
			int shipSize = tempHold.governShips.size();
			for (int i2 = 0; i2 < shipSize; i2++) {
				renderSingleShip(tempHold.governShips.get(i2));
			}
			// Log.d("renderShips", ship.position.x+","+ship.position.y);
		}
		int groupSize = world.shipGroups.size();
		for (int i = 0; i < groupSize; i++) {
			ShipGroup tempGroup = world.shipGroups.get(i);
			int shipSize = tempGroup.shipList.size();
			for (int i2 = 0; i2 < shipSize; i2++) {
				renderSingleShip(tempGroup.shipList.get(i2));
			}
		}
	}

	public final Camera2D getCamera2D() {
		return cam;
	}
}
