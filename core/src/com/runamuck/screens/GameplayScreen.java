package com.runamuck.screens;

import java.util.ArrayList;

import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.runamuck.rendering.RenderManager;
import com.runamuck.rendering.SpriteRenderable;
import com.runamuck.simulation.Entity;

public class GameplayScreen extends BaseScreen {
	private static final float WORLD_WIDTH = 48;
	private static final float WORLD_HEIGHT = 32;
	
	// TODO remove these once we have the main character light weapon
	private static final int RANDOM_LIGHTS = 4;	
	static final int RAYS_PER_BALL = 128;
	static final float LIGHT_DISTANCE = 10f;
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
	
	private Entity playerEntity;
	private Array<Entity> entities = new Array<Entity>();
	
	private RenderManager renderManager;
	
	@Override
	public void show() {
		camera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT);
		camera.update();
		
		RayHandler.setGammaCorrection(true);
		RayHandler.useDiffuseLight(true);
		
		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(0f, 0f, 0f, 0.5f);
		rayHandler.setBlurNum(3);
		
		createWorld();

		initPointLights();
		
		renderManager = new RenderManager(renderContext);
		
		TextureRegion textureRegion = new TextureRegion(new Texture(
				Gdx.files.internal("data/marble.png")));
		for(Entity entity : entities) {
			SpriteRenderable renderable = new SpriteRenderable(entity, new Sprite(textureRegion), null);
			renderManager.addRenderable(renderable);
		}
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

	
	private Body createPlayerBody() {
		CircleShape ballShape = new CircleShape();
		ballShape.setRadius(RADIUS);

		FixtureDef def = new FixtureDef();
		def.restitution = 0.9f;
		def.friction = .1f;
		def.shape = ballShape;
		def.density = 1f;
		BodyDef circleBodyDef = new BodyDef();
		circleBodyDef.type = BodyType.DynamicBody;
		circleBodyDef.linearDamping = .9f;
		circleBodyDef.angularDamping = .25f;

		// Create the BodyDef, set a random position above the
		// ground and create a new body
		circleBodyDef.position.x = 0;
		circleBodyDef.position.y = WORLD_HEIGHT / 4f;
		Body playerBody = world.createBody(circleBodyDef);
		playerBody.createFixture(def);

		ballShape.dispose();
		
		return playerBody;
	}
	
	private void createWorld() {
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
		
		// Create player
		playerEntity = new Entity(createPlayerBody());
		PointLight light = new PointLight(
				rayHandler, RAYS_PER_BALL, null, LIGHT_DISTANCE, 0f, 0f);
		light.attachToBody(playerEntity.getBody(), RADIUS / 2f, RADIUS / 2f);
		light.setColor(
				1f,
				0,
				0,
				1f);
		playerEntity.setWeapon(light);
		
		entities.add(playerEntity);
	}
	
	private int fixedStep(float delta) {
		physicsTimeLeft += delta;
		if (physicsTimeLeft > MAX_TIME_PER_FRAME)
			physicsTimeLeft = MAX_TIME_PER_FRAME;

		int stepCount = 0;
		while (physicsTimeLeft >= TIME_STEP) {
			world.step(TIME_STEP, VELOCITY_ITERS, POSITION_ITERS);
			physicsTimeLeft -= TIME_STEP;
			stepCount++;
		}
		return stepCount;
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		
		int stepCount = fixedStep(delta);
		
		for(Entity entity : entities) {
			entity.update(delta);
		}
		
		renderManager.renderBeforeFog();
		
		/** BOX2D LIGHT STUFF BEGIN */
		rayHandler.setCombinedMatrix(camera);

		if (stepCount > 0) rayHandler.update();
		rayHandler.render();
		/** BOX2D LIGHT STUFF END */
	}
}
