package com.runamuck;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.runamuck.rendering.RenderContext;
import com.runamuck.screens.MainMenuScreen;

public class SpectrumGame implements ApplicationListener {

	private Skin skin;
	private Stage stage;
	private ScreenManager screenManager;
	private SpriteBatch batch;
	private RenderContext renderContext;
	private AssetManager assetManager;
	
	@Override
	public void create() {
		assetManager = new AssetManager();
		
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		
		batch = new SpriteBatch();
		stage = new Stage(new ScalingViewport(Scaling.stretch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new OrthographicCamera()),
								batch);
		renderContext = new RenderContext(batch);
		
		screenManager = new ScreenManager();
		screenManager.pushScreen(new MainMenuScreen());
	}
	
	public void dispose () {
		screenManager.dispose();
	}

	public void pause () {
		screenManager.pause();
	}

	public void resume () {
		screenManager.resume();
	}

	public void render () {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		screenManager.render();
		
		stage.act();
		stage.draw();
	}

	public void resize (int width, int height) {
		stage.getViewport().update(width, height, true);
		screenManager.resize(width, height);
	}

	public ScreenManager getScreenManager() {
		return screenManager;
	}
	
	public Stage getStage() {
		return stage;
	}
	
	public Skin getSkin() {
		return skin;
	}
	
	public SpriteBatch getBatch() {
		return batch;
	}
	
	public RenderContext getRenderContext() {
		return renderContext;
	}

	public AssetManager getAssetManager() {
		return assetManager;
	}
}
