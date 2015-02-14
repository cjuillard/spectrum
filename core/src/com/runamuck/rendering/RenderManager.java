package com.runamuck.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.runamuck.data.EntityDefinition;
import com.runamuck.simulation.Entity;
import com.runamuck.simulation.ISpectrumWorldListener;
import com.runamuck.simulation.SpectrumWorld;

public class RenderManager implements ISpectrumWorldListener{
	
	private RenderContext renderContext;
	private Array<IRenderable> renderables = new Array<IRenderable>();
	private TextureRegion marbleRegion;
	
	public RenderManager(RenderContext renderContext) {
		this.renderContext = renderContext;
		
		marbleRegion = new TextureRegion(new Texture(
				Gdx.files.internal("data/marble.png")));
	}
	
	public void update(float timeElapsed) {
		for(IRenderable renderable : renderables) {
			renderable.update(timeElapsed);
		}
	}
	
	public void renderBeforeFog() {
		renderContext.getBatch().begin();
		for(IRenderable renderable : renderables) {
			renderable.render(renderContext);
		}
		renderContext.getBatch().end();
		
	}
	
	public void renderAfterFog() {
		renderContext.getBatch().begin();
		for(IRenderable renderable : renderables) {
			renderable.renderAfterFog(renderContext);
		}
		renderContext.getBatch().end();
		
	}
	
	public void addRenderable(IRenderable renderable) {
		this.renderables.add(renderable);
	}

	public void loadWorld(SpectrumWorld world) {
		for(Entity entity : world.getEntities()) {
			loadEntity(entity);
		}
	}
	
	public void loadEntity(Entity entity) {
		switch(entity.getType()) {
		case PLAYER:
		{
			Sprite sprite = new Sprite(marbleRegion);
			sprite.setSize(EntityDefinition.getWidth(entity.getType()), EntityDefinition.getHeight(entity.getType()));
			sprite.setOriginCenter();
			SpriteRenderable renderable = new SpriteRenderable(entity, sprite, null);
			renderables.add(renderable);
		}
			break;
		case ENEMY1:
			break;
		}
	}
	
	@Override
	public void entityAdded(Entity entity) {
		loadEntity(entity);
	}

	@Override
	public void entityRemoved(Entity entity) {
		// TODO add entity removing code
		
	}
}
