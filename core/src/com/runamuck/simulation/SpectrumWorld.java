package com.runamuck.simulation;

import box2dLight.ChainLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.runamuck.data.EntityDefinition;

public class SpectrumWorld {
	static final int RAYS_PER_BALL = 128;
	static final float LIGHT_DISTANCE = 20f;
	static final float RADIUS = 1f;
	
	// Physics simulation parameters
	private final static int MAX_FPS = 30;
	private final static int MIN_FPS = 15;
	public final static float TIME_STEP = 1f / MAX_FPS;
	private final static float MAX_STEPS = 1f + MAX_FPS / MIN_FPS;
	private final static float MAX_TIME_PER_FRAME = TIME_STEP * MAX_STEPS;
	private final static int VELOCITY_ITERS = 6;
	private final static int POSITION_ITERS = 2;
	
	private World box2DWorld;
	private Body groundBody;
	
	private Entity playerEntity;
	private Array<Entity> entities = new Array<Entity>();
	private float physicsTimeLeft;
	
	private Array<ISpectrumWorldListener> listeners = new Array<ISpectrumWorldListener>();
	
	private float width;
	private float height;
	
	public SpectrumWorld() {
		this.width = 48;
		this.height = 32;
	}
	
	private Body createPlayerBody() {
		CircleShape ballShape = new CircleShape();
		ballShape.setRadius(EntityDefinition.getWidth(EntityType.PLAYER) / 2f);

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
		circleBodyDef.position.y = height / 4f;
		Body playerBody = box2DWorld.createBody(circleBodyDef);
		playerBody.createFixture(def);

		ballShape.dispose();
		
		return playerBody;
	}
	
	private void createWorld() {
		box2DWorld = new World(new Vector2(0, 0), true);
		
		float halfWidth = width / 2f;
		float halfHeight = height / 2f;
		ChainShape chainShape = new ChainShape();
		chainShape.createLoop(new Vector2[] {
				new Vector2(-halfWidth, -halfHeight),
				new Vector2(halfWidth, -halfHeight),
				new Vector2(halfWidth, halfHeight),
				new Vector2(-halfWidth, halfHeight) });
		BodyDef chainBodyDef = new BodyDef();
		chainBodyDef.type = BodyType.StaticBody;
		groundBody = box2DWorld.createBody(chainBodyDef);
		groundBody.createFixture(chainShape, 0);
		chainShape.dispose();
	}
	
	private void createPlayer(RayHandler rayHandler) {
		// Create player
		playerEntity = new Entity(createPlayerBody(), EntityType.PLAYER);
		float startPos = EntityDefinition.getHeight(EntityType.PLAYER) / 2f;
		ChainLight light = new ChainLight(
				rayHandler, RAYS_PER_BALL, null, LIGHT_DISTANCE*3, 1,
				new float[]{-1, -startPos, 0, -startPos, 1, -startPos});
		light.attachToBody(playerEntity.getBody());
//		PointLight light = new PointLight(
//				rayHandler, RAYS_PER_BALL, null, LIGHT_DISTANCE, 0f, 0f);
//		light.attachToBody(playerEntity.getBody(), RADIUS / 2f, RADIUS / 2f);
		light.setColor(
				1f,
				0,
				0,
				1f);
		playerEntity.setWeapon(light);
		
		addEntity(playerEntity);
	}
	
	public void addEntity(Entity entity) {
		entities.add(playerEntity);
		
		for(int i = 0; i < listeners.size; i++) {
			listeners.get(i).entityAdded(entity);
		}
	}
	
	public void removeEntity(Entity entity) {
		boolean removed = entities.removeValue(playerEntity, true);
		
		if(removed) {
			box2DWorld.destroyBody(entity.getBody());
			for(int i = 0; i < listeners.size; i++) {
				listeners.get(i).entityRemoved(entity);
			}
		}
	}

	public void create(RayHandler rayHandler) {
		createWorld();
		createPlayer(rayHandler);
	}
	
	public int update(Camera cam, float delta) {
		int steps = fixedStep(cam, delta);
		return steps;
	}
	
	private Vector2 dir = new Vector2();
	private Vector3 tmpMousePos = new Vector3();
	private void handlePlayerMovement(Camera cam, float deltaTime) {
		dir.setZero();
		if(Gdx.input.isKeyPressed(Keys.W)) {
			dir.y++;
		}
		if(Gdx.input.isKeyPressed(Keys.S)) {
			dir.y--;
		}
		if(Gdx.input.isKeyPressed(Keys.A)) {
			dir.x--;
		}
		if(Gdx.input.isKeyPressed(Keys.D)) {
			dir.x++;
		}
		
		Body body = playerEntity.getBody();
		if(dir.x != 0 || dir.y != 0) {
			dir.nor().scl(2000 * deltaTime);
			
			playerEntity.getBody().applyForceToCenter(dir, true);
		}
		
		// Set players rotation
		tmpMousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		cam.unproject(tmpMousePos);
		
		dir.set(tmpMousePos.x, tmpMousePos.y).sub(body.getPosition().x, body.getPosition().y);
		body.setTransform(body.getPosition().x, body.getPosition().y, (dir.angle()-90) * MathUtils.degreesToRadians);
	}
	
	private int fixedStep(Camera cam, float delta) {
		physicsTimeLeft += delta;
		if (physicsTimeLeft > MAX_TIME_PER_FRAME)
			physicsTimeLeft = MAX_TIME_PER_FRAME;

		int stepCount = 0;
		while (physicsTimeLeft >= TIME_STEP) {
			handlePlayerMovement(cam, TIME_STEP);
			box2DWorld.step(TIME_STEP, VELOCITY_ITERS, POSITION_ITERS);
			updateEntities(TIME_STEP);
			physicsTimeLeft -= TIME_STEP;
			stepCount++;
		}
		return stepCount;
	}
	
	private void updateEntities(float timeStep) {
		for(int i = 0; i < entities.size; i++) {
			entities.get(i).update(timeStep);
		}
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public World getBox2dWorld() {
		return box2DWorld;
	}

	public Array<Entity> getEntities() {
		return entities;
	}
	
	// Environment?
	// Box2D world
	// Your entity
	// Enemies
	
	// update() logic
	// add/remove()
	
	// renderables - light + sprites/geometric renderings
}
