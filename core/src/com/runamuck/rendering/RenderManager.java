package com.runamuck.rendering;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.runamuck.data.EntityDefinition;
import com.runamuck.data.EntityDefinitions;
import com.runamuck.simulation.Entity;
import com.runamuck.simulation.ISpectrumWorldListener;
import com.runamuck.simulation.SpectrumWorld;

public class RenderManager implements ISpectrumWorldListener{
	
	private RenderContext renderContext;
	private IRenderable background;
	private Array<IRenderable> renderables = new Array<IRenderable>();
	private AssetManager assetManager;
	
	public RenderManager(RenderContext renderContext, AssetManager assetManager) {
		this.renderContext = renderContext;
		this.assetManager = assetManager;
	}
	
	public void update(float timeElapsed) {
		for(IRenderable renderable : renderables) {
			renderable.update(timeElapsed);
		}
	}
	
	public void renderBeforeFog() {
		renderContext.getBatch().begin();
		if(background != null) background.render(renderContext);
		
		renderContext.getBatch().flush();
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
				renderables.add(createSpriteRenderable(entity, "data/marble.png", null));
			}
			break;
		case RANDOM_MOVE:
			{
				renderables.add(createSpriteRenderable(entity, "data/enemy1_bf.png", "data/enemy1_af.png"));
			}
			break;
		case FOLLOW_SLOW:
			{
				renderables.add(createSpriteRenderable(entity, "data/enemy2_bf.png", "data/enemy1_af.png"));
			}
			break;
		case FOLLOW_FAST:
			{		
				renderables.add(createSpriteRenderable(entity, "data/enemy3_bf.png", "data/enemy1_af.png"));
			}
		break;
		}
	}
	
	private SpriteEntityRenderable createSpriteRenderable(Entity entity, String belowFog, String aboveFog) {
		TextureParameter texParam = new TextureParameter();
		texParam.magFilter = TextureFilter.Linear;
		texParam.minFilter = TextureFilter.Linear;
		
		assetManager.load(belowFog, Texture.class, texParam);
		if(aboveFog != null) assetManager.load(aboveFog, Texture.class, texParam);
		assetManager.finishLoading();
		
		Sprite belowFogSprite = new Sprite(assetManager.get(belowFog, Texture.class));
		Sprite aboveFogSprite = aboveFog != null ? new Sprite(assetManager.get(aboveFog, Texture.class)) : null;
		
		EntityDefinition def = EntityDefinitions.get(entity.getType());
		belowFogSprite.setSize(def.getWidth(), def.getHeight());
		belowFogSprite.setOriginCenter();
		if(aboveFogSprite != null) {
			aboveFogSprite.setSize(def.getWidth(), def.getHeight());
			aboveFogSprite.setOriginCenter();
		}
		
		SpriteEntityRenderable renderable = new SpriteEntityRenderable(entity, belowFogSprite, aboveFogSprite);
		return renderable;
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

	public IRenderable getBackground() {
		return background;
	}

	public void setBackground(IRenderable background) {
		this.background = background;
	}
}
