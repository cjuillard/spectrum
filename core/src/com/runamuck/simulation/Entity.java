package com.runamuck.simulation;

import box2dLight.Light;

import com.badlogic.gdx.physics.box2d.Body;

public class Entity {
	
	protected Body body;
	protected float hp;
	private Light weapon;
	
	public Entity(Body body) {
		this.body = body;
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
	
	

}
