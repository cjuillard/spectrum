package com.runamuck.simulation.actions;

import com.badlogic.gdx.math.Vector2;
import com.runamuck.simulation.Entity;

public class FollowAction extends EntityAction {

	protected Entity followTarget;
	
	public FollowAction() {
		super();
	}
	
	protected void init(Entity source, Entity followTarget) {
		this.source = source;
		this.followTarget = followTarget;
	}
	private Vector2 tmp2 = new Vector2();
	
	@Override
	public void update(float elapsed) {
		super.update(elapsed);
		
		if(followTarget.getHp() <= 0) {
			markComplete();
			return;
		}
		tmp2.set(followTarget.getBody().getPosition()).sub(source.getBody().getPosition());
		tmp2.nor().scl(source.getMoveForce() * elapsed);
		source.getBody().applyForceToCenter(tmp2, true);
	}

	@Override
	public void reset() {
		super.reset();
		
		followTarget = null;
	}
}
