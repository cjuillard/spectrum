package com.runamuck.data;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.runamuck.simulation.EntityType;

public abstract class EntityDefinitions {
	private static Map<EntityType, EntityDefinition> definitions = new HashMap<EntityType,EntityDefinition>();
	static {
		EntityDefinition playerDef = new EntityDefinition() {

			@Override
			public Body createBody(World world) {
				CircleShape ballShape = new CircleShape();
				ballShape.setRadius(EntityDefinitions.get(EntityType.PLAYER).getWidth() / 2f);

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
				Body playerBody = world.createBody(circleBodyDef);
				playerBody.createFixture(def);

				ballShape.dispose();
				
				return playerBody;
			}
			
		};
		playerDef.setWidth(2)
		.setHeight(2);
		definitions.put(EntityType.PLAYER, playerDef);
		
		EntityDefinition enemy1Def = new EntityDefinition() {

			@Override
			public Body createBody(World world) {
				CircleShape ballShape = new CircleShape();
				ballShape.setRadius(EntityDefinitions.get(EntityType.ENEMY1).getWidth() / 2f);

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
				Body playerBody = world.createBody(circleBodyDef);
				playerBody.createFixture(def);

				ballShape.dispose();
				
				return playerBody;
			}
			
		};
		enemy1Def.setWidth(1)
		.setHeight(1);
		definitions.put(EntityType.ENEMY1, enemy1Def);
		

	}
	
	public static EntityDefinition get(EntityType type) {
		return definitions.get(type);
	}

	public static boolean isDamagedByLight(EntityType type) {
		switch(type) {
		case PLAYER:
			return false;
		default:
			return true;
		}
	}
}
