package com.runamuck.simulation;

import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.runamuck.data.EntityDefinition;
import com.runamuck.data.EntityDefinitions;

public class PulseWeapon extends Weapon {
	protected Array<PulseData> bullets = new Array<PulseData>();
	private float pulseRadius = 4f;
	private float pulseDelay = .15f;
	private float pulseSpeed = 50f;
	public static final int RAYS_PER_BALL = 128;
	
	private float timeSinceLastPulse = 0;
	private RayHandler rayHandler;
	private Body body;
	
	private class PulseData {
		public final Light light;
		public Vector2 vel = new Vector2();

		public PulseData(Light light, float angle, float speed) {
			this.light = light;
			vel.set(1,0);
			vel.setAngle(angle);
			vel.scl(speed);
		}
	}
	
	public PulseWeapon(SpectrumWorld spectrumWorld, RayHandler rayHandler, EntityDefinition def, Body body) {
		super(spectrumWorld);
		
		this.rayHandler = rayHandler;
		this.body = body;
		
		this.damage = 10000f;	// default damage
	}

	@Override
	public void update(float delta) {
		timeSinceLastPulse -= delta;
		if(timeSinceLastPulse <= 0) {
			timeSinceLastPulse += pulseDelay;
			fire();
		}
		
		for(int i = bullets.size - 1; i >= 0; i--) {
			PulseData data = bullets.get(i);
			
			
			doDamage(data.light, delta);
			data.light.setPosition(data.light.getX() + data.vel.x * delta, data.light.getY() + data.vel.y * delta);
			if(data.light.getX() < -world.getWidth() / 2f || data.light.getX() > world.getWidth() / 2f ||
				data.light.getY() < -world.getHeight() / 2f || data.light.getY() > world.getHeight() / 2f) {
				bullets.removeIndex(i);
				data.light.remove();
				data.light.dispose();
			}
		}
	}
	
	private void doDamage(Light light, float delta) {
		// Damage the entities this light touches
		Array<Entity> entities = world.getEntities();
		for(int i = entities.size -1 ; i >= 0; i--) {
			Entity otherEntity = entities.get(i);
			
			if(EntityDefinitions.isDamagedByLight(otherEntity.getType())) {
				Vector2 pos = otherEntity.getBody().getPosition();
				if(light.contains(pos.x, pos.y)) {
					otherEntity.setHp(otherEntity.getHp() - delta * damage);
				}
			}
		}
	}

	private void fire() {
		Light light = new PointLight(rayHandler, RAYS_PER_BALL, Color.WHITE, pulseRadius, body.getPosition().x, body.getPosition().y);
		bullets.add(new PulseData(light, (body.getAngle() * MathUtils.radiansToDegrees) + 90, pulseSpeed));
	}

	@Override
	public void dispose() {
		for(PulseData data : bullets) {
			data.light.remove();
			data.light.dispose();
		}
		bullets.clear();
	}
}
