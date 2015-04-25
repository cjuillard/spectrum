package com.runamuck.simulation;

import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.runamuck.data.EntityDefinitions;
import com.runamuck.simulation.actions.EntityAction;

public class Entity {
	
	protected Body body;
	protected float hp;
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
	}
	
	public EntityType getType() {
		return type;
	}

	public void dispose() {
		if(body != null) {
			body.getWorld().destroyBody(body);
		}
		body = null;
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
