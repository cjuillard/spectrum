package com.runamuck.rendering;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.runamuck.simulation.Entity;

public class BlurredSpriteRenderable extends SpriteEntityRenderable {
	
	private Array<Sprite> blurredSprites = new Array<Sprite>();
	
	private float blurMoveDelay = .4f;
	private int numBlurImages = 2;
	private float fadeLength = blurMoveDelay * numBlurImages;
	private float startAlpha;
	
	private float lastDelay = blurMoveDelay;
	
	public BlurredSpriteRenderable(Entity entity, Sprite belowFog, Sprite aboveFog, Sprite aboveFogBlur) {
		super(entity, belowFog, aboveFog);
		
		startAlpha = aboveFogBlur.getColor().a;
		blurredSprites.add(aboveFogBlur);
		for(int i = 0; i < numBlurImages - 1; i++) {
			Sprite sprite = new Sprite(aboveFogBlur);
			blurredSprites.add(sprite);
		}
	}

	@Override
	public void update(float elapsed) {
		super.update(elapsed);
		
		lastDelay -= elapsed;
		Body body = entity.getBody();
		while(lastDelay <= 0) {
			lastDelay += blurMoveDelay;
			
			Sprite sprite = blurredSprites.removeIndex(blurredSprites.size - 1);
			sprite.setCenter(body.getPosition().x, body.getPosition().y);
			blurredSprites.insert(0, sprite);
			sprite.setAlpha(startAlpha);
		}
		
		for(int i = 0; i < blurredSprites.size; i++) {
			Sprite sprite = blurredSprites.get(i);
			sprite.setAlpha(Math.max(0, sprite.getColor().a - (elapsed / fadeLength) * startAlpha));
			
			//System.out.print(sprite.getColor().a + " ");
		}
		//System.out.println();
	}
	
	@Override
	public void render(RenderContext renderContext) {
		super.render(renderContext);
	}
	
	@Override
	public void renderAboveFog(RenderContext renderContext) {
		renderContext.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		for(int i = 0; i < blurredSprites.size; i++) {
			blurredSprites.get(i).draw(renderContext.getBatch());
		}

		super.renderAboveFog(renderContext);

		renderContext.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}
}
