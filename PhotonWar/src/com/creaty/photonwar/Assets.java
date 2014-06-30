package com.creaty.photonwar;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.gl.Font;
import com.badlogic.androidgames.framework.gl.Texture;
import com.badlogic.androidgames.framework.gl.TextureRegion;
import com.badlogic.androidgames.framework.impl.GLGame;

public class Assets {

	public static Texture texture;
	public static Texture items;
	public static TextureRegion cannonRegion;
	public static TextureRegion ballRegion;
	public static TextureRegion bobRegion;
	public static Font font;

	public static void load(GLGame game) {
		texture = new Texture((GLGame) game, "atlas.png");
		cannonRegion = new TextureRegion(texture, 0, 0, 64, 32);
		ballRegion = new TextureRegion(texture, 0, 32, 16, 16);
		bobRegion = new TextureRegion(texture, 32, 32, 32, 32);
		
		items = new Texture(game, "items.png");
		font = new Font(items, 224, 0, 16, 16, 20);
	}

	public static void reload() {
		texture.reload();
		items.reload();
	}
}
