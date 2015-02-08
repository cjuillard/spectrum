package com.runamuck.screens;

import java.util.ArrayList;

import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class GameplayScreen extends BaseScreen {
	private static final float WORLD_WIDTH = 48;
	private static final float WORLD_HEIGHT = 32;
	
	// TODO remove these once we have the main character light weapon
	private static final int RANDOM_LIGHTS = 4;	
	static final int RAYS_PER_BALL = 128;
	static final float LIGHT_DISTANCE = 3f;
	static final float RADIUS = 1f;
	
	// Physics simulation parameters
	private final static int MAX_FPS = 30;
	private final static int MIN_FPS = 15;
	public final static float TIME_STEP = 1f / MAX_FPS;
	private final static float MAX_STEPS = 1f + MAX_FPS / MIN_FPS;
	private final static float MAX_TIME_PER_FRAME = TIME_STEP * MAX_STEPS;
	private final static int VELOCITY_ITERS = 6;
	private final static int POSITION_ITERS = 2;
	
	private float physicsTimeLeft;
	
	private World world;
	private Body groundBody;
	
	private OrthographicCamera camera;
	private RayHandler rayHandler;
	
	private ArrayList<Light> lights = new ArrayList<Light>(RANDOM_LIGHTS);
	
	@Override
	public void show() {
		camera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT);
		camera.update();
		
		createPhysicsWorld();
		
		/** BOX2D LIGHT STUFF BEGIN */
		RayHandler.setGammaCorrection(true);
		RayHandler.useDiffuseLight(true);
		
		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(0f, 0f, 0f, 0.5f);
		rayHandler.setBlurNum(3);

		initPointLights();
		/** BOX2D LIGHT STUFF END */
	}
	
	void clearLights() {
		if (lights.size() > 0) {
			for (Light light : lights) {
				light.remove();
				light.dispose();
			}
			lights.clear();
		}
		groundBody.setActive(true);
	}
	
	void initPointLights() {
		clearLights();
		for (int i = 0; i < RANDOM_LIGHTS; i++) {
			PointLight light = new PointLight(
					rayHandler, RAYS_PER_BALL, null, LIGHT_DISTANCE, 0f, 0f);
			light.setPosition(WORLD_WIDTH * (i / (float)(RANDOM_LIGHTS-1)) - WORLD_WIDTH / 2f, 0);
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
	
	private void createPhysicsWorld() {

		world = new World(new Vector2(0, 0), true);
		
		float halfWidth = WORLD_WIDTH / 2f;
		ChainShape chainShape = new ChainShape();
		chainShape.createLoop(new Vector2[] {
				new Vector2(-halfWidth, 0f),
				new Vector2(halfWidth, 0f),
				new Vector2(halfWidth, WORLD_HEIGHT),
				new Vector2(-halfWidth, WORLD_HEIGHT) });
		BodyDef chainBodyDef = new BodyDef();
		chainBodyDef.type = BodyType.StaticBody;
		groundBody = world.createBody(chainBodyDef);
		groundBody.createFixture(chainShape, 0);
		chainShape.dispose();
//		createBoxes();
//		createBoxes2();
	}
	
	private boolean fixedStep(float delta) {
		physicsTimeLeft += delta;
		if (physicsTimeLeft > MAX_TIME_PER_FRAME)
			physicsTimeLeft = MAX_TIME_PER_FRAME;

		boolean stepped = false;
		while (physicsTimeLeft >= TIME_STEP) {
			world.step(TIME_STEP, VELOCITY_ITERS, POSITION_ITERS);
			physicsTimeLeft -= TIME_STEP;
			stepped = true;
		}
		return stepped;
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		
		boolean stepped = fixedStep(Gdx.graphics.getDeltaTime());
		
		/** BOX2D LIGHT STUFF BEGIN */
		rayHandler.setCombinedMatrix(camera);

		if (stepped) rayHandler.update();
		rayHandler.render();
		/** BOX2D LIGHT STUFF END */
	}
}
