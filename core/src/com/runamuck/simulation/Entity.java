package com.runamuck.simulation;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.runamuck.data.EntityDefinitions;

public class Entity {
	
	protected Body body;
	protected float hp;
	protected Weapon weapon;
	protected EntityType type;
	protected SpectrumWorld world;
	
	public Entity(SpectrumWorld world, Body body, EntityType type) {
		this.world = world;
		this.body = body;
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
	
	public void onDeath() {
		
	}

	public void update(float delta) {
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
}
