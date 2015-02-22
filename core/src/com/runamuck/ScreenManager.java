package com.runamuck;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.runamuck.screens.BaseScreen;

public class ScreenManager {
	protected BaseScreen screen;
	private Skin skin;
	private Stage stage;
	
	private Array<BaseScreen> screens = new Array<BaseScreen>();
	
	public ScreenManager() {
		SpectrumGame game = (SpectrumGame)Gdx.app.getApplicationListener();
		this.skin = game.getSkin();
		this.stage = game.getStage();
	}

	public void create() {
		
	}
	
	public void dispose () {
		if (screen != null) screen.hide();
	}

	public void pause () {
		if (screen != null) screen.pause();
	}

	public void resume () {
		if (screen != null) screen.resume();
	}

	public void render () {
		if (screen != null) screen.render(Gdx.graphics.getDeltaTime());
	}

	public void resize (int width, int height) {
		if (screen != null) screen.resize(width, height);
	}

	/** Sets the current screen. {@link BaseScreen#hide()} is called on any old screen, and {@link BaseScreen#show()} is called on the new
	 * screen, if any.
	 * @param screen may be {@code null} */
	public void pushScreen (BaseScreen screen) {
		if (this.screen != null) {
			this.screen.hide();
			this.screen.getUiRoot().remove();
		}
		this.screen = screen;
		
		// Setup the input
		InputMultiplexer processors = new InputMultiplexer(screen.getInputProcessors());
		processors.addProcessor(0, stage);
		processors.addProcessor(1, getBackButtonListener());
		Gdx.input.setInputProcessor(processors);
		
		if (this.screen != null) {
			this.screen.show();
			stage.addActor(this.screen.getUiRoot());
			this.screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
		
		screens.add(screen);
	}
	
	protected InputAdapter getBackButtonListener() {
		return new InputAdapter() {
			@Override
			public boolean keyUp(int keycode) {
				if(keycode == Keys.BACKSPACE) {
					if(screen.backButtonPressed()) {
						return true;
					}
				}
				
				return false;
			}
		};
	}
	
	public boolean popScreen() {
		if(screens.size == 1) return false;
		
		if (this.screen != null) {
			this.screen.hide();
			this.screen.getUiRoot().remove();
		}
		screens.pop();
		this.screen = screens.get(screens.size - 1);
		
		// Setup the input
		InputMultiplexer processors = new InputMultiplexer(screen.getInputProcessors());
		processors.addProcessor(0, stage);
		Gdx.input.setInputProcessor(processors);
		
		if (this.screen != null) {
			this.screen.show();
			stage.addActor(this.screen.getUiRoot());
			this.screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
		
		return true;
	}

	/** @return the currently active {@link BaseScreen}. */
	public BaseScreen getScreen () {
		return screen;
	}
}
