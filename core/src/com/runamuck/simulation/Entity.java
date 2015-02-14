package com.runamuck.simulation;

import box2dLight.Light;

import com.badlogic.gdx.physics.box2d.Body;

public class Entity {
	
	protected Body body;
	protected float hp;
	protected Light weapon;
	protected EntityType type;
	
	public Entity(Body body, EntityType type) {
		this.body = body;
		this.type = type;
	}
	
	public Body getBody() {
		return body;
	}
	
	public float getHp() {
		return hp;
	}
	
	public void setHp(float hp) {
		this.hp = hp;
	}

	public void update(float delta) {
		
	}

	public Light getWeapon() {
		return weapon;
	}

	public void setWeapon(Light light) {
		if(this.weapon != null) {
			this.weapon.remove();
			this.weapon.dispose();
		}
		this.weapon = light;
	}
	
	public EntityType getType() {
		return type;
	}
}
