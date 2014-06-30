package com.badlogic.androidgames.framework.gl;

/** 材质的一个特定矩形区域 */
public class TextureRegion {
	/**
	 * l_x-左侧x坐标值<br>
	 * t_y-上侧y坐标值
	 */
	public final float l_x, t_y;
	/**
	 * r_x-右侧x坐标值<br>
	 * b_y-下侧y坐标值
	 */
	public final float r_x, b_y;
	public final Texture texture;

	public TextureRegion(Texture texture, float x, float y, float width,
			float height) {
		this.l_x = x / texture.width;
		this.t_y = y / texture.height;
		this.r_x = this.l_x + width / texture.width;
		this.b_y = this.t_y + height / texture.height;
		this.texture = texture;
	}
}
