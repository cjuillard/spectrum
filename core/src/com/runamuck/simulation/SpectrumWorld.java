package com.runamuck.simulation;

import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.runamuck.ai.EnemyFollowAIState;
import com.runamuck.ai.RandomMoveAIState;
import com.runamuck.data.EntityDefinition;
import com.runamuck.data.EntityDefinitions;
import com.runamuck.simulation.actions.ActionPool;

public class SpectrumWorld {
	private static final int PLAYER_FORCE = 4000;
	public static final int RAYS_PER_BALL = 128;
	public static final float LIGHT_DISTANCE = 20f;
	public static final float RADIUS = 1f;
	
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
	
	private ShipEntity playerEntity;
	private Array<Entity> entities = new Array<Entity>();
	private float physicsTimeLeft;
	
	private Array<ISpectrumWorldListener> listeners = new Array<ISpectrumWorldListener>();
	
	private float width;
	private float height;
	private RayHandler rayHandler;
	
	public SpectrumWorld() {
		this.width = 72;
		this.height = 48;
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
		box2DWorld.setContactListener(getContactListener());
		chainBodyDef.type = BodyType.StaticBody;
		groundBody = box2DWorld.createBody(chainBodyDef);
		groundBody.createFixture(chainShape, 0);
		chainShape.dispose();
		
//		PointLight light = new PointLight(
//				rayHandler, 4, null, width*2f, 0f, 0f);
//		light.setColor(
//				1f,
//				1,
//				1,
//				1f);
		
//		ChainLight light = new ChainLight(
//				rayHandler, RAYS_PER_BALL, null, LIGHT_DISTANCE*3, 1,
//				new float[]{-width/2f, -height/2, -width/2f, height/2f});
//		light.setColor(1f, 1, 1, 1f);
		
		rayHandler.setAmbientLight(.05f, .05f, .05f, 0);
	}
	
	private ContactListener getContactListener() {
		return new ContactListener() {
			
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
			}
			
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
			}
			
			@Override
			public void endContact(Contact contact) {
			}
			
			@Override
			public void beginContact(Contact contact) {
				Body a = contact.getFixtureA().getBody();
				Body b = contact.getFixtureA().getBody();
				
				Object aData = a.getUserData();
				Object bData = b.getUserData();
				
				if(aData instanceof Entity && bData instanceof Entity) {
					final Entity aEntity = (Entity)aData;
					final Entity bEntity = (Entity)bData;
					
					if(aEntity.getType() == EntityType.PLAYER) {
						Gdx.app.postRunnable(new Runnable() {

							@Override
							public void run() {
//								aEntity.setHp(0);
							}
							
						});
					} else if(bEntity.getType() == EntityType.PLAYER) {
						Gdx.app.postRunnable(new Runnable() {

							@Override
							public void run() {
//								bEntity.setHp(0);
							}
							
						});
					}
				}
			}
		};
	}

	private void createPlayer(RayHandler rayHandler) {
		// Create player
		EntityDefinition def = EntityDefinitions.get(EntityType.PLAYER);
		playerEntity = new ShipEntity(this, def.createBody(box2DWorld), EntityType.PLAYER);
		playerEntity.addAction(ActionPool.createBusyAction(playerEntity));
		
//		playerEntity.setWeapon(new RayWeapon(this, rayHandler, def, playerEntity.getBody()));
		playerEntity.setWeapon(new PulseWeapon(this, rayHandler, def, playerEntity.getBody()));
		
		Light light = new PointLight(rayHandler, RAYS_PER_BALL, new Color(1,1,1,.1f), 5, 0, 0);
		light.attachToBody(playerEntity.getBody());
		
		addEntity(playerEntity);
	}
	
	public void addEntity(Entity entity) {
		entities.add(entity);
		
		for(int i = 0; i < listeners.size; i++) {
			listeners.get(i).entityAdded(entity);
		}
	}
	
	public void removeEntity(Entity entity) {
		boolean removed = entities.removeValue(entity, true);
		
		if(removed) {
			if(entity == playerEntity) {
				playerEntity = null;
			}
			
			entity.dispose();
			for(int i = 0; i < listeners.size; i++) {
				listeners.get(i).entityRemoved(entity);
			}
		}
	}

	public void create(RayHandler rayHandler) {
		this.rayHandler = rayHandler;
		createWorld();
		createPlayer(rayHandler);
		
		for(int i = 0; i < 5; i++) {
			createRandomEnemy();
		}
	}
	
	// Test code
	public void createRandomEnemy() {
		EntityType type = EntityType.values()[MathUtils.random(2) + 1];
		createEntity(type, 
					MathUtils.random(-width/2f, width/2f), 
					MathUtils.random(-height/2f, height/2f));
	}
	
	public void createEntity(EntityType type, float x, float y) {
		// Create player
		EntityDefinition def = EntityDefinitions.get(type);
		Entity entity = new Entity(this, def.createBody(box2DWorld), type);
		entity.setAI(def.getAI(entity));
		entity.getBody().setTransform(x, y, 0);
		
		if(MathUtils.randomBoolean()) {
			entity.setAI(new DefaultStateMachine<Entity>(entity, EnemyFollowAIState.WAITING));
		} else {
			entity.setAI(new DefaultStateMachine<Entity>(entity, RandomMoveAIState.MOVING));
		}
		addEntity(entity);
	}
	
	public int update(Camera cam, float delta) {
		int steps = fixedStep(cam, delta);
		return steps;
	}
	
	private Vector2 dir = new Vector2();
	private Vector3 tmpMousePos = new Vector3();
	private void handlePlayerMovement(Camera cam, float deltaTime) {
		if(playerEntity == null) return;
		
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
			dir.nor().scl(PLAYER_FORCE * deltaTime);
			
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

	public RayHandler getRayHandler() {
		return rayHandler;
	}
	
	public void addListener(ISpectrumWorldListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(ISpectrumWorldListener listener) {
		this.listeners.removeValue(listener, true);
	}

	public Entity getPlayerEntity() {
		return playerEntity;
	}
}
