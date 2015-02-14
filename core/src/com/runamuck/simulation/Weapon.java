package com.runamuck.simulation;

import box2dLight.Light;

public class Weapon {
	private Light light;
	private float damage;
	
	public Weapon(Light light, float damage) {
		this.light = light;
		this.damage = damage;
	}
	
	public Light getLight() {
		return light;
	}
	
	public float getDamage() {
		return damage;
	}
}
