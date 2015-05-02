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
	private Array<IRenderable> renderables = new Array<IRenderable>();
	private AssetManager assetManager;
	
	private TextureParameter linearTexParm = new TextureParameter();
	
	public RenderManager(RenderContext renderContext, AssetManager assetManager) {
		this.renderContext = renderContext;
		this.assetManager = assetManager;
		
		linearTexParm.magFilter = TextureFilter.Linear;
		linearTexParm.minFilter = TextureFilter.Linear;
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
				BlurredSpriteRenderable renderable = createBlurredSpriteRenderable(entity, null, "data/marble.png", "data/marble_blur.png", .2f);
				renderables.add(renderable);
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
		loadTexture(belowFog);
		loadTexture(aboveFog);
		assetManager.finishLoading();
		
		Sprite belowFogSprite = belowFog != null ? new Sprite(assetManager.get(belowFog, Texture.class)) : null;
		Sprite aboveFogSprite = aboveFog != null ? new Sprite(assetManager.get(aboveFog, Texture.class)) : null;
		
		EntityDefinition def = EntityDefinitions.get(entity.getType());
		if(belowFogSprite != null) {
			belowFogSprite.setSize(def.getWidth(), def.getHeight());
			belowFogSprite.setOriginCenter();
		}
		if(aboveFogSprite != null) {
			aboveFogSprite.setSize(def.getWidth(), def.getHeight());
			aboveFogSprite.setOriginCenter();
		}
		
		SpriteEntityRenderable renderable = new SpriteEntityRenderable(entity, belowFogSprite, aboveFogSprite);
		return renderable;
	}
	
	private BlurredSpriteRenderable createBlurredSpriteRenderable(Entity entity, String belowFog, String aboveFog, String aboveFogBlur, float alpha) {
		loadTexture(belowFog);
		loadTexture(aboveFog);
		loadTexture(aboveFogBlur);
		assetManager.finishLoading();
		
		EntityDefinition def = EntityDefinitions.get(entity.getType());
		
		Sprite belowFogSprite = loadSprite(belowFog, def, alpha);
		Sprite aboveFogSprite = loadSprite(aboveFog, def, alpha);
		Sprite aboveFogBlurSprite = loadSprite(aboveFogBlur, def, alpha);
		
		BlurredSpriteRenderable renderable = new BlurredSpriteRenderable(entity, belowFogSprite, aboveFogSprite, aboveFogBlurSprite);
		return renderable;
	}
	
	private Sprite loadSprite(String path, EntityDefinition def, float alpha) {
		if(path == null) return null;
		
		Sprite sprite = new Sprite(assetManager.get(path, Texture.class));
		sprite.setSize(def.getWidth(), def.getHeight());
		sprite.setOriginCenter();
		sprite.setAlpha(alpha);
		
		return sprite;
	}
	
	private void loadTexture(String path) {
		if(path != null) {
			assetManager.load(path, Texture.class, linearTexParm);
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
