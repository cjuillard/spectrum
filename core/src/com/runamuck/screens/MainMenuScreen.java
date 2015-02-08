package com.runamuck.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;


public class MainMenuScreen extends BaseScreen {

	@Override
	public void show() {
		Table rootTable = new Table();
		rootTable.setFillParent(true);
		uiRoot.addActor(rootTable);
		
		TextButton startGameButton = new TextButton("Start Game", skin);
		rootTable.add(startGameButton).expand();
		startGameButton.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.postRunnable(new Runnable() {

					@Override
					public void run() {
						screenManager.setScreen(new GameplayScreen());
					}
					
				});
				
			}
		});
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
