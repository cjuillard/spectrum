package com.runamuck.rendering;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.runamuck.simulation.Entity;
import com.sun.istack.internal.Nullable;

public class SpriteEntityRenderable implements IEntityRenderable {

	protected Sprite belowFog;
	protected Entity entity;
	protected Sprite aboveFog;
	
	public SpriteEntityRenderable(Entity entity, Sprite belowFog, @Nullable Sprite aboveFog) {
		this.entity = entity;
		this.belowFog = belowFog;
		this.aboveFog = aboveFog;
	}
	
	@Override
	public void update(float elapsed) {
		
		Body body = entity.getBody();
		float angle = MathUtils.radiansToDegrees * body.getAngle();
		if(belowFog != null) {
			belowFog.setCenter(body.getPosition().x, body.getPosition().y);
			belowFog.setRotation(angle);
		}
//		belowFog.setAlpha((entity.getMaxHP() - entity.getHp()) / entity.getMaxHP());
		if(aboveFog != null) {
			aboveFog.setCenter(body.getPosition().x, body.getPosition().y);
			aboveFog.setRotation(angle);
		}
	}

	@Override
	public void render(RenderContext renderContext) {
		if(belowFog != null) {
			belowFog.draw(renderContext.getBatch());
		}
	}
	
	@Override
	public void renderAboveFog(RenderContext renderContext) {
		if(aboveFog != null) {
			aboveFog.draw(renderContext.getBatch());
		}
	}

	@Override
	public Entity getEntity() {
		return entity;
	}

	public Sprite getBelowFog() {
		return belowFog;
	}

	public void setBelowFog(Sprite belowFog) {
		this.belowFog = belowFog;
	}

	public Sprite getAboveFog() {
		return aboveFog;
	}

	public void setAboveFog(Sprite aboveFog) {
		this.aboveFog = aboveFog;
	}
	
	

}
