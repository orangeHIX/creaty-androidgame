package com.badlogic.androidgames.framework.gl;

import javax.microedition.khronos.opengles.GL10;

import android.util.FloatMath;

import com.badlogic.androidgames.framework.impl.GLGraphics;
import com.badlogic.androidgames.framework.math.Vector2;

public class SpriteBatcher {
	/**保存描绘形状的点，对应的颜色和材质信息*/
	final float[] verticesBuffer;
	int bufferIndex;
	/**存储引导点*/
	final Vertices vertices;
	int numSprites;
	Texture textrue;

	public SpriteBatcher(GLGraphics glGraphics, int maxSprites) {
		this.verticesBuffer = new float[maxSprites * 4 * 4];
		this.vertices = new Vertices(glGraphics, maxSprites * 4,
				maxSprites * 6, false, true);
		this.bufferIndex = 0;
		this.numSprites = 0;

		short[] indices = new short[maxSprites * 6];
		int len = indices.length;
		short j = 0;
		for (int i = 0; i < len; i += 6, j += 4) {
			indices[i + 0] = (short) (j + 0);
			indices[i + 1] = (short) (j + 1);
			indices[i + 2] = (short) (j + 2);
			indices[i + 3] = (short) (j + 2);
			indices[i + 4] = (short) (j + 3);
			indices[i + 5] = (short) (j + 0);
		}
		vertices.setIndices(indices, 0, indices.length);
	}

	public void beginBatch(Texture texture) {
		this.textrue = texture;
		this.textrue.bind();
		numSprites = 0;
		bufferIndex = 0;
	}

	public void endBatch() {
		vertices.setVertices(verticesBuffer, 0, bufferIndex);
		vertices.bind();
		vertices.draw(GL10.GL_TRIANGLES, 0, numSprites * 6);
		vertices.unbind();
		textrue.unbind();
	}
	public void drawSprite(float x, float y, float width, float height,
			TextureRegion region) {
		float halfWidth = width / 2;
		float halfHeight = height / 2;
		float l_x = x - halfWidth;
		float b_y = y - halfHeight;
		float r_x = x + halfWidth;
		float t_y = y + halfHeight;
		// 左下
		verticesBuffer[bufferIndex++] = l_x;
		verticesBuffer[bufferIndex++] = b_y;
		verticesBuffer[bufferIndex++] = region.l_x;
		verticesBuffer[bufferIndex++] = region.b_y;
		// 右下
		verticesBuffer[bufferIndex++] = r_x;
		verticesBuffer[bufferIndex++] = b_y;
		verticesBuffer[bufferIndex++] = region.r_x;
		verticesBuffer[bufferIndex++] = region.b_y;
		// 右上
		verticesBuffer[bufferIndex++] = r_x;
		verticesBuffer[bufferIndex++] = t_y;
		verticesBuffer[bufferIndex++] = region.r_x;
		verticesBuffer[bufferIndex++] = region.t_y;
		// 左上
		verticesBuffer[bufferIndex++] = l_x;
		verticesBuffer[bufferIndex++] = t_y;
		verticesBuffer[bufferIndex++] = region.l_x;
		verticesBuffer[bufferIndex++] = region.t_y;

		numSprites++;
	}

	public void drawSprite(float x, float y, float width, float height,
			Vector2 direction, TextureRegion region) {
		float halfWidth = width / 2;
		float halfHeight = height / 2;

		float dist = direction.dist(0, 0);
		float cos;
		float sin;
		if (dist < 1e-10f) {
			cos = 1.0f;
			sin = 0.f;
		} else {
			cos = direction.x / dist;
			sin = direction.y / dist;
		}
		// 索引1,2,3,4分别对应直角坐标系3,4,1,2象限的坐标索引
		float x1 = -halfWidth * cos - (-halfHeight) * sin;
		float y1 = -halfWidth * sin + (-halfHeight) * cos;
		float x2 = halfWidth * cos - (-halfHeight) * sin;
		float y2 = halfWidth * sin + (-halfHeight) * cos;
		float x3 = halfWidth * cos - halfHeight * sin;
		float y3 = halfWidth * sin + halfHeight * cos;
		float x4 = -halfWidth * cos - halfHeight * sin;
		float y4 = -halfWidth * sin + halfHeight * cos;

		x1 += x;
		y1 += y;
		x2 += x;
		y2 += y;
		x3 += x;
		y3 += y;
		x4 += x;
		y4 += y;

		verticesBuffer[bufferIndex++] = x1;
		verticesBuffer[bufferIndex++] = y1;
		verticesBuffer[bufferIndex++] = region.l_x;
		verticesBuffer[bufferIndex++] = region.b_y;

		verticesBuffer[bufferIndex++] = x2;
		verticesBuffer[bufferIndex++] = y2;
		verticesBuffer[bufferIndex++] = region.r_x;
		verticesBuffer[bufferIndex++] = region.b_y;

		verticesBuffer[bufferIndex++] = x3;
		verticesBuffer[bufferIndex++] = y3;
		verticesBuffer[bufferIndex++] = region.r_x;
		verticesBuffer[bufferIndex++] = region.t_y;

		verticesBuffer[bufferIndex++] = x4;
		verticesBuffer[bufferIndex++] = y4;
		verticesBuffer[bufferIndex++] = region.l_x;
		verticesBuffer[bufferIndex++] = region.t_y;

		numSprites++;
	}

	public void drawSprite(float x, float y, float width, float height,
			float angle, TextureRegion region) {
		float halfWidth = width / 2;
		float halfHeight = height / 2;

		float rad = angle * Vector2.TO_RADIANS;
		float cos = FloatMath.cos(rad);
		float sin = FloatMath.sin(rad);

		// 索引1,2,3,4分别对应直角坐标系3,4,1,2象限的坐标索引
		float x1 = -halfWidth * cos - (-halfHeight) * sin;
		float y1 = -halfWidth * sin + (-halfHeight) * cos;
		float x2 = halfWidth * cos - (-halfHeight) * sin;
		float y2 = halfWidth * sin + (-halfHeight) * cos;
		float x3 = halfWidth * cos - halfHeight * sin;
		float y3 = halfWidth * sin + halfHeight * cos;
		float x4 = -halfWidth * cos - halfHeight * sin;
		float y4 = -halfWidth * sin + halfHeight * cos;

		x1 += x;
		y1 += y;
		x2 += x;
		y2 += y;
		x3 += x;
		y3 += y;
		x4 += x;
		y4 += y;

		verticesBuffer[bufferIndex++] = x1;
		verticesBuffer[bufferIndex++] = y1;
		verticesBuffer[bufferIndex++] = region.l_x;
		verticesBuffer[bufferIndex++] = region.b_y;

		verticesBuffer[bufferIndex++] = x2;
		verticesBuffer[bufferIndex++] = y2;
		verticesBuffer[bufferIndex++] = region.r_x;
		verticesBuffer[bufferIndex++] = region.b_y;

		verticesBuffer[bufferIndex++] = x3;
		verticesBuffer[bufferIndex++] = y3;
		verticesBuffer[bufferIndex++] = region.r_x;
		verticesBuffer[bufferIndex++] = region.t_y;

		verticesBuffer[bufferIndex++] = x4;
		verticesBuffer[bufferIndex++] = y4;
		verticesBuffer[bufferIndex++] = region.l_x;
		verticesBuffer[bufferIndex++] = region.t_y;

		numSprites++;
	}
}