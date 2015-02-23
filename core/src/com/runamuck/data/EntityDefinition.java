package com.runamuck.data;

import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.runamuck.simulation.Entity;


public abstract class EntityDefinition {
	protected float height;
	protected float width;
	protected float maxHP = 100;
	
	public EntityDefinition() {
		
	}

	public float getHeight() {
		return height;
	}

	public EntityDefinition setHeight(float height) {
		this.height = height;
		return this;
	}

	public float getWidth() {
		return width;
	}

	public EntityDefinition setWidth(float width) {
		this.width = width;
		return this;
	}
	
	public abstract Body createBody(World world);
	public abstract StateMachine<Entity> getAI(Entity source);
	
	public float getMaxHP() {
		return maxHP;
	}
	
	public EntityDefinition setMaxHP(float maxHP) {
		this.maxHP = maxHP;
		return this;
	}
}
