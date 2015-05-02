package com.runamuck.rendering;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class SpriteRenderable implements IRenderable {

	protected Sprite sprite;

	public SpriteRenderable(Sprite sprite) {
		this.sprite = sprite;
	}
	
	@Override
	public void update(float elapsed) {
		
	}

	@Override
	public void render(RenderContext renderContext) {
		sprite.draw(renderContext.getBatch());
	}

	@Override
	public void renderAboveFog(RenderContext renderContext) {
		
	}

}
