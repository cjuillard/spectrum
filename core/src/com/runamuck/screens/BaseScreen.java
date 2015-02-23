package com.runamuck.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.runamuck.ScreenManager;
import com.runamuck.SpectrumGame;
import com.runamuck.rendering.RenderContext;


public class BaseScreen {
	
	protected ScreenManager screenManager;
	protected Stack uiRoot;
	protected Skin skin;
	protected Batch batch;
	protected RenderContext renderContext;
	protected AssetManager assetManager;
	
	public BaseScreen() {
		SpectrumGame game = (SpectrumGame)Gdx.app.getApplicationListener();
		this.skin = game.getSkin();
		this.screenManager = game.getScreenManager();
		this.batch = game.getBatch();
		this.renderContext = game.getRenderContext();
		this.assetManager = game.getAssetManager();
		
		uiRoot = new Stack();
		uiRoot.setFillParent(true);
	}
	
	public Group getUiRoot() {
		return uiRoot;
	}
	
	/** Returns a list of additional input processor this screen uses. */
	public InputProcessor[] getInputProcessors() {
		return new InputProcessor[0];
	}
			
	public void show() {
		
		
	}

	public void render(float delta) {

	}

	public void resize(int width, int height) {

	}
	
	public void pause() {

	}

	public void resume() {

	}

	public void hide() {

	}

	public void dispose() {

	}
	
	public boolean backButtonPressed() {
		if(screenManager.popScreen()) {
			return true;
		}
		return false;
	}

}
