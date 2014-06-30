package com.badlogic.androidgames.framework.gl;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import com.badlogic.androidgames.framework.FileIO;
import com.badlogic.androidgames.framework.impl.GLGame;
import com.badlogic.androidgames.framework.impl.GLGraphics;

public class Texture {
	GLGraphics glGraphics;
	FileIO fileIO;
	String fileName;
	int textureId;
	int minFilter;
	int magFilter;
	public int width;
	public int height;

	public Texture(GLGame glGame, String fileName) {
		this.glGraphics = glGame.getGLGraphics();
		this.fileIO = glGame.getFileIO();
		this.fileName = fileName;
		load();
	}

	private void load() {
		GL10 gl = glGraphics.getGL();
		int[] textureIds = new int[1];
		gl.glGenTextures(1, textureIds, 0); // 为即将存储的材质分配编号(texture name)
		textureId = textureIds[0];

		InputStream in = null;
		try {
			in = fileIO.readAsset(fileName);
			Bitmap bitmap = BitmapFactory.decodeStream(in);
			/*
			 * glBindTexture(int target,int texture) glBindTexture lets you
			 * create or use a named texture. Calling glBindTexture with target
			 * set to GL_TEXTURE_2D, and texture set to the name of the new
			 * texture binds the texture name to the target. When a texture is
			 * bound to a target, the previous binding for that target is
			 * automatically broken.
			 */
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId); // 绑定材质
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
			setFilters(GL10.GL_NEAREST, GL10.GL_NEAREST);// 默认放缩时按照材质真实质量渲染
			gl.glBindTexture(GL10.GL_TEXTURE_2D, 0); // 解绑
			width = bitmap.getWidth();
			height = bitmap.getHeight();
			bitmap.recycle();
		} catch (IOException e) {
			throw new RuntimeException("Couldn't load texture '" + fileName
					+ "'", e);
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
				}
		}
	}

	public void reload() {
		load();
		bind();
		setFilters(minFilter, magFilter);
		glGraphics.getGL().glBindTexture(GL10.GL_TEXTURE_2D, 0);
	}

	/**
	 * 设置当材质在屏幕上放缩时的效果
	 * 
	 * @param minFilter设置材质缩小时如何渲染
	 * @param magFilter设置材质放大时如何渲染
	 */
	public void setFilters(int minFilter, int magFilter) {
		this.minFilter = minFilter;
		this.magFilter = magFilter;
		GL10 gl = glGraphics.getGL();
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				minFilter);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				magFilter);
	}

	public void bind() {
		GL10 gl = glGraphics.getGL();
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
	}
	
	public void unbind(){
		GL10 gl = glGraphics.getGL();
		gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
	}

	/** 将材质从显存中移除,注意：此方法会自动解绑当前材质 */
	public void dispose() {
		GL10 gl = glGraphics.getGL();
		gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
		int[] textureIds = { textureId };
		gl.glDeleteTextures(1, textureIds, 0);
	}
}