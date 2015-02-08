package com.runamuck.rendering;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RenderContext {
	private SpriteBatch batch;
	
	public RenderContext(SpriteBatch batch) {
		this.batch = batch;
	}

	public SpriteBatch getBatch() {
		return batch;
	}
}
