package com.runamuck.rendering;

public interface IRenderable {
	void update(float elapsed);
	
	void render(RenderContext renderContext);
	
	void renderAfterFog(RenderContext renderContext);
}
