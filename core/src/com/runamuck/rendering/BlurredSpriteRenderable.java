package com.runamuck.rendering;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.runamuck.simulation.Entity;

public class BlurredSpriteRenderable extends SpriteEntityRenderable {
	
	private class BlurParticle implements Poolable {
		public Sprite sprite;
		public float currDuration;
		public float duration;
		public float maxAlpha;
		
		public void update(float elapsed) {
			currDuration = Math.max(0, currDuration - elapsed);
			sprite.setAlpha(currDuration / duration * maxAlpha);
		}
		
		public boolean isAlive() {
			return currDuration > 0;
		}

		@Override
		public void reset() {
			currDuration = duration;
		}

		public void init(Vector2 pos, float duration, float maxAlpha) {
			this.sprite.setCenter(pos.x, pos.y);
			this.duration = duration;
			this.currDuration = duration;
			this.maxAlpha = maxAlpha;
		}
	}
	private Array<BlurParticle> blurredSprites = new Array<BlurParticle>();
	
	Pool<BlurParticle> blurParticlePool = new Pool<BlurParticle>() {

		@Override
		protected BlurParticle newObject() {
			BlurParticle particle = new BlurParticle();
			particle.sprite = new Sprite(aboveFogBlurSprite);
			return particle;
		}
	};
	private float blurSpawnDelta = .5f;
	private float currMoveDelta = 0;
	private float fadeLength = .5f;
	private float startAlpha;
	
	private Vector2 bodyLastPosition = new Vector2();

	private Sprite aboveFogBlurSprite;
	
	public BlurredSpriteRenderable(Entity entity, Sprite belowFog, Sprite aboveFog, Sprite aboveFogBlur) {
		super(entity, belowFog, aboveFog);
		
		this.aboveFogBlurSprite = aboveFogBlur;
		
		startAlpha = aboveFogBlur.getColor().a;
	}

	@Override
	public void update(float elapsed) {
		super.update(elapsed);

		Body body = entity.getBody();
		currMoveDelta += bodyLastPosition.dst(body.getPosition());
		bodyLastPosition.set(body.getPosition());
		if(currMoveDelta > blurSpawnDelta) {
			BlurParticle newParticle = blurParticlePool.obtain();
			newParticle.init(body.getPosition(), fadeLength, startAlpha);
			
			blurredSprites.add(newParticle);
			
			currMoveDelta = 0;
		}
		
		for(int i = blurredSprites.size - 1; i >= 0; i--) {
			BlurParticle particle = blurredSprites.get(i);
			particle.update(elapsed);
			
			if(!particle.isAlive()) {
				blurredSprites.removeIndex(i);
				blurParticlePool.free(particle);
			}
		}
	}
	
	@Override
	public void render(RenderContext renderContext) {
		super.render(renderContext);
	}
	
	@Override
	public void renderAboveFog(RenderContext renderContext) {
		renderContext.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		for(int i = 0; i < blurredSprites.size; i++) {
			blurredSprites.get(i).sprite.draw(renderContext.getBatch());
		}

		super.renderAboveFog(renderContext);

		renderContext.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}
}
