package com.runamuck.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class RenderContext {
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private ShaderProgram unshadedShader;
	private OrthographicCamera cam;
	
	public RenderContext(SpriteBatch batch) {
		this.batch = batch;
		shapeRenderer = new ShapeRenderer();
		unshadedShader = new ShaderProgram(Gdx.files.local("shaders/unshaded-vert.glsl"), Gdx.files.local("shaders/unshaded-frag.glsl"));
		if(!unshadedShader.isCompiled()) {
			System.out.println("Unshaded shader failed to compile...");
			System.out.println(unshadedShader.getLog());
		}
	}

	public void setCamera(OrthographicCamera cam) {
		this.cam = cam;
	}
	
	public OrthographicCamera getCamera() {
		return cam;
	}
	
	public SpriteBatch getBatch() {
		return batch;
	}
	
	public ShapeRenderer getShapeRenderer() {
		return shapeRenderer;
	}
	
	public ShaderProgram getUnshadedShader() {
		return unshadedShader;
	}
}
