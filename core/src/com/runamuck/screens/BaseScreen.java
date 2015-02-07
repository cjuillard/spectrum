package com.runamuck.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.runamuck.SpectrumGame;



public class BaseScreen {
	
	protected Stack uiRoot;
	protected Skin skin;

	public BaseScreen() {
		SpectrumGame game = (SpectrumGame)Gdx.app.getApplicationListener();
		this.skin = game.getSkin();
		uiRoot = new Stack();
		uiRoot.setFillParent(true);
	}
	
	public Group getUiRoot() {
		return uiRoot;
	}
	
	public void show() {
		
		
	}

	public void render(float delta) {
		// TODO Auto-generated method stub

	}

	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}
	
	public void pause() {
		// TODO Auto-generated method stub

	}

	public void resume() {
		// TODO Auto-generated method stub

	}

	public void hide() {
		// TODO Auto-generated method stub

	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

}
