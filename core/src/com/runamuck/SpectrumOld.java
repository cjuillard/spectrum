package com.runamuck;

import java.util.ArrayList;

import box2dLight.ChainLight;
import box2dLight.ConeLight;
import box2dLight.DirectionalLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;

public class SpectrumOld extends InputAdapter implements ApplicationListener {
	
	static final int RAYS_PER_BALL = 128;
	static final int BALLSNUM = 5;
	static final float LIGHT_DISTANCE = 16f;
	static final float RADIUS = 1f;
	
	static final float viewportWidth = 48;
	static final float viewportHeight = 32;
	
	OrthographicCamera camera;

	SpriteBatch batch;
	BitmapFont font;
	TextureRegion textureRegion;
	TextureRegion textureRegion2;
	Texture bg;

	/** our box2D world **/
	World world;

	/** our boxes **/
	ArrayList<Body> balls = new ArrayList<Body>(BALLSNUM);
	ArrayList<Body> boxes = new ArrayList<Body>();
	
	/** our ground box **/
	Body groundBody;

	/** our mouse joint **/
	MouseJoint mouseJoint = null;

	/** a hit body **/
	Body hitBody = null;

	/** pixel perfect projection for font rendering */
	Matrix4 normalProjection = new Matrix4();
	
	boolean showText = true;
	
	/** BOX2D LIGHT STUFF */
	RayHandler rayHandler;
	
	ArrayList<Light> lights = new ArrayList<Light>(BALLSNUM);
	
	float sunDirection = -90f;
	
	GlowTest glowTest;
	
	@Override
	public void create() {
		
		MathUtils.random.setSeed(Long.MIN_VALUE);

		camera = new OrthographicCamera(viewportWidth, viewportHeight);
		camera.position.set(0, viewportHeight / 2f, 0);
		camera.update();
		
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.RED);
		
		textureRegion = new TextureRegion(new Texture(
				Gdx.files.internal("data/marble.png")));
		textureRegion2 = new TextureRegion(new Texture(
				Gdx.files.internal("data/box.png")));
		bg = new Texture(Gdx.files.internal("data/bg.png"));

		createPhysicsWorld();
		Gdx.input.setInputProcessor(this);

		normalProjection.setToOrtho2D(
				0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		/** BOX2D LIGHT STUFF BEGIN */
		RayHandler.setGammaCorrection(true);
		RayHandler.useDiffuseLight(true);
		
		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(0f, 0f, 0f, 0.5f);
		rayHandler.setBlurNum(3);

		initPointLights();
		/** BOX2D LIGHT STUFF END */
		
		glowTest = new GlowTest(Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 4);
	}

	@Override
	public void render() {
		handleInput(Gdx.graphics.getDeltaTime());
		
		/** Rotate directional light like sun :) */
		if (lightsType == 3) {
			sunDirection += Gdx.graphics.getDeltaTime() * 4f;
			lights.get(0).setDirection(sunDirection);
		}

		camera.update();

		boolean stepped = fixedStep(Gdx.graphics.getDeltaTime());
		Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(camera.combined);
		
		// Draw the background
		batch.begin();
		{
			batch.disableBlending();
			batch.draw(bg, -viewportWidth / 2f, 0, viewportWidth, viewportHeight);
		}
		batch.end();
		batch.enableBlending();
		
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		// Draw the balls
		batch.begin();
		{
			batch.enableBlending();
			for (int i = 0; i < BALLSNUM; i++) {
				Body ball = balls.get(i);
				Vector2 position = ball.getPosition();
				float angle = MathUtils.radiansToDegrees * ball.getAngle();
				batch.draw(
						textureRegion,
						position.x - RADIUS, position.y - RADIUS,
						RADIUS, RADIUS,
						RADIUS * 2, RADIUS * 2,
						1f, 1f,
						angle);
			}
			
			for(int i = 0; i < boxes.size(); i++) {
				Body box = boxes.get(i);
				Vector2 position = box.getPosition();
				float angle = MathUtils.radiansToDegrees * box.getAngle();
				batch.draw(
						textureRegion2,
						position.x - BOX_SIZE_H, position.y - BOX_SIZE_H,
						BOX_SIZE_H, BOX_SIZE_H,
						BOX_SIZE_H * 2, BOX_SIZE_H * 2,
						1f, 1f,
						angle);
			}
		}
		batch.end();
		
		// Draw the glow
		glowTest.frameBuffer.begin();
		Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		{
			batch.enableBlending();
			batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
			
			for (int i = 0; i < BALLSNUM; i++) {
				Body ball = balls.get(i);
				Vector2 position = ball.getPosition();
				float angle = MathUtils.radiansToDegrees * ball.getAngle();
				
				if(i == 0) {
					float a = (System.currentTimeMillis() % 2000) / 2000f;
					if(a <= .5f) {
						a = MathUtils.lerp(.5f, 1f, 2 * a);
					} else {
						a = MathUtils.lerp(1, .5f, 2 * (a - .5f));
					}
					batch.setColor(a, 0, 0, 1f);
				} else {
					batch.setColor(1, 1, 1, 1f);
				}
//				
				
				batch.draw(
						textureRegion,
						position.x - RADIUS, position.y - RADIUS,
						RADIUS, RADIUS,
						RADIUS * 2, RADIUS * 2,
						1f, 1f,
						angle);
			}
			
			for(int i = 0; i < boxes.size(); i++) {
				Body ball = boxes.get(i);
				Vector2 position = ball.getPosition();
				float angle = MathUtils.radiansToDegrees * ball.getAngle();
				batch.setColor(0, 1, 0, 1f);
				batch.draw(
						textureRegion2,
						position.x - BOX_SIZE_H, position.y - BOX_SIZE_H,
						BOX_SIZE_H, BOX_SIZE_H,
						BOX_SIZE_H * 2, BOX_SIZE_H * 2,
						1f, 1f,
						angle);
			}
		}
		batch.end();
		batch.setShader(null);
		batch.setColor(1,1,1,1);
		glowTest.frameBuffer.end();
		
		glowTest.render();
		
		/** BOX2D LIGHT STUFF BEGIN */
		rayHandler.setCombinedMatrix(camera);

		if (stepped) rayHandler.update();
		rayHandler.render();
		/** BOX2D LIGHT STUFF END */

//		long time = System.nanoTime();
		
		boolean atShadow = rayHandler.pointAtShadow(testPoint.x,
				testPoint.y);
//		aika += System.nanoTime() - time;
      
		/** FONT */
		if (showText) {
			batch.setProjectionMatrix(normalProjection);
			batch.begin();
			
			font.draw(batch,
					"F1 - PointLight",
					0, Gdx.graphics.getHeight());
			font.draw(batch,
					"F2 - ConeLight",
					0, Gdx.graphics.getHeight() - 15);
			font.draw(batch,
					"F3 - ChainLight",
					0, Gdx.graphics.getHeight() - 30);
			font.draw(batch,
					"F4 - DirectionalLight",
					0, Gdx.graphics.getHeight() - 45);
			font.draw(batch,
					"F5 - random lights colors",
					0, Gdx.graphics.getHeight() - 75);
			font.draw(batch,
					"F6 - random lights distance",
					0, Gdx.graphics.getHeight() - 90);
			font.draw(batch,
					"F9 - default blending (1.3)",
					0, Gdx.graphics.getHeight() - 120);
			font.draw(batch,
					"F10 - over-burn blending (default in 1.2)",
					0, Gdx.graphics.getHeight() - 135);
			font.draw(batch,
					"F11 - some other blending",
					0, Gdx.graphics.getHeight() - 150);
			
			font.draw(batch,
					"F12 - toggle help text",
					0, Gdx.graphics.getHeight() - 180);
	
			font.draw(batch,
					Integer.toString(Gdx.graphics.getFramesPerSecond())
					+ "mouse at shadows: " + atShadow
					+ " time used for shadow calculation:"
					+ aika / ++times + "ns" , 0, 20);
	
			batch.end();
		}
	}
	
	public static ShaderProgram createBlurShader(int width, int heigth) {
		final String FBO_W = Integer.toString(width);
		final String FBO_H = Integer.toString(heigth);
//		final String rgb = RayHandler.isDiffuse  ? ".rgb" : "";
		final String rgb = "";
		final String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
				+ "uniform vec2  dir;\n" //
				+ "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
				+ "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
				+ "uniform mat4 u_projTrans;\n" //
				+ "varying vec4 v_color;\n" //
				+ "varying vec2 v_texCoords0;\n" //
				+ "varying vec2 v_texCoords1;\n" //
				+ "varying vec2 v_texCoords2;\n" //
				+ "varying vec2 v_texCoords3;\n" //
				+ "varying vec2 v_texCoords4;\n" //
				+ "#define FBO_W "
				+ FBO_W
				+ ".0\n"//
				+ "#define FBO_H "
				+ FBO_H
				+ ".0\n"//
				+ "const vec2 futher = vec2(3.2307692308 / FBO_W, 3.2307692308 / FBO_H );\n" //
				+ "const vec2 closer = vec2(1.3846153846 / FBO_W, 1.3846153846 / FBO_H );\n" //
				+ "void main()\n" //
				+ "{\n" //
				+ "vec2 f = futher * dir;\n" //
				+ "vec2 c = closer * dir;\n" //
				+ "v_texCoords0 = a_texCoord0 - f;\n" //
				+ "v_texCoords1 = a_texCoord0 - c;\n" //
				+ "v_texCoords2 = a_texCoord0;\n" //
				+ "v_texCoords3 = a_texCoord0 + c;\n" //
				+ "v_texCoords4 = a_texCoord0 + f;\n" //
				+ "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
				+ "   v_color.a = v_color.a * (255.0/254.0);\n" //
				+ "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
				+ "}\n";
		final String fragmentShader = "#ifdef GL_ES\n" //
				+ "precision lowp float;\n" //
				+ "#define LOWP lowp\n" //
				+ "#define MED mediump\n"
				+ "#else\n"
				+ "#define LOWP \n" //
				+ "#define MED \n"
				+ "#endif\n" //
				+ "uniform sampler2D u_texture;\n" //
				+ "varying LOWP vec4 v_color;\n" //
				+ "varying MED vec2 v_texCoords0;\n" //
				+ "varying MED vec2 v_texCoords1;\n" //
				+ "varying MED vec2 v_texCoords2;\n" //
				+ "varying MED vec2 v_texCoords3;\n" //
				+ "varying MED vec2 v_texCoords4;\n" //
				+ "const float center = 0.2270270270;\n" //
				+ "const float close  = 0.3162162162;\n" //
				+ "const float far    = 0.0702702703;\n" //
				+ "void main()\n" //
				+ "{	 \n" //
				+ "gl_FragColor"+rgb+" = (far    * texture2D(u_texture, v_texCoords0)"+rgb+"\n" //
				+ "	      		+ close  * texture2D(u_texture, v_texCoords1)"+rgb+"\n" //
				+ "				+ center * texture2D(u_texture, v_texCoords2)"+rgb+"\n" //
				+ "				+ close  * texture2D(u_texture, v_texCoords3)"+rgb+"\n" //
				+ "				+ far    * texture2D(u_texture, v_texCoords4)"+rgb+")\n"//
				+ "				* v_color" +rgb+";\n"	//
				+ "}\n";
		ShaderProgram.pedantic = false;
		ShaderProgram blurShader = new ShaderProgram(vertexShader,
				fragmentShader);
		if (blurShader.isCompiled() == false) {
			Gdx.app.log("ERROR", blurShader.getLog());
		}

		return blurShader;
	}
	
	private Vector2 dir = new Vector2();
	private void handleInput(float deltaTime) {
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
		
		if(dir.x != 0 || dir.y != 0) {
			dir.nor().scl(2000 * deltaTime);
			
			balls.get(0).applyForceToCenter(dir, true);
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
		for (int i = 0; i < BALLSNUM; i++) {
			PointLight light = new PointLight(
					rayHandler, RAYS_PER_BALL, null, LIGHT_DISTANCE, 0f, 0f);
			light.attachToBody(balls.get(i), RADIUS / 2f, RADIUS / 2f);
			light.setColor(
					MathUtils.random(),
					MathUtils.random(),
					MathUtils.random(),
					1f);
			lights.add(light);
		}
	}
	
	void initConeLights() {
		clearLights();
		for (int i = 0; i < BALLSNUM; i++) {
			ConeLight light = new ConeLight(
					rayHandler, RAYS_PER_BALL, null, LIGHT_DISTANCE,
					0, 0, 0f, MathUtils.random(15f, 40f));
			light.attachToBody(
					balls.get(i),
					RADIUS / 2f, RADIUS / 2f, MathUtils.random(0f, 360f));
			light.setColor(
					MathUtils.random(),
					MathUtils.random(),
					MathUtils.random(),
					1f);
			lights.add(light);
		}
	}
	
	void initChainLights() {
		clearLights();
		for (int i = 0; i < BALLSNUM; i++) {
			ChainLight light = new ChainLight(
					rayHandler, RAYS_PER_BALL, null, LIGHT_DISTANCE, 1,
					new float[]{-5, 0, 0, 3, 5, 0});
			// Beam-like
//			ChainLight light = new ChainLight(
//					rayHandler, RAYS_PER_BALL, null, LIGHT_DISTANCE*3, 1,
//					new float[]{-1, 0, 0, 0, 1, 0});
			light.attachToBody(
					balls.get(i),
					MathUtils.random(0f, 360f));
			light.setColor(
					MathUtils.random(),
					MathUtils.random(),
					MathUtils.random(),
					1f);
			lights.add(light);
		}
	}
	
	void initDirectionalLight() {
		clearLights();
		
		groundBody.setActive(false);
		sunDirection = MathUtils.random(0f, 360f);
		
		DirectionalLight light = new DirectionalLight(
				rayHandler, 4 * RAYS_PER_BALL, null, sunDirection);
		lights.add(light);
	}
	
	private final static int MAX_FPS = 30;
	private final static int MIN_FPS = 15;
	public final static float TIME_STEP = 1f / MAX_FPS;
	private final static float MAX_STEPS = 1f + MAX_FPS / MIN_FPS;
	private final static float MAX_TIME_PER_FRAME = TIME_STEP * MAX_STEPS;
	private final static int VELOCITY_ITERS = 6;
	private final static int POSITION_ITERS = 2;
	private static final float BOX_SIZE_H = 2;

	float physicsTimeLeft;
	long aika;
	int times;

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

	private void createPhysicsWorld() {

		world = new World(new Vector2(0, 0), true);
		
		float halfWidth = viewportWidth / 2f;
		ChainShape chainShape = new ChainShape();
		chainShape.createLoop(new Vector2[] {
				new Vector2(-halfWidth, 0f),
				new Vector2(halfWidth, 0f),
				new Vector2(halfWidth, viewportHeight),
				new Vector2(-halfWidth, viewportHeight) });
		BodyDef chainBodyDef = new BodyDef();
		chainBodyDef.type = BodyType.StaticBody;
		groundBody = world.createBody(chainBodyDef);
		groundBody.createFixture(chainShape, 0);
		chainShape.dispose();
		createBoxes();
		createBoxes2();
	}

	private void createBoxes() {
		CircleShape ballShape = new CircleShape();
		ballShape.setRadius(RADIUS);

		FixtureDef def = new FixtureDef();
		def.restitution = 0.9f;
		def.friction = .1f;
		def.shape = ballShape;
		def.density = 1f;
		BodyDef boxBodyDef = new BodyDef();
		boxBodyDef.type = BodyType.DynamicBody;
		boxBodyDef.linearDamping = .9f;
		boxBodyDef.angularDamping = .25f;

		for (int i = 0; i < BALLSNUM; i++) {
			// Create the BodyDef, set a random position above the
			// ground and create a new body
			boxBodyDef.position.x = -20 + (float) (Math.random() * 40);
			boxBodyDef.position.y = 10 + (float) (Math.random() * 15);
			Body boxBody = world.createBody(boxBodyDef);
			boxBody.createFixture(def);
			balls.add(boxBody);
		}
		ballShape.dispose();
	}
	
	private void createBoxes2() {
		PolygonShape boxShape = new PolygonShape();
		boxShape.setAsBox(BOX_SIZE_H, BOX_SIZE_H);
		
		FixtureDef boxDef = new FixtureDef();
		boxDef.restitution = 0.9f;
		boxDef.friction = .1f;
		boxDef.shape = boxShape;
		boxDef.density = 1f;
		BodyDef boxBodyDef = new BodyDef();
		boxBodyDef.type = BodyType.StaticBody;

		for (int i = 0; i < BALLSNUM; i++) {
			// Create the BodyDef, set a random position above the
			// ground and create a new body
			boxBodyDef.position.x = -20 + (float) (Math.random() * 40);
			boxBodyDef.position.y = 10 + (float) (Math.random() * 15);
			Body boxBody = world.createBody(boxBodyDef);
			boxBody.createFixture(boxDef);
			boxes.add(boxBody);
		}
		boxShape.dispose();
	}

	/**
	 * we instantiate this vector and the callback here so we don't irritate the
	 * GC
	 **/
	Vector3 testPoint = new Vector3();
	QueryCallback callback = new QueryCallback() {
		@Override
		public boolean reportFixture(Fixture fixture) {
			if (fixture.getBody() == groundBody)
				return true;

			if (fixture.testPoint(testPoint.x, testPoint.y)) {
				hitBody = fixture.getBody();
				return false;
			} else
				return true;
		}
	};

	@Override
	public boolean touchDown(int x, int y, int pointer, int newParam) {
		// translate the mouse coordinates to world coordinates
		testPoint.set(x, y, 0);
		camera.unproject(testPoint);

		// ask the world which bodies are within the given
		// bounding box around the mouse pointer
		hitBody = null;
		world.QueryAABB(callback, testPoint.x - 0.1f, testPoint.y - 0.1f,
				testPoint.x + 0.1f, testPoint.y + 0.1f);

		// if we hit something we create a new mouse joint
		// and attach it to the hit body.
		if (hitBody != null) {
			MouseJointDef def = new MouseJointDef();
			def.bodyA = groundBody;
			def.bodyB = hitBody;
			def.collideConnected = true;
			def.target.set(testPoint.x, testPoint.y);
			def.maxForce = 1000.0f * hitBody.getMass();

			mouseJoint = (MouseJoint) world.createJoint(def);
			hitBody.setAwake(true);
		}

		return false;
	}

	/** another temporary vector **/
	Vector2 target = new Vector2();

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
    camera.unproject(testPoint.set(x, y, 0));
    target.set(testPoint.x, testPoint.y);
		// if a mouse joint exists we simply update
		// the target of the joint based on the new
		// mouse coordinates
		if (mouseJoint != null) {
			mouseJoint.setTarget(target);
		}
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		// if a mouse joint exists we simply destroy it
		if (mouseJoint != null) {
			world.destroyJoint(mouseJoint);
			mouseJoint = null;
		}
		return false;
	}

	@Override
	public void dispose() {
		rayHandler.dispose();
		world.dispose();
	}

	/**
	 * Type of lights to use:
	 * 0 - PointLight
	 * 1 - ConeLight
	 * 2 - ChainLight
	 * 3 - DirectionalLight
	 */
	int lightsType = 0;
	
	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
		
		case Input.Keys.F1:
			if (lightsType != 0) {
				initPointLights();
				lightsType = 0;
			}
			return true;
			
		case Input.Keys.F2:
			if (lightsType != 1) {
				initConeLights();
				lightsType = 1;
			}
			return true;
			
		case Input.Keys.F3:
			if (lightsType != 2) {
				initChainLights();
				lightsType = 2;
			}
			return true;
			
		case Input.Keys.F4:
			if (lightsType != 3) {
				initDirectionalLight();
				lightsType = 3;
			}
			return true;
			
		case Input.Keys.F5:
			for (Light light : lights)
				light.setColor(
						MathUtils.random(),
						MathUtils.random(),
						MathUtils.random(),
						1f);
			return true;
			
		case Input.Keys.F6:
			for (Light light : lights)
				light.setDistance(MathUtils.random(
						LIGHT_DISTANCE * 0.5f, LIGHT_DISTANCE * 2f));
			return true;
			
		case Input.Keys.F9:
			rayHandler.diffuseBlendFunc.reset();
			return true;
			
		case Input.Keys.F10:
			rayHandler.diffuseBlendFunc.set(
					GL20.GL_DST_COLOR, GL20.GL_SRC_COLOR);
			return true;
			
		case Input.Keys.F11:
			rayHandler.diffuseBlendFunc.set(
					GL20.GL_SRC_COLOR, GL20.GL_DST_COLOR);
			return true;
			
		case Input.Keys.F12:
			showText = !showText;
			return true;
			
		default:
			return false;
			
		}
	}

	@Override
	public boolean mouseMoved(int x, int y) {
		testPoint.set(x, y, 0);
		camera.unproject(testPoint);
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		camera.rotate((float) amount * 3f, 0, 0, 1);
		return false;
	}

	@Override
	public void pause() {
	}

	@Override
	public void resize(int arg0, int arg1) {
	}

	@Override
	public void resume() {
	}
	
}