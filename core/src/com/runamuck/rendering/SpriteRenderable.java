package com.runamuck.rendering;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.runamuck.simulation.Entity;
import com.sun.istack.internal.Nullable;

public class SpriteRenderable implements IRenderable {

	protected Sprite belowFog;
	private Entity entity;
	private Sprite afterFog;
	
	public SpriteRenderable(Entity entity, Sprite belowFog, @Nullable Sprite afterFog) {
		this.entity = entity;
		this.belowFog = belowFog;
		this.afterFog = afterFog;
	}
	
	@Override
	public void update(float elapsed) {
		Body body = entity.getBody();
		belowFog.setPosition(body.getPosition().x, body.getPosition().y);
		belowFog.setRotation(body.getAngle());
		
		if(afterFog != null) {
			afterFog.setPosition(body.getPosition().x, body.getPosition().y);
			afterFog.setRotation(body.getAngle());
		}
	}

	@Override
	public void render(RenderContext renderContext) {
		belowFog.draw(renderContext.getBatch());
	}
	
	@Override
	public void renderAfterFog(RenderContext renderContext) {
		if(afterFog != null) {
			afterFog.draw(renderContext.getBatch());
		}
	}

}