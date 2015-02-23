package com.runamuck.simulation;

import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.runamuck.data.EntityDefinitions;
import com.runamuck.simulation.actions.EntityAction;

public class Entity {
	
	protected Body body;
	protected float hp;
	protected Weapon weapon;
	protected EntityType type;
	protected SpectrumWorld world;
	
	protected Array<EntityAction> actions = new Array<EntityAction>();
	protected StateMachine<Entity> aiStateMachine;
	private float moveForce = 500;
	
	public Entity(SpectrumWorld world, Body body, EntityType type) {
		this.world = world;
		this.body = body;
		body.setUserData(this);
		this.type = type;
		this.hp = getMaxHP();
	}
	
	public Body getBody() {
		return body;
	}
	
	public float getMaxHP() {
		return EntityDefinitions.get(type).getMaxHP();
	}
	
	public float getHp() {
		return hp;
	}
	
	public void setHp(float hp) {
		float oldHP = this.hp;
		this.hp = Math.max(0, hp);
		if(oldHP > 0 && this.hp <= 0) {
			world.removeEntity(this);
		}
		
	}
	
	public SpectrumWorld getWorld() {
		return world;
	}
	
	public void onDeath() {
		
	}

	public void update(float delta) {
		if(aiStateMachine != null) {
			aiStateMachine.update();
		}
		
		if(actions.size > 0) {
			EntityAction currAction = actions.get(0);
			currAction.update(delta);
			if(currAction.isComplete()) {
				Pools.free(currAction);
				actions.removeIndex(0);
			}
		}
		
		if(weapon != null) {
			Array<Entity> entities = world.getEntities();
			for(int i = entities.size -1 ; i >= 0; i--) {
				Entity otherEntity = entities.get(i);
				
				if(EntityDefinitions.isDamagedByLight(otherEntity.getType())) {
					Vector2 pos = otherEntity.getBody().getPosition();
					if(weapon.getLight().contains(pos.x, pos.y)) {
						otherEntity.setHp(otherEntity.getHp() - delta * weapon.getDamage());
					}
				}
			}
//			weapon.getLight()
		}
	}

	public Weapon getWeapon() {
		return weapon;
	}

	public void setWeapon(Weapon light) {
		if(this.weapon != null) {
			this.weapon.getLight().remove();
			this.weapon.getLight().dispose();
		}
		this.weapon = light;
	}
	
	public EntityType getType() {
		return type;
	}

	public void dispose() {
		if(body != null) {
			body.getWorld().destroyBody(body);
		}
		body = null;
		
		if(weapon != null) {
			weapon.getLight().remove();
			weapon.getLight().dispose();
		}
		weapon = null;
	}

	public float getMoveForce() {
		return moveForce ;
	}
	
	public Array<EntityAction> getActions() {
		return actions;
	}
	
	public void addAction(EntityAction action) {
		this.actions.add(action);
	}
	
	public StateMachine<Entity> getState() {
		return aiStateMachine;
	}
	
	public void setAI(StateMachine<Entity> aiStateMachine) {
		this.aiStateMachine = aiStateMachine;
	}
}
