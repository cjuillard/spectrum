package com.runamuck;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.runamuck.screens.MainMenuScreen;

public class SpectrumGame implements ApplicationListener {

	private Skin skin;
	private Stage stage;
	private ScreenManager screenManager;
	
	@Override
	public void create() {
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		stage = new Stage();
		
		screenManager = new ScreenManager();
		screenManager.setScreen(new MainMenuScreen());
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
}
