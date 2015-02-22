package com.runamuck.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.runamuck.data.EntityDefinition;
import com.runamuck.data.EntityDefinitions;
import com.runamuck.simulation.Entity;
import com.runamuck.simulation.ISpectrumWorldListener;
import com.runamuck.simulation.SpectrumWorld;

public class RenderManager implements ISpectrumWorldListener{
	
	private RenderContext renderContext;
	private Array<IRenderable> renderables = new Array<IRenderable>();
	private TextureRegion marbleRegion;
	private TextureRegion en1UFRegion;
	private TextureRegion en1AFRegion;
	
	public RenderManager(RenderContext renderContext) {
		this.renderContext = renderContext;
		
		marbleRegion = new TextureRegion(new Texture(
				Gdx.files.internal("data/marble.png")));
		
		en1UFRegion = new TextureRegion(new Texture(Gdx.files.internal("data/enemy1_bf.png")));
		en1AFRegion = new TextureRegion(new Texture(Gdx.files.internal("data/enemy1_af.png")));
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
			renderable.renderAboveFog(renderContext);
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
			EntityDefinition def = EntityDefinitions.get(entity.getType());
			sprite.setSize(def.getWidth(), def.getHeight());
			sprite.setOriginCenter();
			SpriteEntityRenderable renderable = new SpriteEntityRenderable(entity, sprite, null);
			renderables.add(renderable);
		}
			break;
		case ENEMY1:
			EntityDefinition def = EntityDefinitions.get(entity.getType());
			Sprite underFogSprite = new Sprite(en1UFRegion);
			underFogSprite.setSize(def.getWidth(), def.getHeight());
			underFogSprite.setOriginCenter();
			Sprite aboveFogSprite = new Sprite(en1AFRegion);
			aboveFogSprite.setSize(def.getWidth(), def.getHeight());
			aboveFogSprite.setOriginCenter();
			SpriteEntityRenderable renderable = new SpriteEntityRenderable(entity, underFogSprite, aboveFogSprite);
			renderables.add(renderable);
			break;
		}
	}
	
	@Override
	public void entityAdded(Entity entity) {
		loadEntity(entity);
	}

	@Override
	public void entityRemoved(Entity entity) {
		for(int i = renderables.size - 1; i >= 0; i--) {
			IRenderable renderable = renderables.get(i);
			if(renderable instanceof IEntityRenderable) {
				if(((IEntityRenderable) renderable).getEntity() == entity) {
					renderables.removeIndex(i);
				}
			}
		}
	}
}
