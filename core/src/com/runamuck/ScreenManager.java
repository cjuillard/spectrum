package com.runamuck;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.runamuck.screens.BaseScreen;

public class ScreenManager {
	protected BaseScreen screen;
	private Skin skin;
	private Stage stage;
	
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
	public void setScreen (BaseScreen screen) {
		if (this.screen != null) {
			this.screen.hide();
			this.screen.getUiRoot().remove();
		}
		this.screen = screen;
		Gdx.input.setInputProcessor(stage);	// todo: also add the screen's custom input processors
		if (this.screen != null) {
			this.screen.show();
			stage.addActor(this.screen.getUiRoot());
			this.screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
	}

	/** @return the currently active {@link BaseScreen}. */
	public BaseScreen getScreen () {
		return screen;
	}
}
