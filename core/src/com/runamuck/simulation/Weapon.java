package com.runamuck.simulation;


public abstract class Weapon {
	
	protected SpectrumWorld world;
	protected float damage;
	
	public Weapon(SpectrumWorld world) {
		this.world = world;
	}
	
	abstract public void dispose();

	abstract public void update(float delta);

	public float getDamage() {
		return damage;
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}
}
