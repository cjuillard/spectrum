package com.runamuck.screens;

import java.util.ArrayList;

import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.runamuck.rendering.RenderManager;
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
	
	@Override
	public void show() {
		spectrumWorld = new SpectrumWorld();
		
		camera = new OrthographicCamera(spectrumWorld.getWidth(), spectrumWorld.getHeight());
		camera.update();
		
		RayHandler.setGammaCorrection(true);
		RayHandler.useDiffuseLight(true);
		
		rayHandler = new RayHandler(spectrumWorld.getBox2dWorld());
		rayHandler.setAmbientLight(0f, 0f, 0f, 0.5f);
		rayHandler.setBlurNum(3);
		
		spectrumWorld.create(rayHandler);

//		initPointLights();
		
		renderManager = new RenderManager(renderContext);
		renderManager.loadWorld(spectrumWorld);
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
		super.render(delta);
		
		camera.update();
		
		batch.setProjectionMatrix(camera.combined);
		
		int stepCount = spectrumWorld.update(camera, delta);
		renderManager.update(delta);
		
		renderManager.renderBeforeFog();
		
		/** BOX2D LIGHT STUFF BEGIN */
		rayHandler.setCombinedMatrix(camera);

		if (stepCount > 0) rayHandler.update();
		rayHandler.render();
		/** BOX2D LIGHT STUFF END */
		
		renderManager.renderAfterFog();
	}
}
