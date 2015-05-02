package com.runamuck.screens;

import java.util.ArrayList;

import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.runamuck.rendering.DeformablePlane;
import com.runamuck.rendering.RenderManager;
import com.runamuck.rendering.SpriteRenderable;
import com.runamuck.simulation.Entity;
import com.runamuck.simulation.SpectrumWorld;

public class GameplayScreen extends BaseScreen {
	// TODO remove these once we have the main character light weapon
	private static final int RANDOM_LIGHTS = 4;	
	static final int RAYS_PER_BALL = 128;
	static final float LIGHT_DISTANCE = 10f;
	
	private OrthographicCamera camera;
	private RayHandler rayHandler;
	
	private ArrayList<Light> lights = new ArrayList<Light>(RANDOM_LIGHTS);
	
	private RenderManager renderManager;
	private SpectrumWorld spectrumWorld;
	private Sprite background;
	private DeformablePlane deformablePlane;
	
	@Override
	public void show() {
		spectrumWorld = new SpectrumWorld();
		
		float widthFactor = spectrumWorld.getWidth() / Gdx.graphics.getWidth();
		float heightFactor = spectrumWorld.getHeight() / Gdx.graphics.getHeight();
		if(widthFactor > heightFactor) 
			camera = new OrthographicCamera(Gdx.graphics.getWidth() * heightFactor, spectrumWorld.getHeight());
		else
			camera = new OrthographicCamera(spectrumWorld.getWidth(), Gdx.graphics.getHeight() * widthFactor);
		camera.update();
		
		RayHandler.setGammaCorrection(true);
		RayHandler.useDiffuseLight(true);
		
		rayHandler = new RayHandler(spectrumWorld.getBox2dWorld());
		rayHandler.setAmbientLight(0f, 0f, 0f, 0.5f);
		rayHandler.setBlurNum(3);
		
		spectrumWorld.create(rayHandler);

//		initPointLights();
		
		renderManager = new RenderManager(renderContext, assetManager);
		renderManager.loadWorld(spectrumWorld);
		spectrumWorld.addListener(renderManager);
		
		// Create the background
//		TextureParameter param = new TextureParameter();
//		param.minFilter = TextureFilter.Linear;
//		param.magFilter = TextureFilter.Linear;
//		assetManager.load("data/space_background2.jpg", Texture.class, param);
//		assetManager.finishLoading();
//		Sprite background = new Sprite(assetManager.get("data/space_background2.jpg", Texture.class));
//		background.setSize(spectrumWorld.getWidth() * 2f, spectrumWorld.getHeight() * 2f);
//		background.setPosition(-background.getWidth() / 2f, -background.getHeight() / 2f);
//		SpriteRenderable backgroundRenderable = new SpriteRenderable(background);
//		renderManager.setBackground(backgroundRenderable);
		
		TextureParameter param = new TextureParameter();
		param.minFilter = TextureFilter.Linear;
		param.magFilter = TextureFilter.Linear;
		param.wrapU = TextureWrap.Repeat;
		param.wrapV = TextureWrap.Repeat;
		assetManager.load("env/grid.png", Texture.class, param);
		assetManager.finishLoading();
		this.deformablePlane = new DeformablePlane(assetManager.get("env/grid.png", Texture.class), 
											-spectrumWorld.getWidth() / 2f, -spectrumWorld.getHeight() / 2f, 
											spectrumWorld.getWidth(), spectrumWorld.getHeight());
		
		Timer.schedule(new Task() {

			@Override
			public void run() {
				spectrumWorld.createRandomEnemy();
			}
			
		}, 2, 2);
	}
	
	@Override
	public void resize(int width, int height) {
		float widthFactor = spectrumWorld.getWidth() / Gdx.graphics.getWidth();
		float heightFactor = spectrumWorld.getHeight() / Gdx.graphics.getHeight();
		if(widthFactor > heightFactor)  {
			camera.viewportWidth = Gdx.graphics.getWidth() * heightFactor;
			camera.viewportHeight = spectrumWorld.getHeight();
		} else {
			camera.viewportWidth = spectrumWorld.getWidth();
			camera.viewportHeight = Gdx.graphics.getHeight() * widthFactor;
		}
		super.resize(width, height);
	}
	
	void clearLights() {
		if (lights.size() > 0) {
			for (Light light : lights) {
				light.remove();
				light.dispose();
			}
			lights.clear();
		}
//		groundBody.setActive(true);	// TODO does this need to be here?????
	}
	
	void initPointLights() {
		clearLights();
		for (int i = 0; i < RANDOM_LIGHTS; i++) {
			PointLight light = new PointLight(
					rayHandler, RAYS_PER_BALL, null, LIGHT_DISTANCE, 0f, 0f);
			light.setPosition(spectrumWorld.getWidth() * (i / (float)(RANDOM_LIGHTS-1)) - spectrumWorld.getWidth() / 2f, 0);
//			light.attachToBody(balls.get(i), RADIUS / 2f, RADIUS / 2f);
//			light.setColor(
//					MathUtils.random(),
//					MathUtils.random(),
//					MathUtils.random(),
//					1f);
			light.setColor(1, 1, 1, 1f);
			lights.add(light);
		}
	}
	
	@Override
	public void render(float delta) {
		renderContext.setCamera(camera);
		
		super.render(delta);
		
		int stepCount = spectrumWorld.update(camera, delta);
		renderManager.update(delta);
		
		Entity playerEntity = spectrumWorld.getPlayerEntity();
		if(playerEntity != null) {
			Vector2 pos = playerEntity.getBody().getPosition();
			camera.position.x = pos.x;
			camera.position.y = pos.y;
		}
		camera.update();
		
		ShapeRenderer sr = renderContext.getShapeRenderer();
		sr.setProjectionMatrix(camera.combined);
		batch.setProjectionMatrix(camera.combined);
		
		deformablePlane.update(delta);
		deformablePlane.render(renderContext);
		
		renderManager.renderBeforeFog();
		
		/** BOX2D LIGHT STUFF BEGIN */
		rayHandler.setCombinedMatrix(camera);

		if (stepCount > 0) rayHandler.update();
		rayHandler.render();
		/** BOX2D LIGHT STUFF END */
		
		renderManager.renderAfterFog();
		deformablePlane.renderAboveFog(renderContext);
		
		// Draw the out of bounds
		sr.begin(ShapeType.Line);
		sr.rect(-spectrumWorld.getWidth() / 2f, -spectrumWorld.getHeight() / 2f, 
							spectrumWorld.getWidth(), spectrumWorld.getHeight());
		sr.end();
	}
}
