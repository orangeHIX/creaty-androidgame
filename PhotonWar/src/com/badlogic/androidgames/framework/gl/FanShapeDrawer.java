package com.badlogic.androidgames.framework.gl;



import javax.microedition.khronos.opengles.GL10;


import com.badlogic.androidgames.framework.impl.GLGraphics;
import com.badlogic.androidgames.framework.math.Circle;
import com.badlogic.androidgames.framework.math.FanShape;
import com.badlogic.androidgames.framework.math.Vector2;

public class FanShapeDrawer {
	/**精细度，数字越小，精细度越高*/
	public static enum LEVEL {
		LEVEL_1, LEVEL_2, LEVEL_3, LEVEL_4, LEVEL_5, LEVEL_6
	}

	final float[] verticesBuffer;
	int bufferIndex;
	/**精细度，数字越小，精细度越高*/
	final LEVEL level;
	final Vertices vertices;
	/**扇形数目*/
	int numFanShapes;
	/**绘制每个圆需要的顶点数*/
	int numVerticePerCircle;
	/**绘制每个扇形实际需要的顶点数*/
	int[] numVerticePerFanShape;
	/**快速绘制开关
	 * @deprecated 优化效果不明显*/
	boolean fastDraw;
	/**存储三角函数的单位向量
	 * * @deprecated 优化效果不明显*/
	Vector2[] vectors;
	/**
	 * @param level 精细度 数字越小，精细度越高
	 * @param maxFanShape 一次绘制的扇形（包括圆形在内）的最大数目*/
	public FanShapeDrawer(GLGraphics glGraphics, LEVEL level, int maxFanShape)
	{
		this(glGraphics, level, maxFanShape, false);
	}
	public FanShapeDrawer(GLGraphics glGraphics, LEVEL level, int maxFanShape,
			boolean fastDraw) {

		// this.hasColor = hasColor;
		// this.hasTexCoords = hasTexCoords;
		this.level = level;
		numVerticePerCircle = getMaxVerticesPerCircle(level);
		this.vertices = new Vertices(glGraphics, maxFanShape
				* (numVerticePerCircle + 2), 0, true, false);
		this.verticesBuffer = new float[maxFanShape * (numVerticePerCircle + 2)
				* vertices.vertexSize / 4];
		numVerticePerFanShape = new int[maxFanShape];
		this.fastDraw = fastDraw;
		if (this.fastDraw) {
			vectors = new Vector2[720];
			float step = 360f / 720;
			float angle = 90;
			for (int i = 0; i <= 90; i++, angle -= step) {
				vectors[i] = new Vector2((float)Math.cos(angle *  Math.PI
						/ 180), (float)Math.sin(angle * Math.PI / 180));
			}
			for (int i = 0; i < 90; i++) {
				vectors[180 - i] = new Vector2(vectors[i].y, vectors[i].x);
			}
			for (int i = 0; i < 180; i++) {
				vectors[360 - i] = new Vector2(vectors[i].x, -vectors[i].y);
			}
			for (int i = 0; i < 360; i++) {
				vectors[719 - i] = new Vector2(-vectors[i].x, vectors[i].y);
			}
		}
	}
	/**返回对应精细度所需的顶点数目*/
	protected static int getMaxVerticesPerCircle(LEVEL level) {
		switch (level) {
		case LEVEL_1:
			return 720;
		case LEVEL_2:
			return 360;
		case LEVEL_3:
			return 180;
		case LEVEL_4:
			return 90;
		case LEVEL_5:
			return 45;
		case LEVEL_6:
			return 6;
		default:
			return 3;
		}
	}

	public void beginBatch() {

		numFanShapes = 0;
		bufferIndex = 0;
	}

	public void endBatch() {
		int position = 0;
		vertices.bind();
		for (int i = 0; i < numFanShapes; i++) {
			vertices.setVertices(verticesBuffer, position,
					numVerticePerFanShape[i] * 6);
			// vertices.bind();
			vertices.draw(GL10.GL_TRIANGLE_FAN, 0, numVerticePerFanShape[i]);
			position += numVerticePerFanShape[i] * 6;
			// vertices.unbind();
		}
		vertices.unbind();
		// vertices.setVertices(verticesBuffer, 0, bufferIndex);
		// vertices.draw(GL10.GL_TRIANGLE_FAN, 0, bufferIndex/6);
	}
	/**画一个环形的一部分，r，g，b，a表示环形的颜色，b_r,b_g,b_a表示环形内部的背景颜色<br>
	 * 注意：绘制这样一个环相当于绘制了两个扇形
	 * @param fanShape 对应的与环形外部半径相等的扇形
	 * @param insideRadius 环形的内部半径径*/
	public void drawFanShapeRing(FanShape fanShape, float insideRadius, float r, float g, float b,
			float a, float b_r, float b_g, float b_b){
		float tmp = fanShape.c.radius;
		drawFanShape(fanShape, r, g, b, a);
		fanShape.c.radius = insideRadius;
		drawCircle(fanShape.c, b_r, b_g, b_b, 1);
		fanShape.c.radius = tmp;
	}
	public void drawCircle(Circle c, float r, float g, float b, float a) {
		// Log.d("d", "range:"+range+" " +(fanShape.end -
		// fanShape.start)/360f*numVerticePerShape);
		// float[] x = new float[range+2];
		// float[] y = new float[range+2];
		int numOfVertices;
		if (fastDraw) {
			numOfVertices = fastFillVertices(c, 0, 360, r, g, b, a);
		} else {
			numOfVertices = fillVertices(c, 0, 360, r, g, b, a);
		}

		numVerticePerFanShape[numFanShapes] = numOfVertices;
		numFanShapes++;
	}

	public void drawFanShape(FanShape fanShape, float r, float g, float b,
			float a) {
		if (fanShape.getEnd() - fanShape.getStart() > 0) {
			Circle c = fanShape.c;
			int numOfVertices;
			if (fastDraw) {
				numOfVertices = fastFillVertices(c, fanShape.getStart(),
						fanShape.getEnd(), r, g, b, a);
			} else {
				numOfVertices = fillVertices(c, fanShape.getStart(), fanShape.getEnd(),
						r, g, b, a);
			}

			numVerticePerFanShape[numFanShapes] = numOfVertices;
			numFanShapes++;
		}
	}

	public int fillVertices(Circle c, float f_start, float f_end, float r,
			float g, float b, float a) {
		float step = 360f / numVerticePerCircle;
		float angle = 90 - f_start;
		int range = (int) Math
				.round(((f_end - f_start) / 360f * numVerticePerCircle));

		verticesBuffer[bufferIndex++] = c.center.x;
		verticesBuffer[bufferIndex++] = c.center.y;
		verticesBuffer[bufferIndex++] = r;
		verticesBuffer[bufferIndex++] = g;
		verticesBuffer[bufferIndex++] = b;
		verticesBuffer[bufferIndex++] = a;

		for (int i = 0; i <= range; i++, angle -= step) {
			verticesBuffer[bufferIndex++] = c.center.x + c.radius
					* (float)Math.cos(angle * (float) Math.PI / 180);
			verticesBuffer[bufferIndex++] = c.center.y + c.radius
					* (float)Math.sin(angle * (float) Math.PI / 180);
			verticesBuffer[bufferIndex++] = r;
			verticesBuffer[bufferIndex++] = g;
			verticesBuffer[bufferIndex++] = b;
			verticesBuffer[bufferIndex++] = a;
		}
		return range + 2;
	}

	public int fastFillVertices(Circle c, float f_start, float f_end, float r,
			float g, float b, float a) {
		// float x0, y0, x1, y1, x2, y2, x3, y3;

		float step = 720 / numVerticePerCircle;
		int start = (int) (f_start / 360 * 720);
		int end = (int) (f_end / 360 * 720);
		int count = 0;
		
		verticesBuffer[bufferIndex++] = c.center.x;
		verticesBuffer[bufferIndex++] = c.center.y;
		verticesBuffer[bufferIndex++] = r;
		verticesBuffer[bufferIndex++] = g;
		verticesBuffer[bufferIndex++] = b;
		verticesBuffer[bufferIndex++] = a;
		count++;
		
		for (int i = start; i < end; i += step) {
			verticesBuffer[bufferIndex++] = c.center.x + c.radius
					* vectors[i].x;
			verticesBuffer[bufferIndex++] = c.center.y + c.radius
					* vectors[i].y;
			verticesBuffer[bufferIndex++] = r;
			verticesBuffer[bufferIndex++] = g;
			verticesBuffer[bufferIndex++] = b;
			verticesBuffer[bufferIndex++] = a;
			count++;
		}
		if (end == 720) {
			verticesBuffer[bufferIndex++] = c.center.x + c.radius
					* vectors[0].x;
			verticesBuffer[bufferIndex++] = c.center.y + c.radius
					* vectors[0].y;
		} else {
			verticesBuffer[bufferIndex++] = c.center.x + c.radius
					* vectors[end].x;
			verticesBuffer[bufferIndex++] = c.center.y + c.radius
					* vectors[end].y;
		}
		verticesBuffer[bufferIndex++] = r;
		verticesBuffer[bufferIndex++] = g;
		verticesBuffer[bufferIndex++] = b;
		verticesBuffer[bufferIndex++] = a;
		count++;
		return count;
	}
}
