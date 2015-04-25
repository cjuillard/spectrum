package com.runamuck.simulation;

import com.badlogic.gdx.physics.box2d.Body;

public class ShipEntity extends Entity {

	protected Weapon weapon;
	
	public ShipEntity(SpectrumWorld world, Body body, EntityType type) {
		super(world, body, type);
	}

	public Weapon getWeapon() {
		return weapon;
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		
		if(weapon != null) {
			weapon.update(delta);
		}
	}
	
	public void setWeapon(Weapon light) {
		if(this.weapon != null) {
			weapon.dispose();
		}
		this.weapon = light;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		if(weapon != null) {
			weapon.dispose();
		}
		weapon = null;
	}
}
