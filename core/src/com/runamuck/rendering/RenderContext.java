package com.runamuck.rendering;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class RenderContext {
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	
	public RenderContext(SpriteBatch batch) {
		this.batch = batch;
		shapeRenderer = new ShapeRenderer();
	}

	public SpriteBatch getBatch() {
		return batch;
	}
	
	public ShapeRenderer getShapeRenderer() {
		return shapeRenderer;
	}
}
