package com.runamuck.rendering;

import com.badlogic.gdx.utils.Array;

public class RenderManager {
	
	private RenderContext renderContext;
	private Array<IRenderable> renderables = new Array<IRenderable>();
	
	public RenderManager(RenderContext renderContext) {
		this.renderContext = renderContext;
	}
	
	public void update(float timeElapsed) {
		
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
}
