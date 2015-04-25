package com.runamuck.simulation;

import box2dLight.ChainLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.runamuck.data.EntityDefinition;
import com.runamuck.data.EntityDefinitions;

public class RayWeapon extends Weapon {

	public static final int RAYS_PER_BALL = 128;
	public static final float LIGHT_DISTANCE = 20f;
	public static final float RADIUS = 1f;
	private ChainLight light;
	
	public RayWeapon(SpectrumWorld world, RayHandler rayHandler, EntityDefinition def, Body body) {
		super(world);
		float startPos = def.getHeight() / 2f;
		this.light = new ChainLight(
				rayHandler, RAYS_PER_BALL, null, LIGHT_DISTANCE*3, 1,
				new float[]{-2, -startPos, 0, -startPos, 2, -startPos});
		light.attachToBody(body);
//		PointLight light = new PointLight(
//				rayHandler, RAYS_PER_BALL, null, LIGHT_DISTANCE, 0f, 0f);
//		light.attachToBody(playerEntity.getBody(), RADIUS / 2f, RADIUS / 2f);
		light.setColor(
				1f,
				0,
				0,
				1f);
		
		this.damage = 100f;	// default damage
	}
	
	@Override
	public void update(float delta) {
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

	@Override
	public void dispose() {
		if(light != null) {
			light.remove();
			light.dispose();
			light = null;
		}
	}

}
